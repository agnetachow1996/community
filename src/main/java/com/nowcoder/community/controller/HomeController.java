package com.nowcoder.community.controller;


import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserSerivce;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussService discussService;

    @Autowired
    private UserSerivce userSerivce;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name="orderMode",defaultValue = "0") int orderMode){
        //方法调用前，springmvc会自动实例化model和Page,所以分页时不需要传入page变量到前端
        //这里的orderMode使用get明文传输，如果用value传输需要用POST请求，比较麻烦
        page.setRows(discussService.selectDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);
        List<Discuss> list = discussService.selectDiscussPosts(0,page.getOffset(),page.getLimit(), orderMode);
        List<Map<String,Object>> discussPost = new ArrayList<>();
        if(list != null){
            for(Discuss item:list){
                Map<String,Object> map = new HashMap<>();
                User user = userSerivce.selectUserByID(item.getUserId());
                map.put("post",item);
                map.put("user",user);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,item.getId());
                map.put("likeCount",likeCount);
                discussPost.add(map);
            }
        }
        model.addAttribute("discussPost",discussPost);
        model.addAttribute("orderMode",orderMode);
        return "/index";
    }

    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getError(){
        return "/error/500";
    }

    @RequestMapping(path = "/denied",method = RequestMethod.GET)
    public String getDeniedPage(){
        return "/error/404";
    }
}
