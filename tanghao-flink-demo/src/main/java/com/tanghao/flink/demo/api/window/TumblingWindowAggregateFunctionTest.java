package com.tanghao.flink.demo.api.window;

import com.alibaba.fastjson.JSONObject;
import com.tanghao.flink.demo.common.ConstantKafka;
import com.tanghao.flink.demo.common.Message;
import com.tanghao.flink.demo.common.TradeCountMinuteResult;
import com.tanghao.flink.demo.common.UserConsumerAmountPerMinute;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description： 滚动窗口测试
 * 场景：根据transCode统计每分钟充值、提现、消费等各类型交易总金额；并且根据userId统计用户分钟消费金额。
 * Created By tanghao on 2020/4/7
 */
public class TumblingWindowAggregateFunctionTest {

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
        }).timeWindow(Time.minutes(1)).aggregate(new MySumAggregate());
        res.print();
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

    /**
     * 这里禁止使用RichFunction,暂时不清楚为何这样设计，说是这样使用状态不正确
     */
    private static class MySumAggregate extends RichAggregateFunction<Message,TradeCountMinuteResult,TradeCountMinuteResult> {

        private transient MapState<Long,Long> firstMinUserConsumerAmountMap;
        private transient ValueState<Long> totalTradeAmountValue;

        @Override
        public void open(Configuration parameters) throws Exception {
            MapStateDescriptor<Long,Long> firstMinUserConsumerAmount = new MapStateDescriptor<Long, Long>("firstMinUserConsumerAmount", BasicTypeInfo.LONG_TYPE_INFO,BasicTypeInfo.LONG_TYPE_INFO);
            StateTtlConfig ttlConfig = StateTtlConfig
                    .newBuilder(org.apache.flink.api.common.time.Time.seconds(1))
                    .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                    .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                    .build();
            firstMinUserConsumerAmount.enableTimeToLive(ttlConfig);
            firstMinUserConsumerAmountMap = getRuntimeContext().getMapState(firstMinUserConsumerAmount);

            ValueStateDescriptor<Long> totalTradeAmount = new ValueStateDescriptor<Long>("totalTradeAmount", BasicTypeInfo.LONG_TYPE_INFO);
            StateTtlConfig ttlConfig2 = StateTtlConfig
                    .newBuilder(org.apache.flink.api.common.time.Time.seconds(1))
                    .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                    .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                    .build();
            totalTradeAmount.enableTimeToLive(ttlConfig2);
            totalTradeAmountValue = getRuntimeContext().getState(totalTradeAmount);
        }

        public TradeCountMinuteResult createAccumulator() {
            return new TradeCountMinuteResult();
        }

        public TradeCountMinuteResult add(Message value, TradeCountMinuteResult accumulator) {
            DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm");
            String currentMinute = format.format(new Date());
            if(currentMinute.equals("2020-04-10 17:42")){
                Long userId = value.getUserId();
                Long userSum = null;
                try {
                    userSum = firstMinUserConsumerAmountMap.get(userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(null==userSum){
                    userSum = value.getAmount();
                }else{
                    userSum += value.getAmount();
                }
                try {
                    firstMinUserConsumerAmountMap.put(userId,userSum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Long totalTradeAmount = totalTradeAmountValue.value();
                if(null==totalTradeAmount){
                    totalTradeAmount = value.getAmount();
                }else{
                    totalTradeAmount += value.getAmount();
                }
                totalTradeAmountValue.update(totalTradeAmount);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(StringUtils.isEmpty(accumulator.getTransCode())){
                accumulator.setTransCode(value.getTransCode());
            }
            if(StringUtils.isEmpty(accumulator.getCurrentMinute())){
                accumulator.setCurrentMinute(currentMinute);
            }
            Long sum = null==accumulator.getSumAmount()?0L:accumulator.getSumAmount();
            sum += value.getAmount();
            accumulator.setSumAmount(sum);
            return accumulator;
        }

        public TradeCountMinuteResult getResult(TradeCountMinuteResult accumulator) {
            return accumulator;
        }

        public TradeCountMinuteResult merge(TradeCountMinuteResult a, TradeCountMinuteResult b) {
            return null;
        }
    }

}
