package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.Discuss;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussMapper{
    //int insertDisscuss
    List<Discuss> selectDiscussPosts(int userId,int offset,int limit,int orderMode);
    //@Param主要用于给参数起别名
    //如果只有一个参数，并且在<if>里使用，则必须增加别名。
    int selectDiscussPostRows(@Param("userId")int userId);

    int insertDiscussPost(Discuss post);

    Discuss findDiscussByID(int discussID);

    int updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id,int status);

    int updateScore(int postId, double score);
}
