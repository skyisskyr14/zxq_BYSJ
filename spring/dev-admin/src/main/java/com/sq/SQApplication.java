package com.sq;

import org.mybatis.spring.annotation.MapperScan;
//import com.skyissky.common.config.GlobalJacksonTimeConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.sq")
//@Import(GlobalJacksonTimeConfig.class)

@EnableScheduling
@EnableTransactionManagement
@EnableAsync
public class    SQApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SQApplication.class, args);
    }
}
