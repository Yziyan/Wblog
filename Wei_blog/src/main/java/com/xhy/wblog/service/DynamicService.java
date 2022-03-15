package com.xhy.wblog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhy.wblog.controller.vo.dynamic.PublishVo;
import com.xhy.wblog.entity.Dynamic;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


// 事务管理: 默认 只读
@Transactional(readOnly = true)
public interface DynamicService {


    // 发布动态、编辑动态
    @Transactional(readOnly = false)
    Dynamic save(PublishVo bean);

    // 通过id查询
    Dynamic getById(Integer id);

    //删除动态
    @Transactional(readOnly = false)
    boolean removeById(Integer id);

    //分页查询，获取动态
//    Page<Dynamic> getNewDynamic(int countnum, int nums);
//
    List<Dynamic> findAllPage(int count,int num);

    long getCount();
}
