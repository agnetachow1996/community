package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class) //让测试运行于Spring测试环境
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@MapperScan("com.nowcoder.community.mapper")
public class RedisTest {
    // 统计20W个重复数据的独立整数
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testHyperLog(){

    }

}
