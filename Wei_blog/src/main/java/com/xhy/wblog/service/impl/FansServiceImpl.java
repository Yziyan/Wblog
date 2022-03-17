package com.xhy.wblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xhy.wblog.dao.FansDao;
import com.xhy.wblog.dao.UserDao;
import com.xhy.wblog.entity.Fans;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FansServiceImpl implements FansService {

    @Autowired
    private FansDao fansDao;

    @Autowired
    private UserDao userDao;

    @Override
    public boolean addSubscription(int userId, int otherId) {
        try {
            Fans fans = new Fans();
            fans.setFansUserId(userId);
            fans.setAttentionUseId(otherId);
            fansDao.insert(fans);
            User user = userDao.selectById(userId);
            user.setFriendsCount(user.getFriendsCount()+1);
            userDao.updateById(user);
            User user1 = userDao.selectById(otherId);
            user1.setFansCount(user1.getFansCount()+1);
            userDao.updateById(user1);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean cancelSubscription(int userId, int otherId) {
        try {
            QueryWrapper<Fans> wrapper = new QueryWrapper<>();
            wrapper.eq("attention_user_id",otherId).eq("fans_user_id",userId);
            fansDao.delete(wrapper);
            User user = userDao.selectById(userId);
            user.setFriendsCount(user.getFriendsCount()-1);
            userDao.updateById(user);
            User user1 = userDao.selectById(otherId);
            user1.setFansCount(user1.getFansCount()-1);
            userDao.updateById(user1);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
