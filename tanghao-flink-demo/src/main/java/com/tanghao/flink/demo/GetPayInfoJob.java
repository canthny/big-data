package com.tanghao.flink.demo;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

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
        properties.setProperty("bootstrap.servers", "192.168.0.102:9092");
        properties.setProperty("group.id", "flink_consumer");


        DataStream<String> stream = env
                .addSource(new FlinkKafkaConsumer011<String>("flink-demo", new SimpleStringSchema(), properties));
        stream.print();
        env.execute();
    }
}
