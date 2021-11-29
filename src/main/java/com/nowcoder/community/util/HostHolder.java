package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替session对象
 */

@Component
public class HostHolder {
    // 服务器处理很多请求，创建多个线程来处理，
    // 这些线程都存在map里，map的key是线程thread
    // 由于ThreadLocal本身的设计就是变量不与其他线程共享，
    // 不需要其他线程访问本对象的变量，放在Thread对象中不会有问题。
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUsers(User user){
        users.set(user);
    }

    public User getUser(){
        //获取当前线程，然后通过当前线程从map中查询线程
        return users.get();
    }

    public void clear(){
        //清空map
        users.remove();
    }
}
