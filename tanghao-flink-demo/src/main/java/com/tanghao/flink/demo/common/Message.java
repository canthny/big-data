package com.tanghao.flink.demo.common;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Description： 消息POJO类
 * Created By tanghao on 2020/4/1
 */
public class Message implements Serializable {
    private String id;

    private String transCode;//1001-充值，2001-提现，3001-消费

    private Long amount;

    private Date tradeTime;

    private Long userId;

    /** 符合POJO类的规则：
     * The class is public and standalone (no non-static inner class)
     * The class has a public no-argument constructor
     * All non-static, non-transient fields in the class (and all superclasses) are either public (and non-final) or have a public getter- and a setter- method that follows the Java beans naming conventions for getters and setters.
     * 这里关注第二条，必须有无参构造器
     * */
    public Message(){

    }

    public Message(String transCode, Long amount) {
        this.id = generatorId();
        this.transCode = transCode;
        this.amount = amount;
        this.tradeTime = new Date();
        this.userId = generatorUserId();
    }


    public Message(String id, String transCode, Long amount) {
        this.id = id;
        this.transCode = transCode;
        this.amount = amount;
        this.tradeTime = new Date();
        this.userId = generatorUserId();
    }

    public Message(String id, String transCode, Long amount, Date tradeTime) {
        this.id = id;
        this.transCode = transCode;
        this.amount = amount;
        this.tradeTime = tradeTime;
        this.userId = generatorUserId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    private String generatorId(){
        return UUID.randomUUID().toString();
    }

    private long generatorUserId(){
        Random random = new Random();
        return random.nextInt(1000);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", transCode='" + transCode + '\'' +
                ", amount=" + amount +
                ", tradeTime=" + tradeTime +
                ", userId=" + userId +
                '}';
    }
}