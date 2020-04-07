package com.tanghao.flink.demo;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.formats.json.JsonNodeDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @Author： Canthny
 * @Description： 获取支付信息日志流计算job
 * @Date： Created in 2018/9/13 2:06
 */
public class GetPayInfoJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.8.145:9082");


//        DataStream<String> stream = env.addSource(new FlinkKafkaConsumer011<String>("20180913", new SimpleStringSchema(), properties));
        SingleOutputStreamOperator<ObjectNode> stream= env.addSource(new FlinkKafkaConsumer011("CountPerHourByAccountNo2", new JsonNodeDeserializationSchema(), properties));
//        SingleOutputStreamOperator<Tuple2<String, Long>> stream = env.addSource(new FlinkKafkaConsumer011("20180913", new JsonNodeDeserializationSchema(), properties));

         SingleOutputStreamOperator<ObjectNode> objectNodeOperator = stream.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<ObjectNode>(Time.seconds(15)) {
            @Override
            public long extractTimestamp(ObjectNode element) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date eventTime = null;
                try {
                    eventTime = format.parse(element.get("eventTime").asText());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return eventTime.getTime();
            }
        });

        KeyedStream<Tuple2<String, Long>, Tuple> keyBy = objectNodeOperator.map(new MapFunction<ObjectNode, Tuple2<String, Long>>() {
            public Tuple2<String, Long> map(ObjectNode jsonNodes) throws Exception {
                System.out.println(jsonNodes.get("accountId").asText() + "==map====" + jsonNodes.get("amount").asLong());
                return new Tuple2<String, Long>(jsonNodes.get("accountId").asText(), jsonNodes.get("amount").asLong());
            }
        }).keyBy(0);

        testTumblingEventTimeWindows(keyBy);

//        stream.keyBy(0).timeWindow(Time.minutes(1)).sum(1);
        stream.print();
        env.execute();
    }

    private static void testTumblingEventTimeWindows(KeyedStream<Tuple2<String, Long>, Tuple> keyBy) {
        keyBy.window(TumblingEventTimeWindows.of(Time.seconds(5))).reduce(new ReduceFunction<Tuple2<String, Long>>()
        {
            public Tuple2<String, Long> reduce(Tuple2<String, Long> v1, Tuple2<String, Long> v2) {
                Tuple2<String, Long> test = new Tuple2<String, Long>(v1.f0, v1.f1 + v2.f1);
                System.out.println(test);
                return test;
            }
        });
    }
}
