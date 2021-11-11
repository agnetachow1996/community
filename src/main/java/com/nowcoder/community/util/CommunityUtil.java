package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    //该类用于生成随机字符串
    public static String generateUUID(){
        //利用java工具包生成随机字符串，同时去掉字符串中所有的短横线
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密，用户注册的密码全部MD5加密,该算法只能加密不能解密
    //为了防止破解，再加上salt，字符串+salt较难破解
    public static String MD5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
