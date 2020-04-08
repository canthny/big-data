package com.tanghao.flink.demo.common;

import java.io.Serializable;

/**
 * Description： 交易类型信息
 * Created By tanghao on 2020/4/8
 */
public class TransTypeInfo implements Serializable {

    private String transCode;

    private String transName;

    private boolean isOpen;

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public String toString() {
        return "TransTypeInfo{" +
                "transCode='" + transCode + '\'' +
                ", transName='" + transName + '\'' +
                ", isOpen=" + isOpen +
                '}';
    }
}
