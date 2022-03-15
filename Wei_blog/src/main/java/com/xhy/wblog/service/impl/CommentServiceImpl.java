package com.xhy.wblog.service.impl;

import com.xhy.wblog.controller.vo.comment.PushCommentVo;
import com.xhy.wblog.dao.CommentDao;
import com.xhy.wblog.dao.DynamicDao;
import com.xhy.wblog.dao.UserDao;
import com.xhy.wblog.entity.Comment;
import com.xhy.wblog.entity.Dynamic;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


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

    public Comment getCommentById(Integer dynamicId){
        return commentDao.selectById(dynamicId);
    }
    // 保存评论、
    @Override
    public Map<String, Object> save(PushCommentVo bean) {

        // 拿到三者的id待会查数据
        Integer replyId = bean.getReplyId();
        Integer dynamicId = bean.getDynamicId();
        Integer userId = bean.getUserId();
        Comment comment = new Comment();


        // 注入评论所需的 字段
        comment.setText(bean.getText());
        comment.setDynamicId(dynamicId);
        comment.setUserId(userId);
        comment.setReplyId(replyId);

        // 装返回的结果
        Map<String, Object> map = new HashMap<>();

        if (commentDao.insert(comment) > 0) { // 保存评论成功

            if (replyId != 0) { // 说明是回复
                // 查出回复的那一条评论
                Comment replyComment = commentDao.selectById(replyId);
                User replyUser = userDao.selectById(replyComment.getUserId());
                replyUser.setPassword(null);
                replyComment.setUser(replyUser);
                map.put("replyComment", replyComment);
            }
            // 查出必须的，评论的是哪一条动态、是谁评论的
            Dynamic dynamic = dynamicDao.selectById(dynamicId);
            dynamic.setUser(userDao.selectById(dynamic.getUerId()));
            dynamic.getUser().setPassword(null);

            // 将这条评论的信息返回，并且注写这条评论的用户
            User user = userDao.selectById(userId);
            user.setPassword(null);
            comment = commentDao.selectById(comment.getId());
            comment.setUser(user);
            map.put("comment", comment);
            map.put("dynamic", dynamic);
            return map;
        } else {
            return null;
        }

    }


}
