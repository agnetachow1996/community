package com.nowcoder.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class User {
    @TableId("id")
    private String id;

    @TableField("username")
    private String userName;

    @TableField("password")
    private String password;

    @TableField("salt")
    private String salt;

    @TableField("email")
    private String email;
    private int type;
    private int status;

    @TableField("activation_code")
    private String activationCode;

    @TableField("header_url")
    private String headerUrl;

    @TableField("create_time")
    private Timestamp createTime;
}
