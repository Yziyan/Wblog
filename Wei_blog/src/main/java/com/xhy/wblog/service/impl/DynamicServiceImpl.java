package com.xhy.wblog.service.impl;

import com.xhy.wblog.controller.vo.dynamic.PublishVo;
import com.xhy.wblog.dao.DynamicDao;
import com.xhy.wblog.dao.UserDao;
import com.xhy.wblog.entity.Dynamic;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DynamicServiceImpl implements DynamicService {
    // 自动初始化改dao
    @Autowired
    private DynamicDao dynamicDao;
    // 用来返回对象的信息
    @Autowired
    private UserDao userDao;
    // 动态发布、动态编辑、保存到数据库
    @Override
    public Dynamic save(PublishVo bean) {
        // 将vo转换从保存到数据库的
        Dynamic dynamic = new Dynamic();
        dynamic.setText(bean.getText());
        dynamic.setFile(bean.getFileVo());
        dynamic.setVisible(bean.getVisible());
        dynamic.setUerId(bean.getUserId());
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
    public Dynamic getById (Integer id) {
        return dynamicDao.selectById(id);
    }


}
