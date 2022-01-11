package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserSerivce;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;

@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;
    //因为要获取当前用户的私信列表,所以要引入
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserSerivce userSerivce;

    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId() == id0){
            return userSerivce.selectUserByID(id1);
        }else{
            return userSerivce.selectUserByID(id0);
        }
    }

    //已读通知的列表
    private List<Integer> getListIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(letterList != null){
            for(Message message:letterList){
                if(message.getStatus() == 0 && message.getToId() == hostHolder.getUser().getId()){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
    //私信列表
    @RequestMapping(path="/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        //设置分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //获取会话列表
        List<Message> conversationList =
                messageService.findConversations(user.getId(),page.getOffset(),page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversationList != null){
            for(Message message:conversationList){
                Map<String,Object> map = new HashMap<>();
                //这里的私信列表通常是显示最新的一条数据作为对话的显示
                map.put("conversation",message);
                map.put("unreadCount",messageService.selectConversationUnreadCount(
                        user.getId(),message.getConversationId()));
                map.put("letterCount",messageService.findConversationCount(user.getId()));
                //用户和别人私信有来有回，无论来回都要显示别人的头像
                int targetid = user.getId() == message.getFromId()?message.getToId():message.getFromId();
                map.put("target",userSerivce.selectUserByID(targetid));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);
        //查询用户所有未读私信的数量
        int letterUnreadCount = messageService.selectConversationUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.
                findNoticeUnreadCount(hostHolder.getUser().getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/letter";
    }

    @RequestMapping(path="/letter/detail/{conversationID}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationID")String conversationID,Page page,Model model){
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationID);
        page.setRows(messageService.selectLetterCount(conversationID));
        List<Message> letterList = messageService.findLetters(conversationID,page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList != null){
            for(Message message:letterList){
                Map<String,Object> map = new HashMap<>();
                //这里的私信列表通常是显示最新的一条数据作为对话的显示
                map.put("letter",message);
                map.put("fromUser",userSerivce.selectUserByID(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        //查询用户所有未读私信的数量
        User target = this.getLetterTarget(conversationID);
        model.addAttribute("target",target);
        List<Integer> ids = this.getListIds(letterList);
        if(ids != null){
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }

    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        User target = userSerivce.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJsonString(1,"用户不存在");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJsonString(0);
    }

    @RequestMapping(path = "/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();
        //查询评论类的通知
        Message message = messageService.findLatestNotice(user.getId(),TOPIC_COMMENT);
        Map<String,Object> messageVo = new HashMap<>();
        if(message != null){
            messageVo.put("message",message);
            //此时字符串中没有转义字符了
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
            messageVo.put("user",userSerivce.selectUserByID((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));
        }
        int count = messageService.findNoticeCount(user.getId(),TOPIC_COMMENT);
        messageVo.put("count",count);
        int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_COMMENT);
        messageVo.put("unread",unread);
        model.addAttribute("commentNotice",messageVo);

        //查询点赞类的通知
        message = messageService.findLatestNotice(user.getId(),TOPIC_LIKE);
        messageVo = new HashMap<>();
        if(message != null){
            messageVo.put("message",message);
            //此时字符串中没有转义字符了
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
            messageVo.put("user",userSerivce.selectUserByID((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));
        }
        count = messageService.findNoticeCount(user.getId(),TOPIC_LIKE);
        messageVo.put("count",count);
        unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_LIKE);
        messageVo.put("unread",unread);
        model.addAttribute("likeNotice",messageVo);

        //查询关注类的通知
        message = messageService.findLatestNotice(user.getId(),TOPIC_FOLLOW);
        if(message != null){
            messageVo = new HashMap<>();
            messageVo.put("message",message);
            //此时字符串中没有转义字符了
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
            messageVo.put("user",userSerivce.selectUserByID((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));
        }
        count = messageService.findNoticeCount(user.getId(),TOPIC_FOLLOW);
        messageVo.put("count",count);
        unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_FOLLOW);
        messageVo.put("unread",unread);
        model.addAttribute("followNotice",messageVo);
        // 这里是所有未读的信息数量
        int letterUnreadCount = messageService.
                selectConversationUnreadCount(hostHolder.getUser().getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.
                findNoticeUnreadCount(hostHolder.getUser().getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/notice";
    }

    @RequestMapping(path="/notice/detail/{topic}",method=RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic")String topic,Model model){
        User user = hostHolder.getUser();
        Page page = new Page();
        page.setRows(messageService.findNoticeCount(user.getId(),topic));
        page.setPath("/notice/detail/" + topic);
        page.setLimit(5);
        List<Message> noticeList = messageService.findNotices(user.getId(),topic,page.getOffset(),page.getLimit());
        List<Map<String,Object>> noticeVo = new ArrayList<>();
        if(noticeList != null){
            for(Message notice:noticeList){
                Map<String,Object> map = new HashMap<>();
                map.put("notice",notice);
                String content = HtmlUtils.htmlEscape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content);
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("user",userSerivce.selectUserByID((Integer) data.get("userId")));
                map.put("postId",data.get("postId"));
                //通知作者
                map.put("fromUser",userSerivce.selectUserByID(notice.getFromId()));
                noticeVo.add(map);
            }
            model.addAttribute("notices",noticeVo);
            //设置已读
            List<Integer> ids = getListIds(noticeList);
            if(!ids.isEmpty()){
                messageService.readMessage(ids);
            }
        }
        return "/site/notice-detail";
    }

}
