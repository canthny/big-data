package com.tanghao.bigdata.drools.enums;

/**
 * @Author： Canthny
 * @Description： 风控响应级别
 * @Date： Created in 2018/9/11 15:23
 */
public enum EnumRuleResponseLevel {

    ERROR("2","阻断型"),
    WARN("1","提示型"),
    INFO("0","监控型");

    private String code;

    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    EnumRuleResponseLevel(String code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
