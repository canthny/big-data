package com.tanghao.bigdata.drools.test;

import com.alibaba.fastjson.JSONObject;
import com.tanghao.bigdata.drools.DroolsStarter;
import com.tanghao.bigdata.drools.domain.MobilePaymentInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @Author： Canthny
 * @Description： kafka生产者测试
 * @Date： Created in 2018/9/29 15:02
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DroolsStarter.class)
public class TestKafkaProduct {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Test
    public void testSendMobilePaymentInfo() {
        MobilePaymentInfo info = new MobilePaymentInfo();
        info.setAccountNo("3691529391467418");
        info.setAmount((int)(Math.random()*100));
        info.setBankCardNo("62260113241234");
        info.setMobile("18709858763");
        info.setPayOrderNo("2018090100001");
        info.setTime(new Date());

        String infoStr = JSONObject.toJSONString(info);
        kafkaTemplate.send("mobile_pay_info",infoStr);
    }
}
