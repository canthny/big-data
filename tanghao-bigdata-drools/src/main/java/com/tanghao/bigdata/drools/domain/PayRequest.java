package com.tanghao.bigdata.drools.domain;

import java.util.Date;

/**
 * @Author： Canthny
 * @Description： 一次支付请求参数
 * @Date： Created in 2018/9/11 15:17
 */
public class PayRequest {

    private String accountNo;

    private Long amount;

    private Date payTime;

    private RuleResponse response;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public void setResponse(String code,String level,String desc){
        this.response = new RuleResponse(code,level,desc);
    }

    public RuleResponse getResponse() {
        return response;
    }
}
