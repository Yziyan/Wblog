package com.xhy.wblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xhy.wblog.controller.vo.fans.FansVo;
import com.xhy.wblog.dao.FansDao;
import com.xhy.wblog.dao.UserDao;
import com.xhy.wblog.entity.Fans;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
            fans.setAttentionUserId(otherId);
            fansDao.insert(fans);
            User user = userDao.selectById(userId);
            user.setFriendsCount(user.getFriendsCount() + 1);
            userDao.updateById(user);
            User user1 = userDao.selectById(otherId);
            user1.setFansCount(user1.getFansCount() + 1);
            userDao.updateById(user1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean cancelSubscription(int userId, int otherId) {
        try {
            QueryWrapper<Fans> wrapper = new QueryWrapper<>();
            wrapper.eq("attention_user_id", otherId).eq("fans_user_id", userId);
            fansDao.delete(wrapper);
            User user = userDao.selectById(userId);
            user.setFriendsCount(user.getFriendsCount() - 1);
            userDao.updateById(user);
            User user1 = userDao.selectById(otherId);
            user1.setFansCount(user1.getFansCount() - 1);
            userDao.updateById(user1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<User> getBeSubscript(FansVo fansVo,String url) {
        QueryWrapper<Fans> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("fans_user_id", fansVo.getUserId());
        List<Fans> subscription = fansDao.selectList(wrapper1);//获取关注的人
        List<User> ref = new ArrayList<>();
        for (Fans f : subscription) {
            User user = userDao.selectById(f.getAttentionUserId());
            wrapper1.clear();
            wrapper1.eq("fans_user_id",fansVo.getLookId()).eq("attention_user_id",f.getAttentionUserId());
            user.setIsSubscript(fansDao.selectOne(wrapper1) != null);
            user.setPassword(null);
            String photo = user.getPhoto();
            photo =url+photo;
            user.setPhoto(photo);
            ref.add(user);
        }
        return ref;
    }

    @Override
    public List<User> getFans(FansVo fansVo, String url){
        QueryWrapper<Fans> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("attention_user_id", fansVo.getUserId());
        List<Fans> fans = fansDao.selectList(wrapper1);//获取粉丝
        List<User> ref = new ArrayList<>();
        for (Fans f : fans) {
            User user = userDao.getUser(f.getFansUserId());
            wrapper1.clear();
            wrapper1.eq("fans_user_id",fansVo.getLookId()).eq("attention_user_id",f.getFansUserId());
            Fans chakande = fansDao.selectOne(wrapper1);
            user.setIsSubscript(chakande != null);
            user.setPassword(null);
            String photo = user.getPhoto();
            photo =url+photo;
            user.setPhoto(photo);
            ref.add(user);
        }
        return ref;
    }

}
