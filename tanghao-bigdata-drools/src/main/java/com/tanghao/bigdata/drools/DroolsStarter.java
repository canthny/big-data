package com.tanghao.bigdata.drools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author： Canthny
 * @Description： 启动类
 * @Date： Created in 2018/9/11 16:43
 */
@SpringBootApplication
public class DroolsStarter {

    public static void main(String[] args) {
        SpringApplication.run(DroolsStarter.class,args);
    }
}
