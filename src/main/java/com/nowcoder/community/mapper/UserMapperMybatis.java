package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapperMybatis {
    User selectUserByUsername(String username);

    User selectUserByEmail(String email);

    int insertUser(User user);

    User selectUserById(int id);

    void updateStatus(int status,int id);

    int updateHeader(String header_url, int id);

    void updatePassword(String password,int id);
}
