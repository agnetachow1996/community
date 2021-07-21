package com.nowcoder.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussService;
import com.nowcoder.community.service.UserSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussService discussService;

    @Autowired
    private UserSerivce userSerivce;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model,Page page){
        //方法调用前，springmvc会自动实例化model和Page,所以分页时不需要传入page变量到前端
        page.setSize(15);
        List<Discuss> list = discussService.selectDiscussPage(page).getRecords();
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
        int pageNum = (int) (page.getTotal()/page.getSize());
        model.addAttribute("discussPost",discussPost);

        model.addAttribute("page",page);
        model.addAttribute("pageNum",pageNum);
        return "/index";
    }
}
