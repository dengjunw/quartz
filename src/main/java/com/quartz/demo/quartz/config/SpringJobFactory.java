package com.quartz.demo.quartz.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

@Component
public class SpringJobFactory extends AdaptableJobFactory {
    @Autowired
    AutowireCapableBeanFactory capableBeanFactory;

    public SpringJobFactory(){}

    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        this.capableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}
