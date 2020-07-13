package com.yz;

import com.yz.pojo.ControlLoop;
import com.yz.util.DateUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @author yz
 * @Description
 * @create 2020-06-30 15:59
 */
public class LocalTextJob {
    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 非常关键，一定要设置启动检查点！！
        env.enableCheckpointing(5000);
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStream<String> text = env.readTextFile("E:\\IdeaProject\\Flink\\flink-1.10-test\\src\\main\\resources\\04TRC01048.txt");

        DataStream stream = text.flatMap(new LineMapper());

        //stream.addSink(new SinkTest());
        stream.print();
        stream.writeAsText("E:\\IdeaProject\\Flink\\flink-1.10-test\\src\\main\\resources\\04TRC01040.txt");
        // 将结果打印到控制台，注意这里使用的是单线程打印，而非多线程
        //env.setParallelism(1);

        env.execute("WordCount from Kafka data");


    }

    public static final class LineMapper implements FlatMapFunction<String, ControlLoop> {

        private ControlLoop controlLoop = new ControlLoop();
        private Calendar calendar = Calendar.getInstance();

        @Override
        public void flatMap(String s, Collector<ControlLoop> out) throws Exception {
            String[] split = s.split("\t");
            calendar.add(Calendar.MINUTE,1);
            controlLoop.setDealTime(new Timestamp(calendar.getTime().getTime()));
            controlLoop.setMvValue(Double.parseDouble(split[1]));
            controlLoop.setPvValue(Double.parseDouble(split[2]));
            controlLoop.setSpValue(Double.parseDouble(split[3]));
            controlLoop.setModeValue(Double.parseDouble(split[4]));
            out.collect(controlLoop);
        }
    }
}


