package com.tanghao.flink.demo.api.others;

import com.alibaba.fastjson.JSONObject;
import com.tanghao.flink.demo.common.ConstantKafka;
import com.tanghao.flink.demo.common.Message;
import com.tanghao.flink.demo.common.TransTypeInfo;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.util.Collector;

import java.util.Properties;

/**
 * Description： 广播维表动态更新测试，对应的数据源为BroadcastMsgProducerTest产生的数据
 * 场景：上游的消息中仅包含id和code之类的数据要转化成对应的类目名称，从字典表或者动态广播流中获取映射关系，并且支持类目名称可更新或者开关控制
 * Created By tanghao on 2020/4/8
 */
public class BroadcastTableTest {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.8.145:9082");

        /** 动态配置交易类型信息流，可以修改transCode对应的交易名称，也可以通过isOpen开关来展示是否要转换transName */
        SingleOutputStreamOperator<TransTypeInfo> stream= env.addSource(new FlinkKafkaConsumer011(ConstantKafka.TRANS_TYPE_TOPIC, new SimpleStringSchema(), properties)).map(new MapFunction<String, TransTypeInfo>() {
            public TransTypeInfo map(String s) throws Exception {
                return JSONObject.parseObject(s,TransTypeInfo.class);
            }
        });

        /** 业务消息中仅包含transCode交易编码，因此需要从广播表中获取交易编码对应的transName */
        SingleOutputStreamOperator<Message> messageStream= env.addSource(new FlinkKafkaConsumer011(ConstantKafka.TRADE_MESSAGE_TOPIC, new SimpleStringSchema(), properties)).map(new MapFunction<String, Message>() {
            public Message map(String s) throws Exception {
                return JSONObject.parseObject(s,Message.class);
            }
        });

        /** 存放transCode和transName的映射关系 */
        final MapStateDescriptor<String, String> TRANS_TYPE_MAP = new MapStateDescriptor<String,String>(
                "TRANS_TYPE_MAP",
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO);

        /** 将业务消息流和交易类型码流connect，并通过BroadcastProcessFunction设置业务消息处理方法和广播表更新方法 */
        SingleOutputStreamOperator<Tuple2<Message, String>> resStream = messageStream.connect(stream.broadcast(TRANS_TYPE_MAP)).process(new BroadcastProcessFunction<Message, TransTypeInfo, Tuple2<Message,String>>() {
            @Override
            public void processElement(Message value, ReadOnlyContext ctx, Collector<Tuple2<Message, String>> out) throws Exception {
                ReadOnlyBroadcastState<String, String> broadcastState =
                        ctx.getBroadcastState(TRANS_TYPE_MAP);
                String transName = broadcastState.get(value.getTransCode());
                out.collect(Tuple2.of(value,transName));
            }

            @Override
            public void processBroadcastElement(TransTypeInfo value, Context ctx, Collector<Tuple2<Message, String>> out) throws Exception {
                BroadcastState<String, String> broadcastState =
                        ctx.getBroadcastState(TRANS_TYPE_MAP);
                //判断开关，添加或者删除映射关系记录
                if(value.isOpen()){
                    broadcastState.put(value.getTransCode(), value.getTransName());
                }else{
                    broadcastState.remove(value.getTransCode());
                }
            }
        });

        resStream.print();

        /**
         * 从MessageKafkaProducerTest中producer交易类型信息的isOpen开关，输出结果如下：
         * 5> (Message{id='af02aa86-927d-47d2-af56-a4f06d5151c8', transCode='2001', amount=101, tradeTime=Wed Apr 08 17:14:06 CST 2020, userId=209},提现)
         * 5> (Message{id='0eca95a8-5d79-4e99-a992-a33c9ba20937', transCode='2001', amount=101, tradeTime=Wed Apr 08 17:14:21 CST 2020, userId=111},null)
         * 5> (Message{id='93731fd3-8ecb-4aaa-8ace-078b451de178', transCode='2001', amount=101, tradeTime=Wed Apr 08 17:15:06 CST 2020, userId=141},提现)
         */

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
