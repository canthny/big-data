package com.tanghao.flink.demo.source;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * Description： 内置数据源
 * Created By tanghao on 2020/3/31
 */
public class InnerSource {

    public static void main(String[] args) {
        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        fromFile(streamExecutionEnvironment);
    }

    private static void fromFile(StreamExecutionEnvironment streamExecutionEnvironment){
        DataStreamSource<String> streamSource = streamExecutionEnvironment.readTextFile("D://test.txt","UTF-8");
        streamSource.print();
        try {
            streamExecutionEnvironment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
