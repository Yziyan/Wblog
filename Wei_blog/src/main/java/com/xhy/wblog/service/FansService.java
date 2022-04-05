package com.xhy.wblog.service;

import com.xhy.wblog.controller.vo.fans.FansVo;
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
    List<User> getBeSubscript(FansVo fansVo, String url);
    //获取粉丝
    List<User> getFans(FansVo fansVo,String url);

    // 根据用户URL查看，是否是登录用户的关注
    boolean urlIsSubscript(Integer loginUserId, Integer urlUserId);
}
