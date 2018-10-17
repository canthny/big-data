package com.tanghao.bigdata.drools.test;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;
import com.tanghao.bigdata.drools.DroolsStarter;
import com.tanghao.bigdata.drools.domain.DefeatMapReduceObject;
import com.tanghao.bigdata.drools.domain.MobilePaymentInfo;
import com.tanghao.bigdata.drools.domain.PhoneNoBlackObject;
import com.tanghao.bigdata.drools.mongodb.service.MobilePaymentInfoService;
import com.tanghao.bigdata.drools.service.PhoneNoBlacklistService;
import com.tanghao.bigdata.drools.util.DateUtil;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    /**
     * 缴费数据插入
     * @throws ParseException
     */
    @Test
    public void testSave() throws ParseException {
        for(int i=0;i<10;i++){
            MobilePaymentInfo info = new MobilePaymentInfo();
            info.setAccountNo("3691529391467418");
            info.setAmount((int)(Math.random()*100));
            info.setBankCardNo("62260113241234");
            info.setMobile("18709858763");
            info.setPayOrderNo("2018090300001");
//          SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd" );
//          info.setTime(format.parse("2018-09-26"));
            if(i%3==0){
                info.setTime(DateUtil.getRelativeDate(new Date(),-1));
            }else if(i%3==1){
                info.setTime(new Date());
            }else if(i%3==2){
                info.setTime(DateUtil.getRelativeDate(new Date(),1));
            }
            mobilePaymentInfoService.saveMobilePaymentInfo(info);
        }
    }

    /**
     * 插入黑名单数据
     */
    @Test
    public void insertPhoneNoBlackList(){
        String phoneNo = "18709858763";
        PhoneNoBlackObject obj = new PhoneNoBlackObject();
        obj.setPhoneNo(phoneNo);
        mongoTemplate.save(obj);



    }

    /**
     * mongodb mapreduce批处理任务，可以通过定时器去执行，也可以通过crontab定时任务方式去执行脚本
     * 计算三天内用户缴费平均值
     * @throws ParseException
     */
    @Test
    public void testMapReduce() throws ParseException {
        Query query = new Query();
        SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd" );
        Criteria criteria = Criteria.where("time").gte(DateUtil.getRelativeDate(new Date(),-1)).lte(DateUtil.getRelativeDate(new Date(),1));
        query.addCriteria(criteria);
        MapReduceOptions mapReduceOptions = new MapReduceOptions();
        mapReduceOptions.outputCollection("ACCOUNT_MOBILEPAYMENT_DAY_AVG");
        mongoTemplate.mapReduce(query, "MOBILE_PAYMENT_INFO","classpath:mapreduce/mobile_payment_info_map.js", "classpath:mapreduce/mobile_payment_info_reduce.js",
                mapReduceOptions, HashMap.class);
    }

    /**
     * mongodb查询，指定查询条件和查询结果字段
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
        System.out.println(phoneNoBlackListSet.toArray());
    }
}
