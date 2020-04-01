package com.tanghao.flink.demo.api.datastream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Description： MapApi测试
 * Created By tanghao on 2020/3/31
 */
public class MapApiTest {

    public static void main(String[] args) {
        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        String[] strArray={"deposit,10","withdraw,10","deposit,3","withdraw,10"};
        DataStreamSource<String> streamSource = streamExecutionEnvironment.fromCollection(Arrays.asList(strArray));
        SingleOutputStreamOperator mapStream = streamSource.map(new MapFunction<String, Map<String,Long>>() {
            public Map<String, Long> map(String s) throws Exception {
                String[] array = s.split(",");
                Map<String, Long> map = new HashMap<String, Long>();
                for (int i=0;i<array.length;i++){
                    map.put(array[0],Long.parseLong(array[1])+10);
                }
                return map;
            }
        });
        mapStream.print();
        streamSource.print();

        try {
            streamExecutionEnvironment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
