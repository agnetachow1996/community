package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.UserMapperMybatis;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussService;
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
    private UserMapperMybatis userMapperMybatis;

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
        User user = userMapperMybatis.selectUserById(post.getUserID());
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

                commentVo.put("user",userMapperMybatis.selectUserById(item.getUserId()));
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",comments);

        return "/site/discuss-detail";
    }
}
