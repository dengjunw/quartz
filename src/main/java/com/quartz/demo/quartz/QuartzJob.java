package com.quartz.demo.quartz;

import com.quartz.demo.quartz.RetryStrategy;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface QuartzJob {

    String group() default "default";

    int startDelay() default 5;

    int repeatInterval() default 5;

    int repeatCount() default -1;

    boolean durability() default true;

    RetryStrategy retryStrategy() default RetryStrategy.ArithmeticProgression;
}
