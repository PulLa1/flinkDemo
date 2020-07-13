package com.yz.sink.twicepc;

import com.yz.pojo.WordDto;
import com.yz.sink.DBConnectUtil;
import org.apache.flink.api.common.typeutils.base.VoidSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.sink.TwoPhaseCommitSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySqlTwoPhaseCommitSink extends TwoPhaseCommitSinkFunction<Tuple2<Boolean,WordDto>,Connection,Void> {

    private static final Logger log = LoggerFactory.getLogger(MySqlTwoPhaseCommitSink.class);


    public MySqlTwoPhaseCommitSink(){
        super(DBConnectUtil.ConnectionSerialize(), VoidSerializer.INSTANCE);
    }

    /**
     * 执行数据库入库操作  task初始化的时候调用
     * @param connection
     * @param tp
     * @param context
     * @throws Exception
     */
    @Override
    protected void invoke(Connection connection, Tuple2<Boolean,WordDto> tp, Context context) throws Exception {
        if(!tp.f0){
            return;
        }

        WordDto wordDto = tp.f1;
        String sql = "insert into `t_test` (`value`,`total`,`insert_time`) values (?,?,?)";
        log.info("====执行SQL:{}===",sql);
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, wordDto.getWord());
        ps.setInt(2, wordDto.getCt());
        ps.setTimestamp(3, wordDto.getDealTime());
        //执行insert语句
        ps.execute();
    }

    /**
     * 获取连接，开启手动提交事物（getConnection方法中）
     * @return
     * @throws Exception
     */
    @Override
    protected Connection beginTransaction() throws Exception {
        log.info("start beginTransaction.......");
        return DBConnectUtil.getConnection(false);
    }

    /**
     *预提交，这里预提交的逻辑在invoke方法中
     * @param connection
     * @throws Exception
     */
    @Override
    protected void preCommit(Connection connection) throws Exception {
        log.info("start preCommit...");
    }

    /**
     * 如果invoke方法执行正常，则提交事务
     * @param connection
     */
    @Override
    protected void commit(Connection connection) {
        log.info("start commit...");
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果invoke执行异常则回滚事物，下一次的checkpoint操作也不会执行
     * @param connection
     */
    @Override
    protected void abort(Connection connection) {
        log.info("start abort rollback...");
        try {
            connection.rollback();
            connection.close();
        } catch (SQLException e) {
           e.printStackTrace();
        }
    }
}