package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserSerivce;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserSerivce userSerivce;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    //MultipartFile spring用于上传文件的类,model用于向前端发送数据
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImg, Model model) throws IOException {
        if(headerImg == null){
            model.addAttribute("error","您未选择图片");
            return "/site/setting";
        }
        String fileName = headerImg.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error","图片格式不正确！");
            return "/site/setting";
        }
        //生成随机字符串，防止图片重名
        fileName = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImg.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage().toString());
            throw new RuntimeException("上传文件失败！",e);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        //更新用户头像路径，路径一般是
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + "user/header" + fileName + suffix;
        userSerivce.updateHeaderUrl(user.getId(),headerUrl);
        return "redirect:/index";
    }

    //服务器读取图片给前端,图片是二进制流，将图片内容写入response中
    @RequestMapping(path = "/header/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename")String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/" + suffix);
        try (
                //这两段写在括号里，能够在读写完成后自动关闭，如果该对象自带关闭方法的话
                //请求输出流
                OutputStream os = response.getOutputStream();
                //文件输入流，需要手动关闭
                FileInputStream fileInputStream = new FileInputStream(fileName);
                ){
            int b = 0;
            byte[] buffer = new byte[1024];
            //-1表示没读到数据了
            while((b = fileInputStream.read(buffer))!= -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        }
    }
}
