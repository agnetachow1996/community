package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    //该方法可以使用拦截器拦截，如果用户没有登录则不能访问点赞方法
    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId,int entityUserId){
        User user = hostHolder.getUser();
        //点赞功能
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //当前显示对象的点赞数量（帖子，回复等）
        long likeCount = likeService.findEntityLikeCount(entityType,entityId);
        //like的状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType,entityId);

        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);
        //数据传输成功
        return CommunityUtil.getJsonString(0,null,map);
    }
}
