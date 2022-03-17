package com.xhy.wblog.service;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface FansService {

    //关注
    boolean addSubscription(int userId,int otherId);
    //取消关注
    boolean cancelSubscription(int userId,int otherId);
}
