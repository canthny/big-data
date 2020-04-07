package com.tanghao.flink.demo.common;

import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * Description： TODO
 * Created By tanghao on 2020/4/7
 */
public class MessageSerializer implements Serializer<Message> {

    public MessageSerializer(){
    }
    public void configure(Map paramMap, boolean paramBoolean) {
    }

    public byte[] serialize(String paramString, Message obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    public void close() {
    }
}
