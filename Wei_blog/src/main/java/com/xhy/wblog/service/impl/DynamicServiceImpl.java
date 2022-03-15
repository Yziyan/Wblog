package com.xhy.wblog.service.impl;

import com.xhy.wblog.controller.vo.dynamic.PublishVo;
import com.xhy.wblog.dao.DynamicDao;
import com.xhy.wblog.entity.Dynamic;
import com.xhy.wblog.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DynamicServiceImpl implements DynamicService {
    // 自动初始化改dao
    @Autowired
    private DynamicDao dao;

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

        // 该条动态的id
        Integer id = bean.getId();
        if (id == null || id <= 0) { // 说明是保存
            if (dao.insert(dynamic) > 0) {
                return dao.selectById(dynamic.getId());  // 成功就返回刚插入的这条动态
            } else { // 保存失败了
                return null;
            }

        } else { //编辑
            dynamic.setId(id);
            if (dao.updateById(dynamic) > 0) { // 编辑成功
                return dao.selectById(id);
            } else {
                return null;
            }

        }

    }

    // 通过id查询
    @Override
    public Dynamic getById (Integer id) {
        return dao.selectById(id);
    }


}
