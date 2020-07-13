package com.yz.source;

import com.esotericsoftware.minlog.Log;
import com.yz.pojo.WordDto;
import com.yz.util.DateUtils;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.OperatorStateStore;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.CheckpointListener;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * @author yz
 * @Description 数据源
 * @create 2020-06-12 13:59
 */
public class WordSource extends RichSourceFunction<WordDto> implements CheckpointedFunction, CheckpointListener {
    Logger LOG = LoggerFactory.getLogger(WordSource.class);
    private Calendar calendar;

    private transient volatile boolean isRunning;
    private transient volatile ListState<String> offsetStates;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.isRunning = true;
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, -1);
    }

    /**
     * checkpoint生成快照
     *
     * @param context
     * @throws Exception
     */
    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        //进行延迟处理
        offsetStates.clear();
        String formatDateTime = DateUtils.formatDateTime(calendar.getTime());
        Log.info("将开始时间放入 ：" + formatDateTime);
        offsetStates.add(formatDateTime);
    }


    /**
     * 初始化 和 重启
     *
     * @param context
     * @throws Exception
     */
    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        OperatorStateStore stateStore = context.getOperatorStateStore();
        offsetStates = stateStore.getListState(new ListStateDescriptor<>("SOURCE_OFFSET", String.class));
        if (context.isRestored()) {
            Iterator<String> iterator = offsetStates.get().iterator();
            if (iterator.hasNext()) {
                calendar = Calendar.getInstance();
                String dateStr = iterator.next();
                Log.info("重启Source 获取offset " + dateStr);
                Date tmp = DateUtils.parseDate(dateStr);
                calendar.setTime(tmp);
            }
        }
    }

    @Override
    public void run(SourceContext<WordDto> sourceContext) throws Exception {
        //采集数据
        while (isRunning) {
            //记录采集过的数据
            WordDto wordDto = new WordDto();
            wordDto.setWord("key");
            wordDto.setCt(1);
            synchronized (sourceContext.getCheckpointLock()) {
                sourceContext.collectWithTimestamp(wordDto, calendar.getTime().getTime());
                sourceContext.emitWatermark(new Watermark(calendar.getTime().getTime() - 100));
            }
            calendar.add(Calendar.SECOND, 1);
            //降速拉取数据
            Thread.sleep(300);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }

    @Override
    public void notifyCheckpointComplete(long l) throws Exception {
        LOG.info("source checkpoint结束 " + l);
    }
}
