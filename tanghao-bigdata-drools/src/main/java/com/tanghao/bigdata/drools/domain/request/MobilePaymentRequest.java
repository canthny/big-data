package com.tanghao.bigdata.drools.domain.request;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @Author： Canthny
 * @Description： 手机充值请求对象
 * @Date： Created in 2018/9/29 19:44
 */
public class MobilePaymentRequest extends BaseRequest{

    private static final long serialVersionUID = -5099820860840607608L;

    private String accountNo;

    private String bankCardNo;

    private String mobile;

    private Integer amount;

    private String outTradeDate;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getOutTradeDate() {
        return outTradeDate;
    }

    public void setOutTradeDate(String outTradeDate) {
        this.outTradeDate = outTradeDate;
    }
}
