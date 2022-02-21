package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RunWith(SpringRunner.class) //让测试运行于Spring测试环境
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@MapperScan("com.nowcoder.community.mapper")
public class ThreadPoolTest {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    //JDK普通线程池,并反复复用线程池里的线程
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    //JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(5);

    private void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 1.JDK普通线程
    }

    @Test
    public void testExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello Executor!");
            }
        };
        for(int i = 0;i < 10;i++){
            executorService.submit(task);
        }
        sleep(10000);
    }

    @Test
    private void testThreadPoolTaskSchedular(){
        Runnable task = new Runnable() {
            @Override
            public void run() {

            }
        };
    }
}
