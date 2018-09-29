package com.tanghao.bigdata.drools.kafka.config;

import com.tanghao.bigdata.drools.kafka.listener.MobilePaymentInfoListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.stereotype.Component;

/**
 * @Author： Canthny
 * @Description： 手机充值消息配置
 * @Date： Created in 2018/9/29 10:24
 */
@Configuration
@Component
public class MobilePaymentInfoConfig {

    @Autowired
    MobilePaymentInfoListener mobilePaymentInfoListener;

    @Bean
    ContainerProperties containerProperties(){
        //配置监听topic
        ContainerProperties containerProperties = new ContainerProperties("mobile_pay_info");
        containerProperties.setMessageListener(mobilePaymentInfoListener);
        return containerProperties;
    }
    @Bean
    KafkaMessageListenerContainer<String, String> messageListenerContainer(ConsumerFactory<String, String> consumerFactory,
                                                                           ContainerProperties containerProperties){
        return new KafkaMessageListenerContainer<String, String>(consumerFactory, containerProperties);
    }
}
