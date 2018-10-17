package com.tanghao.bigdata.drools.test;

import com.tanghao.bigdata.drools.DroolsStarter;
import com.tanghao.bigdata.drools.domain.request.MobilePaymentRequest;
import com.tanghao.bigdata.drools.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @Author： Canthny
 * @Description： drools单元测试类
 * @Date： Created in 2018/9/29 19:51
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DroolsStarter.class)
public class TestDrools {

    /**
     *测试case1：金额超限
     */
    @Test
    public void testCase1(){
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("pay_rule");
        MobilePaymentRequest payRequest = new MobilePaymentRequest();
        payRequest.setAccountNo("3691529391467418");
        payRequest.setAmount(351);//设置为大于单笔的最大限额，pay_rule.drl中规则pay_rule1配的是300
        payRequest.setOutTradeDate(DateUtil.convertDateToString("yyyyMMdd",new Date()));
        payRequest.setMobile("18709858763");
        payRequest.setBankCardNo("62260113241234");
        ksession.insert(payRequest);
        ksession.fireAllRules();
        ksession.dispose();
        System.out.println(payRequest.getResponse());
    }

    /**
     *  该银行卡号在黑名单内（银行卡黑名单表）
     *  手机号在黑名单内（手机号黑名单表）
     */
    @Test
    public void testCase2(){
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("pay_rule");
        MobilePaymentRequest payRequest = new MobilePaymentRequest();
        payRequest.setAccountNo("3691529391467418");
        payRequest.setAmount(49);//金额不要超过单笔限额
        payRequest.setOutTradeDate(DateUtil.convertDateToString("yyyyMMdd",new Date()));
        payRequest.setMobile("18709858763");//手机号在黑名单中
        payRequest.setBankCardNo("62260113241234");
        ksession.insert(payRequest);
        ksession.fireAllRules();
        ksession.dispose();
        System.out.println(payRequest.getResponse());
    }

    /**
     *  当前单笔充值额度大于用户最近一个月充值额度平均值的3倍(定时批处理，月初统计上个月充值平均额度)
     */
    @Test
    public void testCase3(){
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("pay_rule");
        MobilePaymentRequest payRequest = new MobilePaymentRequest();
        payRequest.setAccountNo("3691529391467418");
        payRequest.setAmount(280);//设置为大于月平均值的3倍，且不超过单笔最大限额
        payRequest.setOutTradeDate(DateUtil.convertDateToString("yyyyMMdd",new Date()));
        payRequest.setMobile("18709858764");//手机号不在黑名单
        payRequest.setBankCardNo("62260113241234");
        ksession.insert(payRequest);
        ksession.fireAllRules();
        ksession.dispose();
        System.out.println(payRequest.getResponse());
    }

    /**
     *  一个小时内充值次数不超过5笔（实时流计算，保留用户一小时充值笔数）
     */
    @Test
    public void testCase4(){
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("pay_rule");
        MobilePaymentRequest payRequest = new MobilePaymentRequest();
        payRequest.setAccountNo("3691529391467418");
        payRequest.setAmount(220);//设置为小于月平均值的3倍，且不超过单笔最大限额
        payRequest.setOutTradeDate(DateUtil.convertDateToString("yyyyMMdd",new Date()));
        payRequest.setMobile("18709858764");//手机号不在黑名单
        payRequest.setBankCardNo("62260113241234");
        ksession.insert(payRequest);
        ksession.fireAllRules();
        ksession.dispose();
        System.out.println(payRequest.getResponse());
    }
}
