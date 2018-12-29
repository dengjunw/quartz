package com.quartz.demo.quartz;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JobDeleteProcesser implements CommandLineRunner {

    @Autowired
    AbstractApplicationContext applicationContext;

    private List<JobDeleteHandler> eventHandlers;

    public JobDeleteProcesser() {
    }

    @Override
    public void run(String... args) throws Exception {
        this.eventHandlers = (List)this.getBeanNamesByTypeWithAnnotation(EventHandler.class, JobDeleteHandler.class).map((name) -> {
            return (JobDeleteHandler)this.applicationContext.getBeanFactory().getBean(name, JobDeleteHandler.class);
        }).collect(Collectors.toList());
    }

    public void processDeleteEvent(String name, JSONObject jobDetail, boolean success) {
        this.eventHandlers.forEach((one) -> {
            one.dealJobDeleteEvent(name, jobDetail, success);
        });
    }

    private <T> Stream<String> getBeanNamesByTypeWithAnnotation(Class<? extends Annotation> annotationType, Class<T> beanType) throws Exception {
        return Stream.of(this.applicationContext.getBeanNamesForType(beanType)).filter((name) -> {
            BeanDefinition beanDefinition = this.applicationContext.getBeanFactory().getBeanDefinition(name);
            Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(annotationType);
            if (!beansWithAnnotation.isEmpty()) {
                return beansWithAnnotation.containsKey(name);
            } else if (beanDefinition.getSource() instanceof StandardMethodMetadata) {
                StandardMethodMetadata metadata = (StandardMethodMetadata)beanDefinition.getSource();
                return metadata.isAnnotated(annotationType.getName());
            } else {
                return false;
            }
        });
    }
}
