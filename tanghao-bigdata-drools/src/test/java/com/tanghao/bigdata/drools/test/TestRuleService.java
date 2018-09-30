package com.tanghao.bigdata.drools.test;

import com.tanghao.bigdata.drools.DroolsStarter;
import com.tanghao.bigdata.drools.service.MobilePaymentRuleService;
import com.tanghao.bigdata.drools.service.PhoneNoBlacklistService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author： Canthny
 * @Description： 规则服务类测试
 * @Date： Created in 2018/9/30 0:49
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DroolsStarter.class)
public class TestRuleService {

    @Test
    public void testPhoneNoBlacklistService(){
        System.out.println(PhoneNoBlacklistService.isBlack("18709858763"));
    }

    @Test
    public void testMobilePaymentRuleService(){
        System.out.println(MobilePaymentRuleService.getAvgAmountPerMonthByAccountNo("3691529391467418"));
    }
}
