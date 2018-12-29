package com.quartz.demo.quartz;

import com.alibaba.fastjson.JSONObject;

public abstract class JobDeleteHandler {

    public JobDeleteHandler() {
    }

    public abstract void dealJobDeleteEvent(String var1, JSONObject var2, boolean var3);
}
