package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.internet.MimeMessage;


@Component
public class MailClient {
    //for what?why?
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void setJavaMailSender(String context,String to,String subject){
        try{
            //MineMessage和MimeMailMessage的区别？
            //mimeMailMessage与mimeMessageHelper之间的关系？
            MimeMessage mimeMailMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            //设置为true则支持HTML文件
            mimeMessageHelper.setText(context,true);
            //发送邮件
            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            logger.error("发送邮件失败+"+e.getMessage());
        }

    }
}
