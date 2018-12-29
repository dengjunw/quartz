package com.quartz.demo.demo;

import com.alibaba.fastjson.JSONObject;
import com.quartz.demo.quartz.QuartzJob;
import com.quartz.demo.quartz.QuartzTaskScheduler;
import com.quartz.demo.quartz.RepeatJob;
import com.quartz.demo.quartz.RetryStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;


@Component
@Slf4j
@QuartzJob(
        retryStrategy = RetryStrategy.Constant,
        repeatInterval = 300,
        repeatCount = 3,
        startDelay = 1
)
public class JobTest extends RepeatJob {

    @Autowired
    private QuartzTaskScheduler scheduler;
    String JOB_KEY = "job_test";

    @Override
    public boolean process(JSONObject var1) {
        log.info("result :{}", var1);
        return false;
    }

    public void submitTestJob(Long testId, Date nextTime) {
        log.info("adding test job to quartz,test id is {}.", testId);
        JSONObject data = new JSONObject();
        data.put(JOB_KEY, testId);
        scheduler.submitJob(JobTest.class, data, Optional.of(String.valueOf(testId)), Optional.of(nextTime));
    }
    public void deleteTestJob(Long testId) {
        scheduler.deleteJob(JobTest.class, String.valueOf(testId));
    }
}
