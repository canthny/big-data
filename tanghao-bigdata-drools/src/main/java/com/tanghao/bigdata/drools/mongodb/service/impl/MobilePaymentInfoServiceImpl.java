package com.tanghao.bigdata.drools.mongodb.service.impl;

import com.tanghao.bigdata.drools.mongodb.domain.MobilePaymentInfo;
import com.tanghao.bigdata.drools.mongodb.service.MobilePaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

/**
 * @Author： Canthny
 * @Description： 手机缴费信息接口实现类
 * @Date： Created in 2018/9/27 14:47
 */
@Component("mobilePaymentInfoService")
public class MobilePaymentInfoServiceImpl implements MobilePaymentInfoService{

    @Autowired
    MongoOperations mongoTemplate;

    public void saveMobilePaymentInfo(MobilePaymentInfo info) {
        mongoTemplate.save(info);
    }
}
