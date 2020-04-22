package com.tanghao.flink.demo.api.window;

import com.alibaba.fastjson.JSONObject;
import com.tanghao.flink.demo.common.ConstantKafka;
import com.tanghao.flink.demo.common.Message;
import com.tanghao.flink.demo.common.UserConsumerAmountPerMinute;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.aggregation.AggregationFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description： 滑动窗口场景——每5s刷新一次最近1分钟消费总金额用户排名top3
 * Created By tanghao on 2020/4/7
 */
public class SlidingWindowTest {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.8.145:9082");

        SingleOutputStreamOperator<Message> messageStream= env.addSource(new FlinkKafkaConsumer011(ConstantKafka.TRADE_MESSAGE_TOPIC, new SimpleStringSchema(), properties)).map(new MapFunction<String, Message>() {
            public Message map(String s) throws Exception {
                return JSONObject.parseObject(s,Message.class);
            }
        });

        SingleOutputStreamOperator<List<UserConsumerAmountPerMinute>> result = messageStream.filter(new FilterFunction<Message>() {
            public boolean filter(Message value) throws Exception {
                if(value.getTransCode().equals("3001")){
                    return true;
                }else{
                    return false;
                }
            }
        }).keyBy(new KeySelector<Message, String>() {
            public String getKey(Message value) throws Exception {
                return value.getTransCode();
            }
        }).timeWindow(Time.minutes(1),Time.seconds(5)).aggregate(new AggregateFunction<Message, Map<Long,UserConsumerAmountPerMinute>, List<UserConsumerAmountPerMinute>>() {
            public Map<Long, UserConsumerAmountPerMinute> createAccumulator() {
                return new HashMap<Long, UserConsumerAmountPerMinute>();
            }

            public Map<Long, UserConsumerAmountPerMinute> add(Message value, Map<Long, UserConsumerAmountPerMinute> accumulator) {
                UserConsumerAmountPerMinute userConsumerAmountPerMinute = accumulator.get(value.getUserId());
                if(null == userConsumerAmountPerMinute){
                    userConsumerAmountPerMinute = new UserConsumerAmountPerMinute();
                    DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm");
                    String currentMinute = format.format(new Date());
                    userConsumerAmountPerMinute.setAmount(value.getAmount());
                    userConsumerAmountPerMinute.setUserId(value.getUserId());
                    userConsumerAmountPerMinute.setCurrentMinute(currentMinute);
                    accumulator.put(value.getUserId(),userConsumerAmountPerMinute);
                }else{
                    userConsumerAmountPerMinute.setAmount(userConsumerAmountPerMinute.getAmount()+value.getAmount());
                }
                return accumulator;
            }

            public List<UserConsumerAmountPerMinute> getResult(Map<Long, UserConsumerAmountPerMinute> accumulator) {
                List<UserConsumerAmountPerMinute> list = new ArrayList<UserConsumerAmountPerMinute>(accumulator.values());
                list.sort(new UserConsumerAmountPerMinuteComparator());
                return list;
            }

            public Map<Long, UserConsumerAmountPerMinute> merge(Map<Long, UserConsumerAmountPerMinute> a, Map<Long, UserConsumerAmountPerMinute> b) {
                return null;
            }
        });

        result.print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class UserConsumerAmountPerMinuteComparator implements Comparator<UserConsumerAmountPerMinute>{
        public int compare(UserConsumerAmountPerMinute o1, UserConsumerAmountPerMinute o2) {
            return o1.getAmount().compareTo(o2.getAmount());
        }
    }
}
