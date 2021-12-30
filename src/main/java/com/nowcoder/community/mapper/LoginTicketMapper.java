package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

//LoginTicket的CURD
@Mapper
@Deprecated //表示不推荐使用
public interface LoginTicketMapper {
    @Insert({"insert into login_ticket(user_id,ticket,status,expired)",
    "values((#{userId}),(#{ticket}),(#{status}),(#{expired}))"})
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    //通过ticket找用户，ticket<-->用户
    @Select({"select user_id,id,ticket,status,expired from login_ticket ",
    "where ticket=(#{ticket})"})
    LoginTicket selectLoginTicket(String ticket);

    @Update({"update login_ticket set status=(#{status}) where ticket=(#{ticket})"})
    int updateLoginTicket(String ticket,int status);
}
