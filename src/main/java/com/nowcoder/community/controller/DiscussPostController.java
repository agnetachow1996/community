package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.UserMapperMybatis;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussService;
import com.nowcoder.community.service.UserSerivce;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;


@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussService discussService;

    @Autowired
    private UserSerivce userSerivce;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscuss(String title,String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJsonString(403,"您未登录！");
        }
        Discuss discuss = new Discuss();
        discuss.setTitle(title);
        discuss.setContent(content);
        discuss.setUserID(user.getId());
        discuss.setCreateTime(new Date());
        discussService.addDiscussPost(discuss);
        //报错情况来统一处理
        return CommunityUtil.getJsonString(0,"发布成功！");
    }

    @RequestMapping(path = "/detail/{discussID}",method = RequestMethod.GET)
    public String getDiscussDetail(@PathVariable("discussID") int discussID, Model model, Page page){
        Discuss post = discussService.findDiscussByID(discussID);
        model.addAttribute("post",post);
        User user = userSerivce.selectUserByID(post.getUserID());
        model.addAttribute("user",user);

        page.setLimit(15);
        page.setPath("/discuss/detail" + discussID);
        page.setRows(post.getCommentCount());
        List<Comment>comments = commentService.findCommentByEntity(CommunityConstant.ENTITY_TYPE_POST,
                post.getId(),page.getOffset(),page.getLimit());
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(comments != null) {
            for (Comment item : comments) {
                Map<String,Object> commentVo = new HashMap<>();
                //这个是评论
                commentVo.put("user",userSerivce.selectUserByID(item.getUserId()));
                commentVo.put("comment",item);
                //这个是回复，这里的回复没有设置分页
                List<Comment> replyList = commentService.findCommentByEntity(
                        CommunityConstant.ENTITY_TYPE_COMMENT,item.getId(),0,Integer.MAX_VALUE);
                //再将回复封装成View Object
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply:replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        replyVo.put("reply",reply);
                        replyVo.put("user",userSerivce.selectUserByID(reply.getUserId()));
                        //回复目标
                        User target = reply.getTargetId() == 0 ?
                                null:userSerivce.selectUserByID(reply.getTargetId());
                        replyVo.put("target",target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);
                //回复数量
                int replyCount = commentService.findCommentCount(
                        CommunityConstant.ENTITY_TYPE_COMMENT,item.getId());
                commentVo.put("replyCount",replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";
    }
}
