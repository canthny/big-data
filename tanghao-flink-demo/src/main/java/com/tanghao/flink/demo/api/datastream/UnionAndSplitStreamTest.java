package com.tanghao.flink.demo.api.datastream;

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
        list1.add(new Message("1","1001",30L));
        list1.add(new Message("4","1001",1L));

        DataStreamSource<Message> streamSource1 = streamExecutionEnvironment.fromCollection(list1);

        List<Message> list2 = new ArrayList<Message>();
        list2.add(new Message("2","2001",21L));
        list2.add(new Message("3","2001",65L));
        list2.add(new Message("5","2001",21L));
        DataStreamSource<Message> streamSource2 = streamExecutionEnvironment.fromCollection(list2);

        DataStream<Message> streamSource = streamSource1.union(streamSource2);
//        streamSource.print();

        /** SplitStream is deprecated,now use Side Outputs instead*/
        final OutputTag<Message> outputTag1001 = new OutputTag<Message>("side-output-1001"){};
        final OutputTag<Message> outputTag2001 = new OutputTag<Message>("side-output-2001"){};

        SingleOutputStreamOperator<Message>  sideOutStream = streamSource.process(new ProcessFunction<Message, Message>() {
            @Override
            public void processElement(Message value, Context ctx, Collector<Message> out) throws Exception {
                    out.collect(value);
                    if(value.getTransCode().equals("1001")){
                        ctx.output(outputTag1001,value);
                    }else if(value.getTransCode().equals("2001")){
                        ctx.output(outputTag2001,value);
                    }
            }
        });
        DataStream<Message> sideOutputStream1001 = sideOutStream.getSideOutput(outputTag1001);
        DataStream<Message> sideOutputStream2001 = sideOutStream.getSideOutput(outputTag2001);

        sideOutStream.print("all");
        sideOutputStream1001.print("1001");
        sideOutputStream2001.print("2001");

        try {
            streamExecutionEnvironment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
