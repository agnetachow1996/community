package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserSerivce;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

//拦截器,这里需要在模板引擎加载完成之后，
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserSerivce userSerivce;
    @Autowired
    private HostHolder hostHolder;


    //在请求开始之初，设置好线程用于保持用户的状态
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        //从cookie中获取凭证,cookie的登录信息凭证都用ticket标注了
        String ticket = CookieUtil.getValue(request,"ticket");
        if(ticket != null){
            //查询凭证
            LoginTicket loginTicket = userSerivce.getTicketValue(ticket);
            //判断凭证是否还有效
            if(loginTicket != null && loginTicket.getExpired().after(new Date()) &&
                    loginTicket.getStatus() == 0){
                User user = userSerivce.selectUserByID(loginTicket.getUserId());
                //在本次请求中持有用户,hostHolder创建一个线程，用户的键值对
                hostHolder.setUsers(user);
               // System.out.println("创建用户线程");
            }
        }
        return true;
    }
    //该方法是在模板引擎加载之前运行，因此需要在模板运行时加入用户信息，这样登录时可以加载出来
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
           // System.out.println("登录用户成功");
        }
    }
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 Exception ex) {
        //在请求结束后清除线程。
        //System.out.println("删除用户线程");
        hostHolder.clear();
    }


}
