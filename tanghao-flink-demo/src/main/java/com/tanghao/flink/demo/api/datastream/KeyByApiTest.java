package com.tanghao.flink.demo.api.datastream;

import com.tanghao.flink.demo.common.Message;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * Description： KeyByApi测试类
 * Created By tanghao on 2020/4/1
 */
public class KeyByApiTest {

    public static void main(String[] args) {
        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        List<Message> list = new ArrayList<Message>();
        list.add(new Message("1","deposit",30L));
        list.add(new Message("2","withdraw",21L));
        list.add(new Message("3","withdraw",65L));
        list.add(new Message("4","deposit",1L));
        list.add(new Message("5","withdraw",21L));
        DataStreamSource<Message> streamSource = streamExecutionEnvironment.fromCollection(list);
        KeyedStream<Message, String> keyedStream= streamSource.keyBy(new KeySelector<Message, String>() {
            public String getKey(Message message) throws Exception {
                return message.getType();
            }
        });

        /** 根据类型统计每种类型的交易金额总和 */
        SingleOutputStreamOperator<Message> reduceResult = keyedStream.reduce(new ReduceFunction<Message>() {
            public Message reduce(Message message, Message message2) throws Exception {
                Long sum = message.getAmount() + message2.getAmount();
                String id = message.getId() + "_" +message2.getId();
                return new Message(id,message.getType(),sum);
            }
        });

        reduceResult.print();

        /** 聚合函数，滚动输出当前key下最新的一条聚合操作后的记录 */
//        keyedStream.sum("amount").print();
        keyedStream.max("amount").print();
//        keyedStream.maxBy("amount").print();
//        keyedStream.min("amount").print();
//        keyedStream.minBy("amount").print();

        try {
            streamExecutionEnvironment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
