/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yz;

import com.yz.pojo.WordDto;
import com.yz.sink.rich.BaseRichSink;
import com.yz.sink.rich.CpcRichSink;
import com.yz.source.WordSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="https://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class StreamingJob {

	public static void main(String[] args) throws Exception {
		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		//设置并行度,为了方便测试，查看消息的顺序，这里设置为1，可以更改为多并行度
		env.setParallelism(1);
		//checkpoint的设置
		//每隔10s进行启动一个检查点【设置checkpoint的周期】
		env.enableCheckpointing(20000);
		//设置模式为：exactly_one，仅一次语义
		env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
		//确保检查点之间有1s的时间间隔【checkpoint最小间隔】
		env.getCheckpointConfig().setMinPauseBetweenCheckpoints(1000);
		//检查点必须在10s之内完成，或者被丢弃【checkpoint超时时间】
		env.getCheckpointConfig().setCheckpointTimeout(10000);
		//同一时间只允许进行一次检查点
		env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
		//表示一旦Flink程序被cancel后，会保留checkpoint数据，以便根据实际需要恢复到指定的checkpoint
		env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

		DataStream<WordDto> streamSource = env.addSource(new WordSource());
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);



		StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
		tableEnv.registerDataStream("WordCount", streamSource, "word, ct, rowtime.rowtime");
		// run a SQL query on the Table and retrieve the result as a new Table
		Table result = tableEnv.sqlQuery(
				"SELECT word, SUM(ct) as ct ,TUMBLE_START(rowtime, INTERVAL '10' SECOND) as dealTime FROM WordCount GROUP BY word,TUMBLE(rowtime, INTERVAL '10' SECOND)");
//		Table result = tableEnv.sqlQuery(
//				"SELECT word, ct as ct ,rowtime as dealTime FROM WordCount ");


		DataStream<Tuple2<Boolean, WordDto>> stream = tableEnv.toRetractStream(result, WordDto.class);

//		stream.print();
		stream.addSink(new CpcRichSink()).name("BaseRichSink");

        env.execute("Flink Streaming Java API Skeleton");
	}
}
