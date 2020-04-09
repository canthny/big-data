package com.tanghao.flink.demo.api.window;

import com.alibaba.fastjson.JSONObject;
import com.tanghao.flink.demo.common.ConstantKafka;
import com.tanghao.flink.demo.common.Message;
import com.tanghao.flink.demo.common.TradeCountMinuteResult;
import com.tanghao.flink.demo.common.UserConsumerAmountPerMinute;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description： 滚动窗口测试
 * Created By tanghao on 2020/4/7
 */
public class TumblingWindowTest {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.8.145:9082");

        SingleOutputStreamOperator<Message> messageStream= env.addSource(new FlinkKafkaConsumer011(ConstantKafka.TRADE_MESSAGE_TOPIC, new SimpleStringSchema(), properties)).map(new MapFunction<String, Message>() {
            public Message map(String s) throws Exception {
                return JSONObject.parseObject(s,Message.class);
            }
        });

        final OutputTag<UserConsumerAmountPerMinute> userConsumerAmountPerMin = new OutputTag<UserConsumerAmountPerMinute>("side-output-user-consumer-amount-per-min"){};


        SingleOutputStreamOperator res = messageStream.keyBy(new KeySelector<Message, String>() {
            public String getKey(Message message) throws Exception {
                return message.getTransCode();
            }
        }).timeWindow(Time.minutes(1)).process(new ProcessWindowFunction<Message, TradeCountMinuteResult, String ,TimeWindow>() {
            public void process(String s, Context context, Iterable<Message> elements, Collector<TradeCountMinuteResult> out) throws Exception {
                TradeCountMinuteResult tradeCountMinuteResult = new TradeCountMinuteResult();
                tradeCountMinuteResult.setTransCode(s);
                DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm");
                String currentMinute = format.format(new Date());
                tradeCountMinuteResult.setCurrentMinute(currentMinute);
                Long sum = 0L;
                Map<Long,Long> userConsumerAmountPerMinMap = new HashMap<Long, Long>();
                for(Message message:elements){
                    sum += message.getAmount();
                    /** 若交易类型是消费，则要统计用户该分钟内的交易总额，输出到用户维度的子流中 */
                    if(message.getTransCode().equals("3001")){
                        if(null==userConsumerAmountPerMinMap.get(message.getUserId())){
                            userConsumerAmountPerMinMap.put(message.getUserId(),message.getAmount());
                        }else{
                            long temp = userConsumerAmountPerMinMap.get(message.getUserId());
                            temp += message.getAmount();
                            userConsumerAmountPerMinMap.put(message.getUserId(),temp);
                        }
                    }
                }
                tradeCountMinuteResult.setSumAmount(sum);
                out.collect(tradeCountMinuteResult);
                for (Map.Entry<Long, Long> m : userConsumerAmountPerMinMap.entrySet()) {
                    Long userId = m.getKey();
                    Long amount = m.getValue();
                    UserConsumerAmountPerMinute userConsumerAmountPerMinute = new UserConsumerAmountPerMinute();
                    userConsumerAmountPerMinute.setAmount(amount);
                    userConsumerAmountPerMinute.setUserId(userId);
                    userConsumerAmountPerMinute.setCurrentMinute(currentMinute);
                    context.output(userConsumerAmountPerMin,userConsumerAmountPerMinute);
                }
            }
        });
        res.print();
        res.getSideOutput(userConsumerAmountPerMin).print();
        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
