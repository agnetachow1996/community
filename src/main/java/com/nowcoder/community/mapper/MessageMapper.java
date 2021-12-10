package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {
    //查询用户的会话列表，针对每一个会话同时返回最新的一条私信(会话很多需要分页的)
    List<Message> selectConversation(int userId,int offset,int limit);

    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询会话所包含的私信列表
    List<Message> selectLetters(String ConversationId,int offset,int limit);

    //查询会话包含的私信数量
    int selectLetterCount(String ConversationId);

    //查询未读私信的数量
    int selectConversationUnreadCount(int userId,String ConversationId);
}
