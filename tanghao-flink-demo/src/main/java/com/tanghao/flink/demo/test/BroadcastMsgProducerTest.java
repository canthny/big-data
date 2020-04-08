package com.tanghao.flink.demo.test;

import com.alibaba.fastjson.JSONObject;
import com.tanghao.flink.demo.common.ConstantKafka;
import com.tanghao.flink.demo.common.Message;
import com.tanghao.flink.demo.common.TransTypeInfo;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Description： kafka生成交易消息
 * Created By tanghao on 2020/4/7
 */
public class BroadcastMsgProducerTest {

    public static void main(String[] args) throws InterruptedException {
        Properties pro = new Properties();
        pro.setProperty("bootstrap.servers", "192.168.8.145:9082");
        pro.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        pro.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(pro);
        sendTransTypeInfo(producer);
        Thread.sleep(300);//更新广播表有个时间间隔
        sendMessage(producer);
        producer.close();
    }

    /**
     * 发送维表配置的交易类型信息
     * isOpen字段标识开关，如果打开可以在接收的消息中根据transCode去TRANS_TYPE_MAP得到transName
     * @param producer
     */
    private static void sendTransTypeInfo(KafkaProducer<String, String> producer){
        TransTypeInfo transTypeInfo = new TransTypeInfo();
        transTypeInfo.setOpen(true);
        transTypeInfo.setTransCode("2001");
        transTypeInfo.setTransName("提现");
        producer.send(new ProducerRecord<String, String>(ConstantKafka.TRANS_TYPE_TOPIC, JSONObject.toJSONString(transTypeInfo)));
    }

    private static void sendMessage(KafkaProducer<String, String> producer){
        Message msg = new Message("2001",101L);
        producer.send(new ProducerRecord<String, String>(ConstantKafka.TRADE_MESSAGE_TOPIC, JSONObject.toJSONString(msg)));
    }
}
