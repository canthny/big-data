package com.tanghao.flink.demo.api.window;

import com.alibaba.fastjson.JSONObject;
import com.tanghao.flink.demo.common.ConstantKafka;
import com.tanghao.flink.demo.common.Message;
import com.tanghao.flink.demo.common.TradeCountMinuteResult;
import com.tanghao.flink.demo.common.UserConsumerAmountPerMinute;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
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
 * 场景：根据transCode统计每分钟充值、提现、消费等各类型交易总金额；并且根据userId统计用户分钟消费金额。
 * Created By tanghao on 2020/4/7
 */
public class TumblingWindowProcessWindowFunctionTest {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.8.145:9082");

        SingleOutputStreamOperator<Message> messageStream= env.addSource(new FlinkKafkaConsumer011(ConstantKafka.TRADE_MESSAGE_TOPIC, new SimpleStringSchema(), properties)).map(new MapFunction<String, Message>() {
            public Message map(String s) throws Exception {
                return JSONObject.parseObject(s,Message.class);
            }
        });

        //用户分钟消费额子流标记
        final OutputTag<UserConsumerAmountPerMinute> userConsumerAmountPerMin = new OutputTag<UserConsumerAmountPerMinute>("side-output-user-consumer-amount-per-min"){};

        //按交易类型分区
        SingleOutputStreamOperator res = messageStream.keyBy(new KeySelector<Message, String>() {
            public String getKey(Message message) throws Exception {
                return message.getTransCode();
            }
        }).timeWindow(Time.minutes(1)).process(new ProcessWindowFunction<Message, TradeCountMinuteResult, String ,TimeWindow>() {
            public void process(String s, Context context, Iterable<Message> elements, Collector<TradeCountMinuteResult> out) throws Exception {
                DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm");
                String currentMinute = format.format(new Date());
                Long sum = 0L;
                Map<Long,Long> userConsumerAmountPerMinMap = new HashMap<Long, Long>();
                for(Message message:elements){
                    /** 之前已经keyBy根据交易类型分区过，每个分区都是统一类型的交易数据，直接累加即可 */
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
                /** 主数据流输出各类型交易额汇总信息*/
                TradeCountMinuteResult tradeCountMinuteResult = new TradeCountMinuteResult();
                tradeCountMinuteResult.setTransCode(s);
                tradeCountMinuteResult.setSumAmount(sum);
                tradeCountMinuteResult.setCurrentMinute(currentMinute);
                out.collect(tradeCountMinuteResult);
                /** 用户分钟消费额统计，输出到子数据流中。可以用于第1分钟消费金额top3用户统计，交给redis排序即可 */
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
        System.out.println("执行计划:"+env.getExecutionPlan());
        res.getSideOutput(userConsumerAmountPerMin).print();
        /**
         * 输出结果：各类型分钟交易总额，用户分钟消费总额
         * 4> TradeCountMinuteResult{transCode='1001', sumAmount=4998, currentMinute='2020-04-09 15:52'}
         * 3> TradeCountMinuteResult{transCode='3001', sumAmount=4023, currentMinute='2020-04-09 15:52'}
         * 6> TradeCountMinuteResult{transCode='2001', sumAmount=6190, currentMinute='2020-04-09 15:52'}
         * 3> UserConsumerAmountPerMinute{userId=754, amount=599, currentMinute='2020-04-09 15:52'}
         * 3> UserConsumerAmountPerMinute{userId=578, amount=725, currentMinute='2020-04-09 15:52'}
         * 3> UserConsumerAmountPerMinute{userId=931, amount=622, currentMinute='2020-04-09 15:52'}
         */
        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
