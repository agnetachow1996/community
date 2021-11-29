package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.UserMapperMybatis;
import com.nowcoder.community.service.DiscussService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;


@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussService discussService;

    @Autowired
    private UserMapperMybatis userMapperMybatis;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscuss(String title,String Content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJsonString(403,"您未登录！");
        }
        Discuss discuss = new Discuss();
        discuss.setTitle(title);
        discuss.setContent(Content);
        discuss.setUserID(user.getId());
        discuss.setCreateTime(new Date());
        discussService.addDiscussPost(discuss);
        //报错情况来统一处理
        return CommunityUtil.getJsonString(0,"发布成功！");
    }

    @RequestMapping(path = "/detail/{discussID}",method = RequestMethod.GET)
    public String getDiscussDetail(@PathVariable("discussID") int discussID, Model model){
        Discuss post = discussService.findDiscussByID(discussID);
        model.addAttribute("post",post);
        User user = userMapperMybatis.selectUserById(post.getUserID());
        model.addAttribute("user",user);
        return "/site/discuss-detail";
    }
}
