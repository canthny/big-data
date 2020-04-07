package com.tanghao.flink.demo.source;

import com.tanghao.flink.demo.common.ConstantKafka;
import com.tanghao.flink.demo.common.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Description： kafka生成交易消息
 * Created By tanghao on 2020/4/7
 */
public class MessageKafkaProducerTest {

    private static void main(){
        Properties pro = new Properties();
        pro.setProperty("bootstrap.servers", "192.168.18.132:9092");
        pro.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        pro.setProperty("value.serializer", Message.class.getName());
        KafkaProducer<String, Object> producer = new KafkaProducer<String, Object>(pro);
        producer.send(new ProducerRecord<String, Object>(ConstantKafka.TRADE_MESSAGE_TOPIC,new Message("withdraw",101L)));
        producer.close();
    }
}
