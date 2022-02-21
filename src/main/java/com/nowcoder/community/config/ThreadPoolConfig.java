package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling  //定时任务启动
@EnableAsync // Async注解：支持方法在多线程下能够被异步调用
public class ThreadPoolConfig {
}
