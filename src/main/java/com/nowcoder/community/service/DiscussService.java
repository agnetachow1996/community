package com.nowcoder.community.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

        return discussMapper.insert(post);

    }

    public List<Discuss> selectDiscussPosts(int userId,int offset,int limit){
        return discussMapper.selectDiscussPosts(userId,offset,limit);
    }

    public int selectDiscussPostRows(int userId){
        return discussMapper.selectDiscussPostRows(userId);
    }
}
