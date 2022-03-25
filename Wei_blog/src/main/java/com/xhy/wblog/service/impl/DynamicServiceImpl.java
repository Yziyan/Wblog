package com.xhy.wblog.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.xhy.wblog.controller.vo.dynamic.PublishVo;
import com.xhy.wblog.dao.DynamicDao;
import com.xhy.wblog.dao.UserDao;
import com.xhy.wblog.entity.Dynamic;
import com.xhy.wblog.entity.ForwardText;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.CommentService;
import com.xhy.wblog.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class DynamicServiceImpl implements DynamicService {
    // 自动初始化改dao
    @Autowired
    private DynamicDao dynamicDao;
    // 用来返回对象的信息
    @Autowired
    private UserDao userDao;
    //用来返回评论信息
    @Autowired
    private CommentService commentService;

    // 动态发布、动态编辑、保存到数据库
    @Override
    public Dynamic save(PublishVo bean) {
        // 将vo转换从保存到数据库的
        Dynamic dynamic = new Dynamic();
        dynamic.setText(bean.getText());
        dynamic.setFile(bean.getFileVo());
        dynamic.setVisible(bean.getVisible());
        dynamic.setUserId(bean.getUserId());
        dynamic.setForwardDynamicId(bean.getForwardDynamicId());

        // 返回的动态结果
        Dynamic resDynamic;
        User resUser;
        // 该条动态的id
        Integer id = bean.getId();
        if (id == null || id <= 0) { // 说明是保存
            if (dynamicDao.insert(dynamic) > 0) {
                resDynamic = dynamicDao.selectById(dynamic.getId());
                // 给动态注入依赖的用户
                resUser = userDao.selectById(bean.getUserId());
                // 将用户的动态数量加1
                Integer dynamicCount = resUser.getDynamicCount() + 1;
                resUser.setDynamicCount(dynamicCount);
                userDao.updateById(resUser);
                resDynamic.setUser(resUser);
                return resDynamic;  // 成功就返回刚插入的这条动态
            } else { // 保存失败了
                return null;
            }

        } else { //编辑
            dynamic.setId(id);
            if (dynamicDao.updateById(dynamic) > 0) { // 编辑成功
                resDynamic = dynamicDao.selectById(id);
                // 给动态注入依赖的用户
                resUser = userDao.selectById(bean.getUserId());
                resDynamic.setUser(resUser);
                return resDynamic;
            } else {
                return null;
            }

        }

    }

    // 通过id查询
    @Override
    public Dynamic getById(Integer id) {
        return dynamicDao.selectById(id);
    }

    @Override
    public boolean removeById(Integer id) {
        Dynamic dynamic = dynamicDao.selectById(id);
        dynamic.setEnable(0);
        User user = userDao.selectById(dynamic.getUserId());
        user.setDynamicCount(user.getDynamicCount() - 1);//动态发布数量-1
        userDao.updateById(user);
        return dynamicDao.updateById(dynamic) > 0;
    }

//    @Override
//    public Page<Dynamic> getNewDynamic(int countnum, int nums) {
//        QueryWrapper<Dynamic> DynamicQueryWrapper = new QueryWrapper<>();
//        Page<Dynamic> dynamicPage = new Page<>(countnum, nums);
//        return dynamicDao.selectPage(dynamicPage,DynamicQueryWrapper);
//    }


    //分页查询
    @Override
    public List<Dynamic> findAllPage(Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_time");
        return dynamicDao.selectList(queryWrapper);
    }

    @Override
    public long getCount() {
        return dynamicDao.selectCount(null);
    }

    @Override
    public List<Dynamic> getHot() {
        QueryWrapper<Dynamic> Wrapper = new QueryWrapper<>();
        Wrapper.orderByDesc("hits").eq("enable", 1);
        PageHelper.startPage(1, 10);
        return dynamicDao.selectList(Wrapper);

    }

    //获取最新动态
    @Override
    public List<Dynamic> getNew(String url) {
        QueryWrapper<Dynamic> Wrapper = new QueryWrapper<>();
        Wrapper.orderByDesc("created_time").eq("enable", 1);
        //PageHelper.startPage(1,3);
        List<Dynamic> dynamics = dynamicDao.selectList(Wrapper);//返回所有信息
        for (Dynamic d : dynamics) {
            d.setFilePath(getFilePath(d, url));
            User user = userDao.selectById(d.getUserId());
            if (user != null) {
                user.setPassword(null);
                d.setUser(user);
            }
            getForwardDynamics(d, url);
        }
        return dynamics;
    }

    //获取转发嵌套
    public Dynamic getForwardDynamics(Dynamic dynamic, String url) {
        List<ForwardText> forwardTexts = new ArrayList<>();
        Dynamic temp = dynamic;
        Integer forwardDynamicId = temp.getForwardDynamicId();
        while (forwardDynamicId != 0) {
            Dynamic d = dynamicDao.selectById(forwardDynamicId);
            if (d != null) {
                d.setFilePath(getFilePath(d, url));
                User user1 = userDao.selectById(d.getUserId());
                if (user1 != null) {
//                    user1.setPassword(null);
//                    d.setUser(user1);
                    ForwardText forwardText = new ForwardText(user1.getProfileUrl(), user1.getName(), d.getText());
                    forwardTexts.add(forwardText);
                }
                temp.setForwardDynamic(d);
                temp = temp.getForwardDynamic();
            }
            forwardDynamicId = temp.getForwardDynamicId();
        }
        dynamic.setForwardTexts(forwardTexts);
        return dynamic;
    }

    //文件路径
    public List<String> getFilePath(Dynamic dynamic, String url) {
        String file = dynamic.getFile();
        if (file != null) {
            String[] files = file.split(",");
            for (int i = 0; i < files.length; i++) {
                files[i] = url + files[i];
            }
            return Arrays.asList(files);
        }
        return null;
    }

    //更新点赞数
    @Override
    public boolean updateDynamicHits(int id, boolean setOrCan) {
        Dynamic dynamic = dynamicDao.selectById(id);
        if (setOrCan) {
            dynamic.setHits(dynamic.getHits() + 1);
        } else {
            dynamic.setHits(dynamic.getHits() - 1);
        }
        return dynamicDao.updateById(dynamic) > 0;
    }


    // 通过用户id查询所有动态
    @Override
    public List<Dynamic> getByUserId(Integer userId, String reqUri) {
        // 调用重载的方法
        List<Dynamic> dynamics = getByUserId(userId);
        for (Dynamic dynamic : dynamics) {
            dynamic.setFilePath(getFilePath(dynamic, reqUri));
        }
        return dynamics;
    }
    // 通过用户id查询所有动态
    @Override
    public List<Dynamic> getByUserId(Integer userId) {
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("enable", 1);
        return dynamicDao.selectList(queryWrapper);
    }

}
