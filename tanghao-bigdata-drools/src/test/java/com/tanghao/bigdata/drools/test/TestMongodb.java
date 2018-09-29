package com.tanghao.bigdata.drools.test;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;
import com.tanghao.bigdata.drools.DroolsStarter;
import com.tanghao.bigdata.drools.domain.MobilePaymentInfo;
import com.tanghao.bigdata.drools.domain.PhoneNoBlackObject;
import com.tanghao.bigdata.drools.mongodb.service.MobilePaymentInfoService;
import com.tanghao.bigdata.drools.service.PhoneNoBlacklistService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * @Author： Canthny
 * @Description： mongodb测试类
 * @Date： Created in 2018/9/27 14:54
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DroolsStarter.class)
public class TestMongodb {

    @Autowired
    MobilePaymentInfoService mobilePaymentInfoService;

    @Autowired
    MongoOperations mongoTemplate;

    @Test
    public void testSave() {
        MobilePaymentInfo info = new MobilePaymentInfo();
        info.setAccountNo("3691529391467418");
        info.setAmount((int)(Math.random()*100));
        info.setBankCardNo("62260113241234");
        info.setMobile("18709858763");
        info.setPayOrderNo("2018090300001");
        info.setTime(new Date());

        mobilePaymentInfoService.saveMobilePaymentInfo(info);
    }

    @Test
    public void insertPhoneNoBlackList(){
        String phoneNo = "18709858763";
        PhoneNoBlackObject obj = new PhoneNoBlackObject();
        obj.setPhoneNo(phoneNo);
        mongoTemplate.save(obj);



    }

    /**
     * 查询，指定查询条件和查询结果字段
     */
    @Test
    public void findPhonenNoBlackList(){

        BasicDBObject fieldsObject=new BasicDBObject();//指定返回的字段fieldsObject.put("name", true);
        fieldsObject.put("phone_no", true);
        Query query = new BasicQuery(new BasicDBObject(),fieldsObject);
        List<PhoneNoBlackObject> list= mongoTemplate.find(query,PhoneNoBlackObject.class,"PHONE_NO_BLACK_LIST");
        Set<String> phoneNoBlackListSet = new HashSet<String>();
        for(PhoneNoBlackObject info:list){
            phoneNoBlackListSet.add(info.getPhoneNo());
        }
        String test = "1";
    }

    @Test
    public void testMapReduce(){
        Query query = new Query();
//        Criteria criteria = Criteria.where("time").gte("2018-09-27").lte("2018-09-30");
//        query.addCriteria(criteria);
        MapReduceOptions mapReduceOptions = new MapReduceOptions();
        mapReduceOptions.outputCollection("ACCOUNT_MOBILEPAYMENT_DAY_AVG");
        mongoTemplate.mapReduce(query, "MOBILE_PAYMENT_INFO","classpath:mapreduce/mobile_payment_info_map.js", "classpath:mapreduce/mobile_payment_info_reduce.js",
                mapReduceOptions, HashMap.class);
    }

    @Test
    public void test(){
        System.out.println(PhoneNoBlacklistService.isBlack("18709858763"));
    }
}
