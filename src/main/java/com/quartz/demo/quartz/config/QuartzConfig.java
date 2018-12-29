package com.quartz.demo.quartz.config;

import com.quartz.demo.quartz.QuartzTaskScheduler;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties({QuartzDataSource.class})
@AutoConfigureOrder
@ConditionalOnProperty(name = {"quartz.enabled"})
public class QuartzConfig {
    @Value("${quartz.instanceName}")
    private String  quartzInstanceName;
    @Value("${quartz.tablePrefix:QRTZ_}")
    private String  tablePrefix;
    @Value("${quartz.executor.enable:true}")
    private boolean executorEnable;

    @Autowired
    SpringJobFactory springJobFactory;

    public QuartzConfig(){}

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean){
        return schedulerFactoryBean.getScheduler();
    }

    @Bean
    public Properties quartzProperties(QuartzDataSource dataSource){
        Properties properties = new Properties();
        properties.put("quartz.scheduler.instanceName", this.quartzInstanceName);
        properties.put("org.quartz.scheduler.instanceId", "AUTO");
        properties.put("org.quartz.scheduler.skipUpdateCheck", "true");
        properties.put("org.quartz.scheduler.jmx.export", "true");

        properties.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        properties.put("org.quartz.jobStore.tablePrefix", this.tablePrefix);
        properties.put("org.quartz.jobStore.isClustered", "true");
        properties.put("org.quartz.jobStore.useProperties", "true");
        properties.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
        properties.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");
        properties.put("org.quartz.jobStore.misfireThreshold", "60000");
        properties.put("org.quartz.jobStore.txIsolationLevelSerializable", "false");

        if (this.executorEnable){
            properties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
            properties.put("org.quartz.threadPool.threadCount", "10");
            properties.put("org.quartz.threadExecutor.class", "org.quartz.impl.DefaultThreadExecutor");
        }else {
            properties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
            properties.put("org.quartz.threadPool.threadCount", "10");
            properties.put("org.quartz.threadExecutor.class", "com.quartz.demo.quartz.DryRunThreadExecutor");
        }
        properties.put("org.quartz.threadPool.threadPriority", "5");
        properties.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        properties.put("org.quartz.jobStore.dataSource", "myDS");
        properties.put("org.quartz.dataSource.myDS.driver",     dataSource.getDriver());
        properties.put("org.quartz.dataSource.myDS.URL",        dataSource.getUrl());
        properties.put("org.quartz.dataSource.myDS.user",       dataSource.getUser());
        properties.put("org.quartz.dataSource.myDS.password",   dataSource.getPassword());
        properties.put("org.quartz.dataSource.myDS.maxConnections", dataSource.getMaxConnections());
        properties.put("org.quartz.dataSource.myDS.maxIdleTime", "1800");
        return properties;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(QuartzDataSource quartzDataSource, JobFactory jobFactory){
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobFactory(jobFactory);
        factoryBean.setOverwriteExistingJobs(true);
        factoryBean.setStartupDelay(10);
        factoryBean.setQuartzProperties(this.quartzProperties(quartzDataSource));
        factoryBean.setAutoStartup(true);
        return factoryBean;
    }

    @Bean
    public QuartzTaskScheduler quartzTaskScheduler(Scheduler scheduler) {
        return new QuartzTaskScheduler(scheduler);
    }


//    @Bean
//    public JobDeleteProcesser jobDeleteProcesser() {
//        return new JobDeleteProcesser();
//    }

}
