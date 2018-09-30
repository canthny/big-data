package com.tanghao.bigdata.drools.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @Author： Canthny
 * @Description： mapreduce后默认输出对象
 * @Date： Created in 2018/9/30 0:35
 */
public class DefeatMapReduceObject implements Serializable {

    private static final long serialVersionUID = -5403346308235899592L;

    private String id;

    private Integer value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DefeatMapReduceObject{" +
                "id='" + id + '\'' +
                ", value=" + value +
                '}';
    }
}
