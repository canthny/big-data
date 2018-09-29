package com.tanghao.bigdata.drools.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @Author： Canthny
 * @Description： 手机号黑名单
 * @Date： Created in 2018/9/29 16:06
 */
@Document(collection="PHONE_NO_BLACK_LIST")
public class PhoneNoBlackObject implements Serializable {

    private static final long serialVersionUID = 9180593713475107020L;

    @Field("phone_no")
    private String phoneNo;

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
