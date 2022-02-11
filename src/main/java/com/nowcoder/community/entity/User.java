package com.nowcoder.community.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
public class User implements UserDetails {
    private int id;

    private String userName;

    private String password;

    private String salt;

    private String email;
    private int type;

    private int status;

    private String activationCode;

    private String headerUrl;

    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getSalt() {
        return salt;
    }

    public String getEmail() {
        return email;
    }

    public int getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return null;
    }

    //SpringSecurity需要实现的方法
    //true:账号未过期
    //具体含义可以查一下
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //凭证未过期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //获取权限，springSecurity中的权限都是字符串
    //多种用户需要返回权限集合
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authList = new ArrayList<>();
        authList.add((GrantedAuthority) () -> {
            switch (type){
                case 1:return "ADMIN";
                default:return "USER";
            }
        });
        return null;
    }

}
