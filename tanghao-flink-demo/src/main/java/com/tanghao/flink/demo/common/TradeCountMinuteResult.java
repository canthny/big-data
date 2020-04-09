package com.tanghao.flink.demo.common;

import java.io.Serializable;

/**
 * Description： 交易统计小时结果
 * Created By tanghao on 2020/4/9
 */
public class TradeCountMinuteResult implements Serializable {

    private String transCode;

    private Long sumAmount;

    private String currentMinute;

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public Long getSumAmount() {
        return sumAmount;
    }

    public void setSumAmount(Long sumAmount) {
        this.sumAmount = sumAmount;
    }

    public String getCurrentMinute() {
        return currentMinute;
    }

    public void setCurrentMinute(String currentMinute) {
        this.currentMinute = currentMinute;
    }

    @Override
    public String toString() {
        return "TradeCountMinuteResult{" +
                "transCode='" + transCode + '\'' +
                ", sumAmount=" + sumAmount +
                ", currentMinute='" + currentMinute + '\'' +
                '}';
    }
}
