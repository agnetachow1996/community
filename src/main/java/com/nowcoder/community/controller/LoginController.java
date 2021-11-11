package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserSerivce;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

//用户注册有问题
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserSerivce userSerivce;

    @Autowired
    private Producer kaptcha;

    //这里设置变量，将applicationProperties中的
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String register(){
        return "/site/register";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login(){
        return "/site/login";
    }

    //提交数据时处理的请求
    @RequestMapping(path="/register",method = RequestMethod.POST)
    public String register(Model model, User user) throws IllegalAccessException {
        Map<String,Object> result = userSerivce.register(user);
        if (result == null || result.isEmpty()){
            model.addAttribute("msg","注册成功，已发送激活邮件请查收");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg", result.get("usernameMsg"));
            model.addAttribute("passwordMsg", result.get("passwordMsg"));
            model.addAttribute("emailMsg", result.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "http://localhost:8080/community/{userID}/code", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userID")int userID,@PathVariable("code")String code){
        int result = userSerivce.activation(userID,code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","您已激活成功。");
            model.addAttribute("target","/login");
        }else if(result == ACTIVATION_FAILURE){
            model.addAttribute("msg","激活失败。");
            model.addAttribute("target","/index");
            //return "/site/operate-result";
        }else{
            model.addAttribute("msg","重复激活。");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }
    //对于验证码的部分，需要单独再写一个请求，因为login函数的作用是向容器返回一个页面，而当服务器再解析界面时，
    //遇到访问路径会再次访问服务器，因此验证码需要单独再写一个函数。
    @RequestMapping(path="/kaptcha",method=RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        String text = kaptcha.createText();
        BufferedImage image = kaptcha.createImage(text);
        //将验证码传入session
        session.setAttribute("kaptcha",text);

        //将数据传给浏览器
        response.setContentType("image/png");
        // 上面这句话是客户端浏览器，区分不同种类的数据，
        // 并根据不同的MIME调用浏览器内不同的程序嵌入模块来处理相应的数据。
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("图片响应出错："+e.getMessage());
        }
    }

    //这个和上面那个login不冲突是因为method用的不一样。
    //其中httpSession是session，response是cookie
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String username,String password,String code,boolean rememberme,
                        Model model,HttpSession httpSession,HttpServletResponse response){
        String kaptcha = (String)httpSession.getAttribute("kaptcha");
        //验证码为空的逻辑
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误");
            return "/site/login";
        }
        //检查账号密码
        int expiredSeconds = rememberme?
                CommunityConstant.DEAFAULT_EXPIRED_TIME:CommunityConstant.DEAFAULT_EXPIRED_TIME;
        Map<String,Object> result =
                userSerivce.login(username,password,expiredSeconds);

        //登录成功状态监测
        if(result.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",result.get("ticket").toString());
            //设置凭证有效的路径，这里cookie应该是整个项目有效，所以这里的路径写项目路径
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",result.get("usernameMsg"));
            model.addAttribute("passwordMsg",result.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    //@CookieValue注解：springMVC传过来的ticket值，利用spring注解重新注入
    public String logout(@CookieValue("ticket") String ticket){
        userSerivce.logout(ticket);
        //重定向时默认是选择get请求的login
        return "redirect:/login";
    }
}
