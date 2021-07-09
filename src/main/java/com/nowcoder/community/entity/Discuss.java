package com.nowcoder.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

@Data
@TableName("discuss_post")
public class Discuss {
    @TableField("id")
    private String id;
    @TableField("user_id")
    private String userID;
    @TableField("title")
    private String title;
    @TableField("content")
    private String content;
    @TableField("type")
    private int type;
    @TableField("status")
    private int status;
    @TableField("create_time")
    private Date createTime;
    @TableField("comment_count")
    private int commentCount;
    @TableField("score")
    private int score;
}
