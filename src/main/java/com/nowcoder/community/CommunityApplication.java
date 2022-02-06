package com.nowcoder.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

//先得在启动类里扫描 Mapper 类，即添加 @MapperScan 注解
@MapperScan("com.nowcoder.community.mapper")
@SpringBootApplication
public class CommunityApplication {
	//在构造器调用完成之后调用该函数
	/**
	 * 这个方法的作用是解决spring与elasticsearch之间的冲突
	 * 冲突主要在于NettyRuntime中的setProcessAvailable中
	 * 如果已经设置了可用的处理器，再次调用会报错
	 * spring在加载完bean时会调用该方法，elasticsearch再调用后，
	 * 就会报错，因此这里需要提前设置属性，防止冲突。
	 * 解决方案参见netty4utils.setProcessAvailable**/
	@PostConstruct
	public void init(){
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}
	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}
}
