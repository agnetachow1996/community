package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {
    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityId, int entityType){
        User user = hostHolder.getUser();
        followService.follow(user.getId(),entityId, entityType);
        return CommunityUtil.getJsonString(0,"关注成功！");
    }

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityId, int entityType){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(),entityId, entityType);
        return CommunityUtil.getJsonString(0,"取关成功！");
    }
}
