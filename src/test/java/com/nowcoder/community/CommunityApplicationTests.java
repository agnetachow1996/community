package com.nowcoder.community;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.ESClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class) //让测试运行于Spring测试环境
//@SpringBootTest
//@ContextConfiguration(classes = CommunityApplication.class)
//@MapperScan("com.nowcoder.community.mapper")
//加载配置文件,communityApplication本身是一个配置文件，此注解通常联合runwith注解使用
public class CommunityApplicationTests implements ApplicationContextAware {

	//记录当前applicationContext的运行环境
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext(){
		AlphaService alphaService = this.applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService.find());
	}

	@Test
	public void testConnect(){

	    RestHighLevelClient client = ESClient.getClient();
        ESClient.closeClient(client);
	}


}
