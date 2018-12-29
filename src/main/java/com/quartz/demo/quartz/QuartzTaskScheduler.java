package com.quartz.demo.quartz;

import com.alibaba.fastjson.JSONObject;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class QuartzTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(QuartzTaskScheduler.class);
    Scheduler scheduler;

    public QuartzTaskScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    private String getTimeStr(Date currentTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(currentTime);
    }

    private <T extends RepeatJob> QuartzJob getTriggerInfo(Class<T> clazz) {
        QuartzJob[] jobInfo = (QuartzJob[])clazz.getAnnotationsByType(QuartzJob.class);
        return jobInfo[0];
    }

    public <T extends RepeatJob> void submitJob(Class<T> clazz, JSONObject jsonObject, Optional<String> jobId, Optional<Date> nextRuntime) {
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }

        QuartzJob jobInfo = this.getTriggerInfo(clazz);
        String name = clazz.getSimpleName() + "_" + (String)jobId.orElse(this.getTimeStr(new Date()) + "_" + JobId.id());
        JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(name, jobInfo.group()).storeDurably(jobInfo.durability()).build();
        Date delayRuntime = DateBuilder.nextGivenSecondDate(new Date(), jobInfo.startDelay());
        Date runTime = (Date)nextRuntime.orElse(delayRuntime);
        SimpleTrigger trigger = (SimpleTrigger)TriggerBuilder.newTrigger().withIdentity(name, jobInfo.group()).startAt(runTime).withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(jobInfo.repeatInterval()).repeatForever()).build();
        jobDetail.getJobDataMap().put("details", jsonObject.toJSONString());
        jobDetail.getJobDataMap().put("count", String.valueOf(1));

        try {
            Date scheduleTime = this.scheduler.scheduleJob(jobDetail, trigger);
            log.info(jobDetail.getKey().toString() + " will run at: " + scheduleTime + ", and retryStrategy: " + jobInfo.retryStrategy().desc + ", repeatInterval: " + trigger.getRepeatInterval() / 1000L + " seconds");
        } catch (ObjectAlreadyExistsException var12) {
            log.info("job with identification already exists. jobname = {}", name);
        } catch (SchedulerException var13) {
            log.error("fail to scheduler job .", var13);
        }

    }

    public void deleteJob(Class clazz, String jobId) {
        String name = clazz.getSimpleName() + "_" + jobId;
        QuartzJob jobInfo = this.getTriggerInfo(clazz);

        try {
            this.scheduler.deleteJob(JobKey.jobKey(name, jobInfo.group()));
            log.info("{} deleted by user.", name);
        } catch (SchedulerException var6) {
            log.error("fail to delete job {}", name);
        }

    }
}
