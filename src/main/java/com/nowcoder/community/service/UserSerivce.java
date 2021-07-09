package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;

import java.util.List;


public interface UserSerivce {
    User selectUserByID(String id);

    List<User> selectAllUser();
}
