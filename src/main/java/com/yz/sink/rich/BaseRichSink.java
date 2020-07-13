package com.yz.sink.rich;

import com.esotericsoftware.minlog.Log;
import com.yz.pojo.WordDto;
import com.yz.sink.DBConnectUtil;
import com.yz.util.DateUtils;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.CheckpointListener;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

public abstract class BaseRichSink<T extends WordDto> extends RichSinkFunction<Tuple2<Boolean,T>> implements CheckpointedFunction, CheckpointListener {

    private Connection connection;
    private static final Logger log = LoggerFactory.getLogger(BaseRichSink.class);
    private static transient volatile BroadcastState<String, Timestamp> mapState;
    private int a;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        connection = DBConnectUtil.getConnection(true);
    }

    @Override
    public void invoke(Tuple2<Boolean, T> value, Context context) throws Exception {
        if (!value.f0) {
            return;
        }
        T wordDto = value.f1;
        PreparedStatement ps = null;
        Timestamp oldTime = mapState.get(wordDto.getWord());
        Timestamp dealTime = wordDto.getDealTime();
        // 此处还可以优化 要减小时间颗粒度 去除毫秒差异
        if (oldTime != null && new Date(oldTime.getTime()).getTime() >= new Date(dealTime.getTime()).getTime()) {
            return;
        }
        String sql = "insert into `t_test` (`value`,`total`,`insert_time`) values (?,?,?)";
        ps = connection.prepareStatement(sql);
        ps.setString(1, wordDto.getWord());
        ps.setInt(2, wordDto.getCt());
        ps.setTimestamp(3, wordDto.getDealTime());
        otherFieldConfirm(wordDto,ps,4);
        ps.execute();
        //添加时间到节点中
        Log.info("将开始时间放入 ：" + DateUtils.formatDateTime(wordDto.getDealTime()));
        mapState.put(wordDto.getWord(), wordDto.getDealTime());
    }


    protected abstract void otherFieldConfirm(T mode ,PreparedStatement ps,int next);


    /**
     * 关闭连接
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * checkpoint 成功后的回调
     *
     * @param l
     * @throws Exception
     */
    @Override
    public void notifyCheckpointComplete(long l) throws Exception {
        log.info("sink 完成 checkpoint " + l);
    }


    /**
     * @param context
     * @throws Exception
     */
    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        log.info("sink  snapshot");
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        MapStateDescriptor<String, Timestamp> descriptor = new MapStateDescriptor<>("SINK_OFFSET", String.class, Timestamp.class);
        mapState = context.getOperatorStateStore().getBroadcastState(descriptor);
        if (context.isRestored()) {
            connection = DBConnectUtil.getConnection(true);
        }
    }
}