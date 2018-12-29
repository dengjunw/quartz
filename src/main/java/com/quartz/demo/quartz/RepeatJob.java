package com.quartz.demo.quartz;

import com.alibaba.fastjson.JSONObject;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@QuartzJob
public abstract class RepeatJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(RepeatJob.class);
    @Autowired(
            required = false
    )
    JobDeleteProcesser jobDeleteProcesser;
    private JSONObject _details;

    public RepeatJob() {
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        MDC.put("traceId", "QTZ_" + JobId.id());
        JobKey jobKey = context.getJobDetail().getKey();
        JobDataMap data = context.getJobDetail().getJobDataMap();
        int count = data.getInt("count");
        long lastTime = data.containsKey("last_time") ? data.getLong("last_time") : -1L;
        QuartzJob jobInfo = this.getTriggerInfo();
        if (this.shouldRun(jobInfo, count, lastTime)) {
            log.info("execute {} for the {} times . job details = {}", new Object[]{jobKey, count, data.getString("details")});
            this._details = JSONObject.parseObject(data.getString("details"));
            boolean result = false;

            try {
                result = this.process(this._details);
            } catch (Throwable var10) {
                log.info("fail to execute job {}, error = {}", jobKey.getName(), var10.getMessage());
            }

            if (result) {
                log.info("{} is delete because it is done.", jobKey);
                this.deleteJob(context, result, this._details);
                return;
            }

            if (count++ == jobInfo.repeatCount()) {
                log.error("{} is delete because max execute count reached. job details = {} ", jobKey, this._details);
                this.deleteJob(context, result, this._details);
                return;
            }

            lastTime = System.currentTimeMillis();
            data.put("count", String.valueOf(count));
            data.put("details", this._details.toJSONString());
            data.put("last_time", String.valueOf(lastTime));
        }

    }

    public boolean shouldRun(QuartzJob quartzJob, int currentCount, long lastTime) {
        long shouldExecuteTime = RetryStrategyHandler.getExecuteTime(quartzJob, currentCount, lastTime);
        long now = System.currentTimeMillis();
        return currentCount <= quartzJob.repeatCount() && shouldExecuteTime <= now + 1000L;
    }

    public abstract boolean process(JSONObject var1);

    void deleteJob(JobExecutionContext context, boolean reason, JSONObject details) {
        JobKey jobKey = context.getJobDetail().getKey();

        try {
            context.getScheduler().deleteJob(jobKey);
            if (this.jobDeleteProcesser != null) {
                this.jobDeleteProcesser.processDeleteEvent(jobKey.getName(), details, reason);
            }
        } catch (SchedulerException var6) {
            log.error("{} delete fail.", jobKey);
        }

    }

    protected QuartzJob getTriggerInfo() {
        QuartzJob[] jobInfo = (QuartzJob[])this.getClass().getAnnotationsByType(QuartzJob.class);
        return jobInfo[0];
    }
}
