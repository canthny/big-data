package com.tanghao.flink.demo.common;

import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * Description： TODO
 * Created By tanghao on 2020/4/7
 */
public class MessageDeserializer implements Deserializer<Message> {

    public void configure(Map<String, ?> paramMap, boolean paramBoolean) {
    }

    public Message deserialize(String paramString, byte[] paramArrayOfByte) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (paramArrayOfByte);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return (Message)obj;
    }

    public void close() {
    }
}
