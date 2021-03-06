package com.nowcoder.community.service;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.UserMapper;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserSerivce implements CommunityConstant, UserDetailsService {

    //this is mybatis
    @Autowired
    private UserMapper userMapperMybatis;

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

    public User selectUserByID(int id) {
        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
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
            cleanCache(userId);
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
        redisTemplate.opsForValue().set(redisKey,loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        //登出时，将ticket的有效状态改变成1
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket ticket1 = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        ticket1.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,ticket1);
    }

    //从数据库中查出来ticket的值
    public LoginTicket getTicketValue(String ticket){
        //return loginTicketMapper.selectLoginTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeaderUrl(int userId,String headerUrl){
        int row = userMapperMybatis.updateHeader(userId,headerUrl);
        cleanCache(userId);
        return row;
    }

    public User findUserByName(String userName){
        return userMapperMybatis.selectUserByUsername(userName);
    }

    //对于一些频繁访问的方法采用redis重构
    //1.优先从缓存中取值
    private User getCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        //redisTemplate.setValueSerializer(new StringRedisSerializer());
        return (User) redisTemplate.opsForValue().get(userKey);
    }
    //2. 取不到值时初始化缓存数据
    private User initCache(int userId){
        User user = userMapperMybatis.selectUserById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    //3. 数据变更时清除缓存数据
    private void cleanCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    //该方法的功能是根据用户名查用户
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return this.findUserByName(userName);
    }

    // 查询用户权限
    public Collection<? extends GrantedAuthority> getAuthorities(int userId){
        User user = this.selectUserByID(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority(){
            @Override
            public String getAuthority() {
                switch (user.getType()){
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }
}
