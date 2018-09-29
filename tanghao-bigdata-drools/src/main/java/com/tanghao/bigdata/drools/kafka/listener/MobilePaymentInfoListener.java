package com.tanghao.bigdata.drools.kafka.listener;

import com.tanghao.bigdata.drools.domain.MobilePaymentInfo;
import com.tanghao.bigdata.drools.mongodb.service.MobilePaymentInfoService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;

/**
 * @Author： Canthny
 * @Description： 手机充值消息监听者
 * @Date： Created in 2018/9/29 10:27
 */
@Component
public class MobilePaymentInfoListener implements MessageListener<String,String>{

    @Autowired
    MobilePaymentInfoService mobilePaymentInfoService;


    @Override
    public void onMessage(ConsumerRecord<String, String> stringStringConsumerRecord) {
        String info = stringStringConsumerRecord.value();
        System.out.println(info);
        MobilePaymentInfo mobilePaymentInfo = JSON.parseObject(info,MobilePaymentInfo.class);
        mobilePaymentInfoService.saveMobilePaymentInfo(mobilePaymentInfo);
    }
}
