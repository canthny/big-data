package com.tanghao.bigdata.drools.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import java.nio.charset.Charset;

/**
 * @Author： Canthny
 * @Description： redis模板类序列化问题配置
 * @Date： Created in 2018/9/29 21:40
 */
@Configuration
public class RedisConfig {

    /**
     * redisTemplate 序列化使用的jdkSerializeable, 存储二进制字节码, 所以自定义序列化类
     * @return
     */
    /**
     * redisTemplate默认jdkSerializeable，key会产生\xac\xed\x00\x05t\x00\x13这样的字符，所以设置成StringRedisSerializer
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(new IntegerRedisSerializer());
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    public class IntegerRedisSerializer implements RedisSerializer<Integer> {

        private final Charset charset;

        public IntegerRedisSerializer() {
            this(Charset.forName("UTF8"));
        }

        public IntegerRedisSerializer(Charset charset) {
            Assert.notNull(charset, "Charset must not be null!");
            this.charset = charset;
        }

        public Integer deserialize(byte[] bytes) {
            return (bytes == null ? null : Integer.parseInt(new String(bytes, charset)));
        }

        public byte[] serialize(Integer num) {
            return (num == null ? null : String.valueOf(num).getBytes(charset));
        }
    }

}
