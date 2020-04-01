package com.tanghao.flink.demo.api.datastream;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;

/**
 * Description： FlatMapApi测试类
 * Created By tanghao on 2020/4/1
 */
public class FlatMapApiTest {

    public static void main(String[] args) {
        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        String[] strArray={"blue,green,red","green,green,red","blue,blue","red"};
        DataStreamSource<String> streamSource = streamExecutionEnvironment.fromCollection(Arrays.asList(strArray));
        SingleOutputStreamOperator stream = streamSource.flatMap(new FlatMapFunction<String, String>() {

            /** flatMap相对于Map更灵活，不仅仅一对一，也可以过滤也可以一对多 */
            public void flatMap(String s, Collector<String> collector) throws Exception {
                String[] array = s.split(",");
                for (int i=0;i<array.length;i++){
                    if("blue".equals(array[i])){
                        collector.collect("yellow");
                    }else if("green".equals(array[i])){
                        //do nothing 相当于过滤掉了
                    }else{
                        collector.collect(array[i]);
                    }
                }
            }
        });
//        streamSource.print();

        stream.print();
        try {
            streamExecutionEnvironment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
