package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class) //让测试运行于Spring测试环境
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@MapperScan("com.nowcoder.community.mapper")
public class SensitiveTest {
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Test
    public void testFilter(){
        String text = "杀人开票";
        String s = sensitiveFilter.filter(text);
        System.out.println(s);
    }

}
