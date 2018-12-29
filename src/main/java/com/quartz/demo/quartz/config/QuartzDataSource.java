package com.quartz.demo.quartz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "quartz.datasource")
@Data
public class QuartzDataSource {
    private String driver;
    private String url;
    private String user;
    private String password;
    private Integer maxConnections = 10;

    public QuartzDataSource(){}
}
