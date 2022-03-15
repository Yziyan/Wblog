package com.xhy.wblog.service;

import org.springframework.transaction.annotation.Transactional;

// 评论相关

// 事务管理: 默认 只读
@Transactional(readOnly = true)
public interface CommentService {

}
