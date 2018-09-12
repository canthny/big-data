package com.tanghao.bigdata.drools.controller;

import com.tanghao.bigdata.drools.domain.PayRequest;
import com.tanghao.bigdata.drools.domain.RuleResponse;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Author： Canthny
 * @Description： 测试支付风控拦截
 * @Date： Created in 2018/9/11 15:18
 */
@RequestMapping(value = "/test/rules")
@RestController
public class TestPayRule {

    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public RuleResponse testPayRule(){
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("pay_rule");
        PayRequest payRequest = new PayRequest();
        payRequest.setAccountNo("1");
        payRequest.setAmount(34L);
        payRequest.setPayTime(new Date());
        ksession.insert(payRequest);
        ksession.fireAllRules();
        ksession.dispose();
        return payRequest.getResponse();
    }
}
