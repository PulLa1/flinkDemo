package com.yz.util;

public class Constants {


    public static final String PULL_DATA_KEY  = "TestKey";



    /**
     * influxdb相关配置
     */
    public static final String INFLUXDB_USER_NAME = "admin";
    public static final String INFLUXDB_PASSWORD = "123456";
    public static final String INFLUXDB_URL = "http://123.56.70.178:8086";
    public static final String INFLUXDB_DATABASE = "opcTest";

    /**
     * redis
     * 配置
     */
    public static final String REDIS_HOSET = "192.168.8.100";
    public static final Integer REDIS_PORT = 6379;
    public static final String REDIS_PASSWORD = "redis-pwd";
    public static final Integer REDIS_DATABASE = 3;
    public static final Integer REDIS_TIMEOUT = 5*1000;
    public static final Integer REDIS_MAX_ACTIVE =100;
    public static final Integer REDIS_MAX_IDLE = 300;
    public static final Integer REDIS_MIN_IDLE = 0;
    public static final Integer REDIS_MAX_WAIT = -1;
}
