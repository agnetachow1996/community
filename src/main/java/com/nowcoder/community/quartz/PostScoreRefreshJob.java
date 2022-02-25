package com.nowcoder.community.quartz;

import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.service.DiscussService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job, CommunityConstant {

    private  static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LikeService likeService;

    @Autowired
    private DiscussService discussService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    //牛客纪元
    private static final Date epoch;
    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化初始时间失败！",e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String scoreKey = RedisKeyUtil.getPostScoreKey();
        // 批量处理
        BoundSetOperations operations = redisTemplate.boundSetOps(scoreKey);
        if(operations.size()==0){
            logger.info("[任务取消] 没有需要刷新的帖子！");
            return;
        }
        logger.info("[任务开始] 正在刷新帖子分数：" +operations.size());
        while (operations.size()>0){
            //每刷新一次帖子就删除一次操作
            this.refresh((Integer)operations.pop());
        }
        // 用完清空 避免占用内容过大
        redisTemplate.delete(scoreKey);
        logger.info("[任务结束] 帖子分数已刷新");
    }

    private void refresh(Integer postId) {
        Discuss post = discussService.findDiscussByID(postId);
        if(post==null){
            logger.error("该帖子不存在： id = " + postId);
            return;
        }
        boolean wonderful = post.getStatus() == 1;
        int commentCount = post.getCommentCount();
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,postId);

        //先计算权重
        double w = (wonderful?75:0) + commentCount * 10 + likeCount * 2;
        // 分数 = 权重+ 距离天数，防止分数为负
        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        // 更新帖子分数
        discussService.updateScore(postId, score);
        // 同步搜索数据
        post.setScore(score);
        //搜索引擎获取到了全新的数据，防止搜索到陈旧的数据
        elasticsearchService.saveDiscuss(post);
    }
}
