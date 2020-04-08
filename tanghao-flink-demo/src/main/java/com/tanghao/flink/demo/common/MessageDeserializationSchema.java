package com.tanghao.flink.demo.common;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

/**
 * Description： TODO
 * Created By tanghao on 2020/4/8
 */
public class MessageDeserializationSchema implements DeserializationSchema<Message> {
    public Message deserialize(byte[] bytes) throws IOException {
        return null;
    }

    public boolean isEndOfStream(Message message) {
        return false;
    }

    public TypeInformation<Message> getProducedType() {
        return null;
    }
}
