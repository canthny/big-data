package com.tanghao.bigdata.drools.service;

import com.mongodb.BasicDBObject;
import com.tanghao.bigdata.drools.domain.DefeatMapReduceObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author： Canthny
 * @Description： 缴费规则服务
 * @Date： Created in 2018/9/30 0:12
 */
@Component
public class MobilePaymentRuleService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static MobilePaymentRuleService mobilePaymentRuleService;

    @PostConstruct
    public void init() {
        mobilePaymentRuleService = this;
        mobilePaymentRuleService.redisTemplate = this.redisTemplate;
        mobilePaymentRuleService.mongoTemplate = this.mongoTemplate;
    }

    public static Integer getAvgAmountPerMonthByAccountNo(String accountNo){
        Integer amount = (Integer)mobilePaymentRuleService.redisTemplate.opsForHash().get("AvgAmountPerMonthByAccountNo",accountNo);
        if(null==amount){
            BasicDBObject queryObject=new BasicDBObject();
            queryObject.put("_id",accountNo);
            BasicDBObject fieldsObject=new BasicDBObject();//指定返回的字段fieldsObject.put("name", true);
            fieldsObject.put("_id", true);
            fieldsObject.put("value", true);
            Query query = new BasicQuery(queryObject,fieldsObject);
            List<DefeatMapReduceObject> list= mobilePaymentRuleService.mongoTemplate.find(query,DefeatMapReduceObject.class,"ACCOUNT_MOBILEPAYMENT_DAY_AVG");
            amount = list.get(0).getValue();
            mobilePaymentRuleService.redisTemplate.opsForHash().put("AvgAmountPerMonthByAccountNo",list.get(0).getId(),list.get(0).getValue());
        }
        //TODO 这里有很多空判断NullPointException，当空的时候应该返回什么呢，成功or失败，还是返回金额在rule文件里再去判断

        return amount;
    }

    /**
     * 从缓存中获取流计算结果，该用户一小时内充值次数
     * @param accountNo
     * @return
     */
    public static Integer getCountPerHourByAccountNo(String accountNo){
        Integer amount = (Integer)mobilePaymentRuleService.redisTemplate.opsForHash().get("CountPerHourByAccountNo",accountNo);
        return null==amount?0:amount;

    }
}
