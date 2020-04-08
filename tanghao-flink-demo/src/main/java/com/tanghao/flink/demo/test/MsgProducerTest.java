package com.tanghao.flink.demo.test;

import com.alibaba.fastjson.JSONObject;
import com.tanghao.flink.demo.common.ConstantKafka;
import com.tanghao.flink.demo.common.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Description： 消息生成器
 * Created By tanghao on 2020/4/8
 */
public class MsgProducerTest {
    private static final String[] transCodes = {"1001","2001","3001"};

    public static void main(String[] args) throws InterruptedException {
        Properties pro = new Properties();
        pro.setProperty("bootstrap.servers", "192.168.8.145:9082");
        pro.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        pro.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(pro);
        for(int i=0;i<30;i++){
            Message msg = new Message(transCodes[i%3],(long)new Random().nextInt(1000));
            System.out.println(msg);
            producer.send(new ProducerRecord<String, String>(ConstantKafka.TRADE_MESSAGE_TOPIC, JSONObject.toJSONString(msg)));
            Thread.sleep(500);
        }
        producer.close();
    }
}
