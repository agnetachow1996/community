package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.service.UserSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/home")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "hello world";
    }

    @RequestMapping("/alpha")
    @ResponseBody //返回简单的字符串
    public String doAlphaService(){
        return alphaService.find();
    }

    //获取参数，可以在变量的参数中注明参数并完成参数对应，这种一般分页用的比较多
    //get明文传输，这是传参数方式一   /students?current=2&limit=3
    @RequestMapping(path="/students", method= RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current",required = false,defaultValue = "1") int current,
            @RequestParam(name = "limit",required = false,defaultValue = "4")int limit){
        System.out.println(current);
        System.out.println(limit);
        return "null";
    }

    //使用路径变量，路径变量的注解是@pathvariable
    @RequestMapping(path="/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        return "hello";
    }

    //响应HTML数据，两种方式，这种方式复杂,注解里面的传输方式建议写清楚
    @RequestMapping(path="/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        //或者全部写成一个map然后当成参数传入
        mav.addObject("name", "张三");
        //这个一般访问的是resources/templete下面的html文件
        mav.addObject("age","34");
        //这里返回的是一个网页
        mav.setViewName("/demo/view");
        return mav;
        //如果出现An error happened during template，是视图层的错误
    }

    //相应HTML数据的第二种方式，这种方式比较常用
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    //视图层已经有对象了，不用实例化
    //这里不加requestbody，是因为返回的不是字符串是一个网页
    public String getSchool(Model model){
        model.addAttribute("name","hahahhaha");
        model.addAttribute("age","123");
        return "/demo/school";
    }

    //响应JSON数据 异步请求
    @RequestMapping(path = "/member",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getMember(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("name","hahahaha");
        emp.put("age",23);
        return emp;
    }

}
