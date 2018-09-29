package com.tanghao.bigdata.drools.mongodb.service;

import com.tanghao.bigdata.drools.mongodb.domain.MobilePaymentInfo;

/**
 * @Author： Canthny
 * @Description： 手机缴费信息接口
 * @Date： Created in 2018/9/27 14:46
 */
public interface MobilePaymentInfoService {

    public void saveMobilePaymentInfo(MobilePaymentInfo info);

}
