package com.nowcoder.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nowcoder.community.entity.Discuss;

public interface DiscussService{
    IPage<Discuss> selectDiscussPage(Page<Discuss> page);
}
