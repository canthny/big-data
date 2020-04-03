package com.tanghao.flink.demo.api.datastream;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

/**
 * Description： FilterApi测试类
 * Created By tanghao on 2020/4/1
 */
public class FilterApiTest {

    public static void main(String[] args) {
        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        String[] strArray={"0","1","2","3","4","5","6","7","8","9","10"};
        DataStreamSource<String> streamSource = streamExecutionEnvironment.fromCollection(Arrays.asList(strArray));
        SingleOutputStreamOperator streamAfterFilter = streamSource.filter(new FilterFunction<String>() {
            public boolean filter(String s) throws Exception {
                if(Integer.parseInt(s) % 2==0){
                    return true;
                }else{
                    return false;
                }
            }
        });

        streamAfterFilter.print("双数流");

        SingleOutputStreamOperator streamAfterFilter2 = streamSource.filter(new FilterFunction<String>() {
            public boolean filter(String s) throws Exception {
                if(Integer.parseInt(s) % 2==0){
                    return false;
                }else{
                    return true;
                }
            }
        });
        streamAfterFilter2.print("单数流");


        try {
            streamExecutionEnvironment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
