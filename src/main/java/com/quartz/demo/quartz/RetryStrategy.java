package com.quartz.demo.quartz;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public enum  RetryStrategy {

    Constant(0, "常数数列"),
    Fibonacci(1, "斐波那契"),
    ArithmeticProgression(2, "等差数列");

    int type;
    String desc;

    private RetryStrategy(int name, String desc) {
        this.type = name;
        this.desc = desc;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
