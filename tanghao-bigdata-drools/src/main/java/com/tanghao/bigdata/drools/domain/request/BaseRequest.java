package com.tanghao.bigdata.drools.domain.request;

import com.tanghao.bigdata.drools.domain.response.RuleResponse;

import java.io.Serializable;

/**
 * @Author： Canthny
 * @Description： 一次支付请求参数
 * @Date： Created in 2018/9/11 15:17
 */
public class BaseRequest implements Serializable{


    private static final long serialVersionUID = 3849610254845766744L;
    private RuleResponse response;

    public void setResponse(String code,String level,String desc){
        this.response = new RuleResponse(code,level,desc);
    }

    public RuleResponse getResponse() {
        return response;
    }
}
