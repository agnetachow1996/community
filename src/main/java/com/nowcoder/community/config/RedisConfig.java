package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    //当你在方法前面写了Bean时，spring容器会自动将方法里面的Factory参数装配
    //去redis安装目录下运行redis-server.exe redis.windows.conf
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> template = new RedisTemplate();
        template.setConnectionFactory(factory);
        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置value的序列化方式,建议一般用json，因为value是结构化的
        template.setValueSerializer(RedisSerializer.json());
        //设施hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设施hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        //触发生效
        template.afterPropertiesSet();
        return template;
    }
}
