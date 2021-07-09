package com.nowcoder.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSeriveImpl implements UserSerivce {

    @Autowired
    private UserMapper userMapper;


    @Override
    public User selectUserByID(String ID) {
        return userMapper.selectById(ID);
    }

    @Override
    public List<User> selectAllUser() {
        return userMapper.selectList(null);
    }
}
