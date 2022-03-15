package com.xhy.wblog.service.impl;

import com.xhy.wblog.dao.CommentDao;
import com.xhy.wblog.dao.DynamicDao;
import com.xhy.wblog.dao.UserDao;
import com.xhy.wblog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CommentServiceImpl implements CommentService {
    // 自动初始化改dao
    @Autowired
    private CommentDao commentDao;
    // 用来返回动态信息
    @Autowired
    private DynamicDao dynamicDao;
    // 用来返回对象的信息
    @Autowired
    private UserDao userDao;

}
