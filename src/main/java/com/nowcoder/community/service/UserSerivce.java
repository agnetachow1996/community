package com.nowcoder.community.service;

import com.nowcoder.community.mapper.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.UserMapperMybatis;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

@Service
public class UserSerivce implements CommunityConstant {

    //this is mybatis
    @Autowired
    private UserMapperMybatis userMapperMybatis;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User selectUserByID(int ID) {
        return userMapperMybatis.selectUserById(ID);
    }


    //实现注册功能
    public Map<String,Object> register(User user) throws IllegalAccessException {
        Map<String,Object> map = new HashMap<>();
        if (user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //逻辑判断用户注册时的各类属性
        if(user.getUserName() == null){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(user.getEmail() == null){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        if(user.getPassword() == null){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //验证用户,判断用户名和邮箱地址是否曾经注册过
        User u = userMapperMybatis.selectUserByUsername(user.getUserName());
        if(u != null){
            map.put("usernameMsg","用户名重复");
            return map;
        }
        u = userMapperMybatis.selectUserByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","邮箱不能重复");
            return map;
        }
        //注册用户，需要搞个5位的salt
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.MD5(user.getPassword()));
        //状态是否被激活,没收到验证码的状态都是0，发送验证码激活
        user.setStatus(0);
        //设置用户激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        //普通注册用户为0
        user.setType(0);
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        //调用insert语句之后mybatis-plus自动增加id(不一定，可以试一下)
        userMapperMybatis.insertUser(user);

        //注册完毕之后给用户发送邮件，其中activation是激活邮件的模板
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //然后需要动态地拼接用户注册时需要点击的链接
        //http://localhost:8080/community/activation/
        String url = domain + contextPath + "activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        //邮件内容，通过模板引擎处理了
        String content = templateEngine.process("/mail/activation",context);
        mailClient.setJavaMailSender(content,user.getEmail(),"周周网账号激活");
        return map;
    }

    //用户激活
    public int activation(int userId,String code){
        User user = userMapperMybatis.selectUserById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }
        //新增用户的code和传入的code相同
        else if(user.getActivationCode().equals(code)){
            userMapperMybatis.updateStatus(1,userId);
            return ACTIVATION_SUCCESS;
        }
        else{
            return ACTIVATION_FAILURE;
        }
    }

    //实现登录功能
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map = new HashMap<>();
        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //验证账号
        User user = userMapperMybatis.selectUserByUsername(username);
        if(user == null){
            map.put("userMsg","用户不存在");
            return map;
        }
        if(user.getActivationCode() == "0"){
            map.put("activationMsg","用户未激活");
            return map;
        }
        String pwd = CommunityUtil.MD5(password);
        if(!pwd.equals(user.getPassword())){
            map.put("loginMsg","密码错误");
            return map;
        }
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        //登出时，将ticket的有效状态改变成1
        loginTicketMapper.updateLoginTicket(ticket,1);
    }

    //从数据库中查出来ticket的值
    public LoginTicket getTicketValue(String ticket){
        return loginTicketMapper.selectLoginTicket(ticket);
    }

    public int updateHeaderUrl(int userId,String headerUrl){
        return userMapperMybatis.updateHeader(headerUrl,userId);
    }

    public User findUserByName(String userName){
        return userMapperMybatis.selectUserByUsername(userName);
    }
}
