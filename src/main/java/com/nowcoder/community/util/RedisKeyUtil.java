package com.nowcoder.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    //前缀
    private static final String PREFIX_ENTITU_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    //某个实体的赞
    //like:entity:entityType:entityID --> set(userID)
    //使用set集合，集合里面装的是userID,这样之后需要获取谁点赞了也能实现
    public static String getEntityLikeKey(int entityType,int entityID){
        return PREFIX_ENTITU_LIKE + SPLIT + entityType + entityID;
    }
    //某个用户的赞
    //like:user:userId --> int
    public static String getUserLikeKey(int userID){
        return PREFIX_USER_LIKE + SPLIT + userID;
    }

    //某个用户关注的实体
    //key是followee:userId:entityType，value是entityId，now是排序条件
    //followee:userId:entityType --> zSet(entityId,now)
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //某个实体拥有的粉丝
    //follower:entityType:entityId --> zSet(userId,now)
    public static String getFollowerKey(int entityId,int entityType){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

}
