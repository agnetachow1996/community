package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;
    //点赞
    public void like(int userId, int entityType, int entityId,int entityUserId){
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
//        //判断该用户是否点过赞，如果点过赞，再点赞就取消赞
//        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);
//        if(isMember){
//            redisTemplate.opsForSet().remove(entityLikeKey,userId);
//        }else{
//            redisTemplate.opsForSet().add(entityLikeKey,userId);
//        }
        //redis重构事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                //某个用户对某个用户的点赞
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                //判断当前用户是否给实体点赞了
                boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey,userId);
                redisOperations.multi();
                if(isMember){
                    //如果点了赞又点，取消赞，同时redis中那个人的获赞数也要-1
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                }else{
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();
            }
        });
    }

    //查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某人对某实体的点赞状态,点赞的实体有帖子，有回复等
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1:0;
    }

    //查询某个用户获得的赞
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer result = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return result == null? 0:result;
    }
}
