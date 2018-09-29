package com.tanghao.bigdata.drools.service;

import com.mongodb.BasicDBObject;
import com.tanghao.bigdata.drools.domain.PhoneNoBlackObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author： Canthny
 * @Description： 手机号黑名单
 * @Date： Created in 2018/9/29 15:54
 */
@Component
public class PhoneNoBlacklistService {

    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired(required = false)
//    public void setRedisTemplate(RedisTemplate redisTemplate) {
//        RedisSerializer stringSerializer = new StringRedisSerializer();
//        redisTemplate.setKeySerializer(stringSerializer);
//        redisTemplate.setValueSerializer(stringSerializer);
//        redisTemplate.setHashKeySerializer(stringSerializer);
//        redisTemplate.setHashValueSerializer(stringSerializer);
//        this.redisTemplate = redisTemplate;
//    }

    @Autowired
    private MongoTemplate mongoTemplate;

    private static PhoneNoBlacklistService phoneNoBlacklistService;

    @PostConstruct
    public void init() {
        phoneNoBlacklistService = this;
        phoneNoBlacklistService.redisTemplate = this.redisTemplate;
        phoneNoBlacklistService.mongoTemplate = this.mongoTemplate;
    }

    public static boolean isBlack(String phoneNo){
        Set<String> resultSet = (HashSet)phoneNoBlacklistService.redisTemplate.opsForSet().members("PHONE_NO_BLACK_LIST");
        if(CollectionUtils.isEmpty(resultSet)) {
            resultSet = getPhoneNoBlackListFromMongoDb();
            phoneNoBlacklistService.redisTemplate.opsForSet().add("PHONE_NO_BLACK_LIST",resultSet.toArray());
        }
        if(resultSet.contains(phoneNo)){
            return true;
        }else{
            return false;
        }
    }

    private static Set<String> getPhoneNoBlackListFromMongoDb(){
        BasicDBObject fieldsObject=new BasicDBObject();//指定返回的字段fieldsObject.put("name", true);
        fieldsObject.put("phone_no", true);
        Query query = new BasicQuery(new BasicDBObject(),fieldsObject);
        List<PhoneNoBlackObject> list= phoneNoBlacklistService.mongoTemplate.find(query,PhoneNoBlackObject.class,"PHONE_NO_BLACK_LIST");
        Set<String> phoneNoBlackListSet = new HashSet<String>();
        for(PhoneNoBlackObject info:list){
            phoneNoBlackListSet.add(info.getPhoneNo());
        }

        return phoneNoBlackListSet;
    }
}