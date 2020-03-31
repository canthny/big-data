package com.tanghao.flink.demo;

import org.apache.flink.formats.json.JsonNodeDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;

/**
 * @Author： Canthny
 * @Description： 测试一些api
 * @Date： Created in 2018/9/13 15:15
 */
public class TestAction {

    public static void main(String[] args) {
        try{
//            ObjectMapper mapper = new ObjectMapper();
//            ObjectNode initialKey = mapper.createObjectNode();
//            initialKey.put("index", 4);
//            byte[] serializedKey = mapper.writeValueAsBytes(initialKey);
//
//            ObjectNode initialValue = mapper.createObjectNode();
//            initialValue.put("word", "world");
//            byte[] serializedValue = mapper.writeValueAsBytes(initialValue);
//
//            JSONKeyValueDeserializationSchema schema = new JSONKeyValueDeserializationSchema(false);
//            ObjectNode deserializedValue = schema.deserialize(serializedKey, serializedValue, "", 0,
//                    0);
//            System.out.println(deserializedValue);
//
//            String testjson = "{\"accountId\":\"0716\",\"amount\":\"100\",\"eventTime\":\"2018/09/13 14:56:00\"}";
//            JsonNodeDeserializationSchema schema1 = new JsonNodeDeserializationSchema();
//            ObjectNode node = schema1.deserialize(testjson.getBytes());
//            System.out.println(node);
        }catch (Exception e){
            System.out.println(e);
        }



    }
}
