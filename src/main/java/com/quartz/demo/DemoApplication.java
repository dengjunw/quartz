package com.quartz.demo;

import com.quartz.demo.demo.JobTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@SpringBootApplication
@Slf4j
public class DemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        String[] profiles = context.getEnvironment().getActiveProfiles();


        JobTest jobTest = context.getBean(JobTest.class);
        //添加一分钟之后执行的任务
        jobTest.submitTestJob(1L,
                Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).plusMinutes(1).toInstant()));

        log.info("{} is active", profiles);
    }

}

