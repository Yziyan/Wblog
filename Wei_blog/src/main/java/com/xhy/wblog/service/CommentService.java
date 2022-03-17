package com.xhy.wblog.service;

import com.xhy.wblog.controller.vo.comment.CommentListVo;
import com.xhy.wblog.controller.vo.comment.PushCommentVo;
import com.xhy.wblog.entity.Comment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    // 删除评论
    @Transactional(readOnly = false)
    boolean removeById(Integer id);
    // 查询评论
    List<Comment> listPage(CommentListVo listVo);

    // 通过id查询评论
    Comment get(Integer id);

    // 点赞功能
    @Transactional(readOnly = false)
    boolean updateHits(Integer commentId, String choose);

    // 获取当前评论的点赞数
    Integer getHits(Integer commentId);
}
