package com.tanghao.bigdata.drools.enums;

/**
 * @Author： Canthny
 * @Description： 风控响应级别
 * @Date： Created in 2018/9/11 15:23
 */
public enum EnumRuleResponseLevel {

    NONE("0","无风险"),
    LOW("1","低风险"),
    MIDDLE("2","中风险 "),
    HIGH("3","高风险"),
    SUPER("4","超高风险");

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

    public static String getDescByCode(String code){
        if (code == null) {
            return null;
        }
        for (EnumRuleResponseLevel level : values()) {
            if (level.getCode().equals(code)) {
                return level.getDesc();
            }
        }
        return null;
    }
}
