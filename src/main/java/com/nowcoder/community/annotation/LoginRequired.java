package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//这个注解用于说明网页是否需要登录状态才能访问
//加了注解的方法会运行拦截器里面的指定方法
//该注解用来描述方法的
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) //说明注解的作用在什么时期
public @interface LoginRequired {
}
