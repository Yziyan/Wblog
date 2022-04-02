package com.xhy.wblog.service;

import com.xhy.wblog.entity.Fans;
import com.xhy.wblog.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface FansService {

    //关注
    @Transactional(readOnly = false)
    boolean addSubscription(int userId,int otherId);
    //取消关注
    @Transactional(readOnly = false)
    boolean cancelSubscription(int userId,int otherId);
    //获取关注的人
    List<User> getBeSubscript(int userId);
    //获取粉丝
    List<User> getFans(int userId);

}
