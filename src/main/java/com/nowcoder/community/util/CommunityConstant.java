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
}
