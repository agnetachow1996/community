package com.nowcoder.community.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

//这个是elasticsearch的注解，用于es的识别。
//属性上的注解能够和ES建立联系
@Document(indexName = "discuss")
public class Discuss {
    @Id
    private int id;

    @Field(type= FieldType.Integer)
    private int userId;

    @Field(type=FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String title;
    //analyser是索引存储的分析器，利用ik将内容分词成词最多的条数，方便被检索到（全分词模式）
    //搜索分析器不用全分词，而是采用合适的分词结果，这样搜索结果更加准确
    @Field(type=FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String content;

    @Field(type= FieldType.Integer)
    private int type;

    @Field(type= FieldType.Integer)
    private int status;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type= FieldType.Integer)
    private int commentCount;
    private double score;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
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

    public double getScore() {
        return score;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userID) {
        this.userId = userID;
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

    public void setScore(double score) {
        this.score = score;
    }
}
