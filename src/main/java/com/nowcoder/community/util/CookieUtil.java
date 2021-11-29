package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    public static String getValue(HttpServletRequest httpServletRequest, String name){
        if(httpServletRequest == null || name == null){
            throw new IllegalArgumentException("参数为空");
        }
        //获取所有cookie对象
        Cookie[] cookies = httpServletRequest.getCookies();
        //然后挨个找里面的cookie都有没有是属于name的
        if(cookies != null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals(name)){
                    //返回cookie值
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
