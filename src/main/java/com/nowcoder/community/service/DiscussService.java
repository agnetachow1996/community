package com.nowcoder.community.service;

import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.mapper.DiscussMapper;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussService{
    @Autowired
    private DiscussMapper discussMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public int addDiscussPost(Discuss post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义的HTML
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setContent(sensitiveFilter.filter(post.getContent()));
        post.setTitle(sensitiveFilter.filter(post.getTitle()));

        return discussMapper.insertDiscussPost(post);

    }

    public List<Discuss> selectDiscussPosts(int userId,int offset,int limit, int orderMode){
        return discussMapper.selectDiscussPosts(userId,offset,limit,orderMode);
    }

    public int selectDiscussPostRows(int userId){
        return discussMapper.selectDiscussPostRows(userId);
    }

    public Discuss findDiscussByID(int discussID){
        return discussMapper.findDiscussByID(discussID);
    }

    public int updateCommentCount(int id,int commentCount){
        return discussMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id,int type){
        return discussMapper.updateType(id, type);
    }

    public int updateStatus(int id,int status){
        return discussMapper.updateType(id, status);
    }

    public int updateScore(Integer postId, double score) {
        return discussMapper.updateScore(postId, score);
    }
}
