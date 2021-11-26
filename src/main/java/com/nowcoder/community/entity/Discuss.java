package com.nowcoder.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("discuss_post")
public class Discuss {
    private int id;
    @TableField("user_id")
    private int userID;
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

    public int getId() {
        return id;
    }

    public int getUserID() {
        return userID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getScore() {
        return score;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
