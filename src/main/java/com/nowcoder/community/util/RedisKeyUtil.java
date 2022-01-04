package com.nowcoder.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    //前缀
    private static final String PREFIX_ENTITU_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
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

    //登录验证码，验证码和用户是相关的，不同的用户验证码不同，但此时用户没有登录
    //不可能直接获取用户名，所以这里用随机字符串标记用户
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //登录的凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

}
