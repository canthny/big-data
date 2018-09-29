package com.tanghao.bigdata.drools.test;

import com.tanghao.bigdata.drools.DroolsStarter;
import com.tanghao.bigdata.drools.mongodb.domain.MobilePaymentInfo;
import com.tanghao.bigdata.drools.mongodb.service.MobilePaymentInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @Author： Canthny
 * @Description： //TODO 那么请问：这个类是干嘛的呢？
 * @Description：
 * @Date： Created in 2018/9/27 14:54
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DroolsStarter.class)
public class TestMongodb {

    @Autowired
    MobilePaymentInfoService mobilePaymentInfoService;

    @Test
    public void testSave() {
        MobilePaymentInfo info = new MobilePaymentInfo();
        info.setAccountNo("1234312412345235");
        info.setAmount(120);
        info.setBankCardNo("62260113241234");
        info.setMobile("18709858763");
        info.setPayOrderNo("2018092700002");
        info.setTime(new Date());
        mobilePaymentInfoService.saveMobilePaymentInfo(info);
    }
}
