package com.xhy.wblog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xhy.wblog.controller.vo.comment.CommentListVo;
import com.xhy.wblog.entity.Comment;
import com.xhy.wblog.entity.Dynamic;
import com.xhy.wblog.entity.User;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// 评论

// BaseMapper<User> 继承Mybatis-plus 的类 ，泛型：表的实体对象
public interface CommentDao extends BaseMapper<Comment> {

    // Mybatis-plus默认提供了很多操作数据库的方法， 可以点进去看看，也可以百度一下。

    // 需求不够自己在后面添加新接口。


}
