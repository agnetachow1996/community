package com.nowcoder.community.service;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentService commentService;
    public List<Comment> findCommentByEntity(int entityType, int entityId,int offset,int limit){
        return commentService.findCommentByEntity(entityType,entityId,offset,limit);
    }

    public int findCommentCount(int entityType, int entityId){
        return commentService.findCommentCount(entityType,entityId);
    }
}
