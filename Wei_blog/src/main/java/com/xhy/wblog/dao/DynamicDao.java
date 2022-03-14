package com.xhy.wblog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xhy.wblog.entity.Dynamic;

// BaseMapper<User> 继承Mybatis-plus 的类 ，泛型：表的实体对象
public interface DynamicDao extends BaseMapper<Dynamic> {

    // Mybatis-plus默认提供了很多操作数据库的方法， 可以点进去看看，也可以百度一下。

    // 需求不够自己在后面添加新接口。

}
