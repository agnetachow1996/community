package com.nowcoder.community.util;

public interface CommunityConstant {
    /*
    * 激活注册
    * */

    //注册成功
    int ACTIVATION_SUCCESS = 0;
    //重复注册
    int ACTIVATION_REPEAT = 1;
    //注册失败
    int ACTIVATION_FAILURE = 2;
    //默认登录凭证有效时间->12个小时
    int DEAFAULT_EXPIRED_TIME = 3600 * 12;
    //勾选记住后，登录凭证的有效时间->100天
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
    /*
    * 实体类型：帖子
    * */
    int ENTITY_TYPE_POST = 1;
    /*
    * 实体类型：回复
    * */
    int ENTITY_TYPE_COMMENT = 2;
    /*
     * 实体类型：用户
     * */
    int ENTITY_TYPE_USER = 3;
    /*
    * kafka主题：评论
    * */
    String TOPIC_COMMENT = "comment";
    /*
    * kafka主题：点赞
    * */
    String TOPIC_LIKE = "like";
    /*
    kafka主题：评论
    * */
    String TOPIC_FOLLOW = "follow";
    /*
    kafka主题：发帖
    **/
    String TOPIC_PUBLISH = "publish";
    /*
    * 系统用户ID
    * */
    int SYSTEM_USER_ID = 1;
    /*
    *权限：普通用户
    * */
    String AUTHORITY_USER = "user";
    /*
     *权限：管理员
     * */
    String AUTHORITY_ADMIN = "admin";
    /*
     *权限：版主
     * */
    String AUTHORITY_MODERATOR = "moderator";
    /**
     * 主题：删除
     * */
    String TOPIC_DELETE = "delete";
}
