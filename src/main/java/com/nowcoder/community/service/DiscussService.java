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

@Service
public class DiscussService{
    @Autowired
    private DiscussMapper discussMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public IPage<Discuss> selectDiscussPage(Page<Discuss> page) {
        QueryWrapper<Discuss> queryWrapper = new QueryWrapper<>();
        //等于2反正是一种不能显示的状态。
        queryWrapper.ne("status", 2);
        return discussMapper.selectPage(page, queryWrapper);
    }

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

    public Discuss findDiscussByID(int id){
        return discussMapper.selectById(id);
    }
}
