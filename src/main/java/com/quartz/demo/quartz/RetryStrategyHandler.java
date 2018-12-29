package com.quartz.demo.quartz;

public class RetryStrategyHandler {

    public RetryStrategyHandler() {
    }

    public static long getExecuteTime(QuartzJob quartzJob, int currentCount, long lastTime) {
        return lastTime == -1L ? 0L : lastTime + getActualInteval(currentCount, quartzJob.repeatInterval(), quartzJob.retryStrategy()) * 1000L;
    }

    private static long getActualInteval(int currentCount, int interval, RetryStrategy retryStrategy) {
        switch(retryStrategy) {
            case Fibonacci:
                return getFibo(currentCount) * (long)interval;
            case ArithmeticProgression:
                return (long)((currentCount - 1) * interval);
            case Constant:
            default:
                return (long)interval;
        }
    }

    private static long getFibo(int i) {
        return i != 1 && i != 2 ? getFibo(i - 1) + getFibo(i - 2) : 1L;
    }
}
