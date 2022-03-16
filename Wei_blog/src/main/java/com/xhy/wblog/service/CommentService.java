package com.xhy.wblog.service;

import com.xhy.wblog.controller.vo.comment.PushCommentVo;
import com.xhy.wblog.entity.Comment;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

// 评论相关

// 事务管理: 默认 只读
@Transactional(readOnly = true)
public interface CommentService {

    //通过动态id查询评论
    Comment getCommentById(Integer dynamicId);

    // 保存评论、
    @Transactional(readOnly = false)
    Map<String, Object> save(PushCommentVo bean);
}
