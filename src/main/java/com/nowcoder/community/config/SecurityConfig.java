package com.nowcoder.community.config;

import com.mysql.cj.exceptions.PasswordExpiredException;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserSerivce;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    @Autowired
    private UserSerivce userSerivce;

    @Override
    public void configure(WebSecurity web) throws Exception{
        // 忽略静态资源的访问
        web.ignoring().antMatchers("/resource/**");
    }

    // 权限管理一般两个操作：授权和认证
    // 该方法是对认证做操作
    // AuthenticationManager: 认证的核心接口
    // AuthenticationManagerBuilder： 用于构造该认证接口的工具
    // ProviderManager: AuthenticationManager接口的默认构造类
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //内置的认证规则
        // Pbkdf2PasswordEncoder这个类似于设置salt，这个是固定认证方式，不适应现在的系统
        // auth.userDetailsService(userSerivce).passwordEncoder(new Pbkdf2PasswordEncoder("12345"));

        // 自定义认证规则
        // AuthenticationProvider： providerManager持有一组AuthenticationProvider，
        // 每个AuthenticationProvider负责一种认证方法
        // 委托模式： providerManager将认证委托给AuthenticationProvider
        auth.authenticationProvider(new AuthenticationProvider() {
            // Authentication 用于封装认证信息的接口，认证信息比如账号密码等，
            // 不同的实现类代表不同的认证信息
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String userName = authentication.getName();
                String password = (String) authentication.getCredentials();
                User user = userSerivce.findUserByName(userName);
                if(user == null){
                    throw new UsernameNotFoundException("账号不存在！");
                }
                //将密码转换成MD5格式
                password = CommunityUtil.MD5(user.getPassword());
                if(!user.getPassword().equals(password)){
                    throw new BadCredentialsException("密码不正确！");
                }
                // 表示认证成功了
                // 第一个参数principle: 主要信息，一般存放等于用户user
                // 第二个参数credentials：证书，通常是密码信息
                // 第三个参数authorities：集合类，该集合用于确认不同用户的权限级别，之前实现的那个接口
                return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            }

            // 体现的是认证类型，该方法说明当前AuthenticationProvider支持哪种类型
            @Override
            public boolean supports(Class<?> aClass) {
                //UsernamePasswordAuthenticationToken：是Authentication的接口实现类
                return UsernamePasswordAuthenticationToken.class.equals(aClass);
            }

        });
    }

    //该方法用于设置登录相关配置
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        // 一旦写上super.configure(http)，就会拦截所有的请求并要求认证，因此要覆盖父类的configure
        //super.configure(http);

        //设置需要登录才能访问的页面
        http.authorizeRequests()
                .antMatchers("/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow")
                .hasAnyAuthority(AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR)
                .anyRequest().permitAll();

        // 两种处理情况
        http.exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
            // 未登录的处理
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                // 获取请求的消息头
                String xRequestWith = request.getHeader("x-requested-with");
                // 如果消息是异步请求
                if("XMLHttpRequest".equals(xRequestWith)){
                    // 服务器相应普通的字符串
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(CommunityUtil.getJsonString(403,"您未登录！"));
                }else{
                    response.sendRedirect(request.getContextPath() + "/login");
                }
            }
        }).accessDeniedHandler(new AccessDeniedHandler() {
            // 登录了但是权限不够
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                // 获取请求的消息头
                String xRequestWith = request.getHeader("x-requested-with");
                // 如果消息是异步请求
                if("XMLHttpRequest".equals(xRequestWith)){
                    // 服务器相应普通的字符串
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(CommunityUtil.getJsonString(403,"您权限不足！"));
                }else{
                    response.sendRedirect(request.getContextPath() + "/denied");
                }
            }
        });

        // 登录表单的配置
        http.formLogin().
                loginPage("/login") // 登录请求处理方法
                .loginProcessingUrl("/login") //登录请求处理的URL;
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

                    }
                }) //登录成功处理
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {

                    }
                }); //登录失败处理
    }
}
