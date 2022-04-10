package com.xhy.wblog.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.xhy.wblog.controller.vo.dynamic.PublishVo;
import com.xhy.wblog.dao.DynamicDao;
import com.xhy.wblog.dao.TopicDao;
import com.xhy.wblog.dao.UserDao;
import com.xhy.wblog.entity.Dynamic;
import com.xhy.wblog.entity.ForwardText;
import com.xhy.wblog.entity.Topic;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.CommentService;
import com.xhy.wblog.service.DynamicService;
import com.xhy.wblog.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
    //用来获取话题
    @Autowired
    private TopicDao topicDao;
    //用来返回话题
    @Autowired
    private TopicService topicService;


    // 用于切割文本信息的话题
    public String getTopicStr(String content) {

        // #assassin####sadsa####
        List<Integer> indexStr = new ArrayList<>();
        int first = content.indexOf("#");
        while (first != -1) {
            indexStr.add(first);
            first = content.indexOf("#", first + 1);
        }
        StringBuilder res = new StringBuilder();
        if (indexStr.size() >= 2) {
            int index = 0;
            while (index < indexStr.size() - 1) {
                if (indexStr.get(index + 1) - indexStr.get(index) == 1) {
                    index++;
                } else {
                    String objStr = content.substring(indexStr.get(index), indexStr.get(++index) + 1);
                    res.append(objStr).append(",");
                    index += 2;
                }
            }
            if (res.length() > 1) res.replace(res.length() - 1, res.length(), "");
            String result = String.valueOf(res);
            return result.equals("") ? null : result;
        } else {
            return null;
        }

    }

    // 动态发布、动态编辑、保存到数据库
    @Override
    public Dynamic save(PublishVo bean, String reqUri) {
        // 拿到切割好的话题
        String topicStr = getTopicStr(bean.getText());
        // 将这个保存到话题表
        topicService.save(topicStr);
        // 将vo转换从保存到数据库的
        Dynamic dynamic = new Dynamic();
        dynamic.setTheme(topicStr);
        dynamic.setText(bean.getText());
        dynamic.setFile(bean.getFileVo());
        dynamic.setVisible(bean.getVisible());
        dynamic.setUserId(bean.getUserId());
        Integer forwardDynamicId = bean.getForwardDynamicId();
        dynamic.setForwardDynamicId(forwardDynamicId);


        // 如果是转发，那么转发数那么把转发数 + 1
        if (forwardDynamicId != 0) {
            Dynamic beForwardDyna = dynamicDao.selectById(bean.getForwardDynamicId());
            Integer newCount = beForwardDyna.getForwardDynamicCount() + 1;
            beForwardDyna.setForwardDynamicCount(newCount);
            dynamicDao.updateById(beForwardDyna);
        }


        // 返回的动态结果
        Dynamic resDynamic;
        User resUser;
        // 该条动态的id
        Integer id = bean.getId();
        if (id == null || id <= 0) { // 说明是保存
            if (dynamicDao.insert(dynamic) > 0) {
                resDynamic = dynamicDao.selectById(dynamic.getId());
                resDynamic.setFilePath(getFilePath(resDynamic, reqUri));
                // 给动态注入依赖的用户
                resUser = userDao.selectById(bean.getUserId());
                resUser.setPassword(null);
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
                resDynamic.setFilePath(getFilePath(resDynamic, reqUri));
                // 给动态注入依赖的用户
                resUser = userDao.selectById(bean.getUserId());
                resUser.setPassword(null);
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


    //分页查询
    @Override
    public List<Dynamic> getMidleDynamic(String url) {
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("hits").eq("enable", 1);
        List<Dynamic> dynamics = dynamicDao.selectList(queryWrapper);
        for (Dynamic d : dynamics) {
            d.setFilePath(getFilePath(d, url));
            User user = userDao.getUser(d.getUserId());
            if (user != null) {
                user.setPassword(null);
                String photo = user.getPhoto();
                photo = url + photo;
                user.setPhoto(photo);
                d.setUser(user);
            }
            getForwardDynamics(d, url);
        }
        return dynamics;
    }

    @Override
    public long getCount() {
        return dynamicDao.selectCount(null);
    }

    @Override
    public List<Topic> getHot() {
        QueryWrapper<Topic> Wrapper = new QueryWrapper<>();
        Wrapper.orderByDesc("query_count");
        PageHelper.startPage(1, 10);
        List<Topic> topics = topicDao.selectList(Wrapper);
        return topics;
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
            User user = userDao.getUser(d.getUserId());
            if (user != null) {
                user.setPassword(null);
                String photo = user.getPhoto();
                photo = url + photo;
                user.setPhoto(photo);
                d.setUser(user);
            }
            getForwardDynamics(d, url);
        }
        return dynamics;
    }

    //获取@我的微博的微博
    @Override
    public List<Dynamic> getForwardMyDynamic(Integer userId, String url) {
        QueryWrapper<Dynamic> Wrapper = new QueryWrapper<>();
        Wrapper.orderByDesc("created_time").eq("enable", 1).eq("user_id", userId);
        //PageHelper.startPage(1,3);
        List<Dynamic> myDynamics = dynamicDao.selectList(Wrapper);
        List<Dynamic> dynamics = new ArrayList<>();//返回所有自己的动态信息
        for (Dynamic dynamic : myDynamics) {//根据自己动态被别人转发的id，获取转发我的动态的动态
            Wrapper.clear();
            int forwardId = dynamic.getId();
            Wrapper.orderByDesc("created_time").eq("enable", 1)
                    .eq("forward_dynamic_id", forwardId);
            List<Dynamic> dynamic1 = dynamicDao.selectList(Wrapper);
            dynamics.addAll(dynamic1);
        }

        for (Dynamic d : dynamics) {//设置发动态的用户信息
            d.setFilePath(getFilePath(d, url));
            User user = userDao.getUser(d.getUserId());
            if (user != null) {
                user.setPassword(null);
                String photo = user.getPhoto();
                photo = url + photo;
                user.setPhoto(photo);
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
        Dynamic d = null;
        while (forwardDynamicId != 0) {
            d = dynamicDao.selectById(forwardDynamicId);
            if (d != null) {
                d.setFilePath(getFilePath(d, url));
                User user1 = userDao.selectById(d.getUserId());
                if (user1 != null) {
//                    user1.setPassword(null);
//                    d.setUser(user1);
                    ForwardText forwardText = new ForwardText(user1.getProfileUrl(), user1.getName(), d.getText(), d.getFilePath());
                    forwardTexts.add(forwardText);
                }
                temp = d;
            }
            forwardDynamicId = temp.getForwardDynamicId();
        }
        dynamic.setForwardDynamic(d);
        dynamic.setForwardTexts(forwardTexts);
        return dynamic;
    }

    //文件路径
    public List<String> getFilePath(Dynamic dynamic, String url) {
        String file = dynamic.getFile();
        if (!(file != null && file.length() == 0) && file != null) {
            String[] files = file.split(",");
            for (int i = 0; i < files.length; i++) {
                if (!(files[i] != null && files[i].length() == 0))
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
            if (dynamic.getHits() > 0) {
                dynamic.setHits(dynamic.getHits() - 1);
            }
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
            getForwardDynamics(dynamic, reqUri);
        }
        return dynamics;
    }

    // 通过用户id查询所有动态
    @Override
    public List<Dynamic> getByUserId(Integer userId) {
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_time").eq("user_id", userId).eq("enable", 1);
        return dynamicDao.selectList(queryWrapper);
    }

    /**
     * 通过theme查询
     *
     * @param theme ：话题
     * @return 所有带这个话题的动态
     */
    @Override
    public List<Dynamic> listByTheme(String theme, String reqUrl) {

        topicService.getByTheme(theme);

        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        // 模糊查询，并且按点赞数降序返回
        queryWrapper.like("theme", theme).
                orderByDesc("hits").
                eq("enable", 1);
        List<Dynamic> dynamics = dynamicDao.selectList(queryWrapper);
        for (Dynamic dynamic : dynamics) {
            // 注入user
            Integer userId = dynamic.getUserId();
            User user = userDao.getUser(userId);
            user.setPassword(null);
            String photo = reqUrl + user.getPhoto();
            user.setPhoto(photo);
            dynamic.setUser(user);
            // 若是转发的。那就注入转发的信息
            getForwardDynamics(dynamic, reqUrl);
            // 若有文件，那么返回filePath
            dynamic.setFilePath(getFilePath(dynamic, reqUrl));
        }
        return dynamics;
    }

}
