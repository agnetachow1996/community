package com.nowcoder.community.controller;


import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussService;
import com.nowcoder.community.service.UserSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@Controller
public class HomeController {
    @Autowired
    private DiscussService discussService;

    @Autowired
    private UserSerivce userSerivce;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用前，springmvc会自动实例化model和Page,所以分页时不需要传入page变量到前端
        page.setRows(discussService.selectDiscussPostRows(0));
        page.setPath("/index");
        List<Discuss> list = discussService.selectDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> discussPost = new ArrayList<>();
        if(list != null){
            for(Discuss item:list){
                Map<String,Object> map = new HashMap<>();
                User user = userSerivce.selectUserByID(item.getUserID());
                map.put("post",item);
                map.put("user",user);
                discussPost.add(map);
            }
        }
        model.addAttribute("discussPost",discussPost);
        return "/index";
    }
}
