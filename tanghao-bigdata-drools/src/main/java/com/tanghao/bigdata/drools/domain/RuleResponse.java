package com.tanghao.bigdata.drools.domain;

/**
 * @Author： Canthny
 * @Description： 风控响应对象
 * @Date： Created in 2018/9/11 15:22
 */
public class RuleResponse {

    RuleResponse(String code,String level,String msg){
        this.code = code;
        this.level = level;
        this.msg = msg;
    }

    private String code;

    private String level;

    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static Boolean checkIsNull(RuleResponse response){
        if (null==response){
            return true;
        }else{
            return false;
        }
    }
}
