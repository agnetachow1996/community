package com.nowcoder.community.controller;

import com.nowcoder.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DataController {
    @Autowired
    private DataService dataService;

    // 这里新增了post请求，就能够让其他方法使用forword到当前方法中
    @RequestMapping(path = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String getDataPage(){
        return "/site/admin/data";
    }

    @RequestMapping(path = "/data/uv",method = RequestMethod.GET)
    public String getUv(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate",start);
        model.addAttribute("uvEndDate",end);
        // forward表示请求只能处理一半，需要另外的方法继续处理请求。而且这样写逻辑能复用
        // 当处理完时，将继续getDataPage函数的逻辑
        return "forward:/data";
    }

    @RequestMapping(path = "/data/dau",method = RequestMethod.GET)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){
        long uv = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult",uv);
        model.addAttribute("dauStartDate",start);
        model.addAttribute("dauEndDate",end);
        // forward表示请求只能处理一半，需要另外的方法继续处理请求。而且这样写逻辑能复用
        // 当处理完时，将继续getDataPage函数的逻辑
        return "forward:/data";
    }
}
