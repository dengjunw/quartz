package com.quartz.demo.quartz;

import org.quartz.spi.ThreadExecutor;

public class DryRunThreadExecutor implements ThreadExecutor {
    public DryRunThreadExecutor(){

    }

    @Override
    public void execute(Thread thread) {
    }

    @Override
    public void initialize() {

    }
}
