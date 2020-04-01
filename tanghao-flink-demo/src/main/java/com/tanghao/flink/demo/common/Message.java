package com.tanghao.flink.demo.common;

/**
 * Description： 消息POJO类
 * Created By tanghao on 2020/4/1
 */
public class Message{
    private String id;

    private String type;

    private Long amount;

    /** 符合POJO类的规则：
     * The class is public and standalone (no non-static inner class)
     * The class has a public no-argument constructor
     * All non-static, non-transient fields in the class (and all superclasses) are either public (and non-final) or have a public getter- and a setter- method that follows the Java beans naming conventions for getters and setters.
     * 这里关注第二条，必须有无参构造器
     * */
    public Message(){

    }

    public Message(String id, String type, Long amount) {
        this.id = id;
        this.type = type;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                '}';
    }
}