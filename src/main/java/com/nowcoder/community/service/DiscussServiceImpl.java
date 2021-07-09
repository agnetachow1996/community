package com.nowcoder.community.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.mapper.DiscussMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscussServiceImpl implements DiscussService {
    @Autowired
    private DiscussMapper discussMapper;
    @Override
    public IPage<Discuss> selectDiscussPage(Page<Discuss> page) {
        QueryWrapper<Discuss> queryWrapper = new QueryWrapper<>();
        //等于2反正是一种不能显示的状态。
        queryWrapper.ne("status",2);
        return discussMapper.selectPage(page, queryWrapper);
    }

}
