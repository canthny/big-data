package com.tanghao.flink.demo.api.window;

import com.alibaba.fastjson.JSONObject;
import com.tanghao.flink.demo.common.ConstantKafka;
import com.tanghao.flink.demo.common.Message;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.util.Properties;

/**
 * Description： 使用事件时间
 * Created By tanghao on 2020/4/9
 */
public class TumblingWindowUseEventTimeTest {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.8.145:9082");
        SingleOutputStreamOperator<Message> messageStream= env.addSource(new FlinkKafkaConsumer011(ConstantKafka.TRADE_MESSAGE_TOPIC, new SimpleStringSchema(), properties)).map(new MapFunction<String, Message>() {
            public Message map(String s) throws Exception {
                return JSONObject.parseObject(s,Message.class);
            }
        }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Message>() {
            @Override
            public long extractAscendingTimestamp(Message element) {
                return element.getTradeTime().getTime();
            }
        });

        SingleOutputStreamOperator res = messageStream.keyBy(new KeySelector<Message, String>() {
            public String getKey(Message message) throws Exception {
                return message.getTransCode();
            }
        }).window(TumblingEventTimeWindows.of(Time.minutes(1))).sum("amount");
        res.print();
        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
