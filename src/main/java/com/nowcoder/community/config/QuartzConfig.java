package com.nowcoder.community.config;

import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//该类用于配置quartZ，一次配置完成后，
// 将配置信息存储到数据库中，下次使用时直接调用数据库中的信息
@Configuration
public class QuartzConfig {
    //问，一般bean注解是修饰类的，为什么修饰该方法却不报错？
    //答：FactoryBean的作用是可简化bean的实例化过程
    // FactoryBean的实例化bean过程有：
    // 1.通过FactoryBean封装bean的实例化过程
    // 2.FactoryBean装配到spring容器内
    // 3.将FactoryBean注入给其他的bean
    // 4.该bean得到的是FacoryBean所管理的对象实例
    @Bean
    public JobDetailFactoryBean alphaJobDetail(){
        return null;
    }

    //配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
    @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail){
        return null;
    }
}
