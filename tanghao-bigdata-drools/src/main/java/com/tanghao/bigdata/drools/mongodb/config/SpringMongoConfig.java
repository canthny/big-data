package com.tanghao.bigdata.drools.mongodb.config;

import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * @Author： Canthny
 * @Description： mongodb配置
 * @Date： Created in 2018/9/29 11:21
 */
@Configuration
public class SpringMongoConfig{

    private @Value("${spring.data.mongodb.uri}") String uri;

    public @Bean
    MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClientURI(uri));
    }

    public @Bean
    MongoTemplate mongoTemplate() throws Exception {

//        MappingMongoConverter converter =
//                new MappingMongoConverter(mongoDbFactory(), new MongoMappingContext());

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, new MongoMappingContext());
        //这里是为了去掉插入mongodb时自动添加的_class类
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), converter);

        return mongoTemplate;

    }

}
