package com.tanghao.flink.demo.common;

import java.io.Serializable;

/**
 * Description： 用户每分钟消费金额统计对象
 * Created By tanghao on 2020/4/9
 */
public class UserConsumerAmountPerMinute implements Serializable {

    private Long userId;

    private Long amount;

    private String currentMinute;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrentMinute() {
        return currentMinute;
    }

    public void setCurrentMinute(String currentMinute) {
        this.currentMinute = currentMinute;
    }

    @Override
    public String toString() {
        return "UserConsumerAmountPerMinute{" +
                "userId=" + userId +
                ", amount=" + amount +
                ", currentMinute='" + currentMinute + '\'' +
                '}';
    }
}
