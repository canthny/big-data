package com.tanghao.flink.demo.api.source;

import com.tanghao.flink.demo.common.Message;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Description： 合并数据源
 * Created By tanghao on 2020/4/2
 */
public class UnionAndSplitStreamTest {

    public static void main(String[] args) {
        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        List<Message> list1 = new ArrayList<Message>();
        list1.add(new Message("1","deposit",30L));
        list1.add(new Message("4","deposit",1L));

        DataStreamSource<Message> streamSource1 = streamExecutionEnvironment.fromCollection(list1);

        List<Message> list2 = new ArrayList<Message>();
        list2.add(new Message("2","withdraw",21L));
        list2.add(new Message("3","withdraw",65L));
        list2.add(new Message("5","withdraw",21L));
        DataStreamSource<Message> streamSource2 = streamExecutionEnvironment.fromCollection(list2);

        DataStream<Message> streamSource = streamSource1.union(streamSource2);
//        streamSource.print();

        /** SplitStream is deprecated,now use Side Outputs instead*/
        final OutputTag<Message> outputTag = new OutputTag<Message>("side-output"){};

        SingleOutputStreamOperator<Message>  sideOutStream = streamSource.process(new ProcessFunction<Message, Message>() {
            @Override
            public void processElement(Message value, Context ctx, Collector<Message> out) throws Exception {
                    if(value.getType().equals("withdraw")){
                        out.collect(value);
                    }else{
                        ctx.output(outputTag,value);
                    }
            }
        });
        DataStream<Message> sideOutputStream = sideOutStream.getSideOutput(outputTag);
        sideOutStream.print("deposit");
        sideOutputStream.print("withdraw");

        try {
            streamExecutionEnvironment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
