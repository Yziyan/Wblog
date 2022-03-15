package com.xhy.wblog.service;

import com.xhy.wblog.controller.vo.dynamic.PublishVo;
import com.xhy.wblog.entity.Dynamic;
import org.springframework.transaction.annotation.Transactional;


// 事务管理: 默认 只读
@Transactional(readOnly = true)
public interface DynamicService {


    // 发布动态、编辑动态
    @Transactional(readOnly = false)
    Dynamic save(PublishVo bean);

    // 通过id查询
    Dynamic getById(Integer id);
}
