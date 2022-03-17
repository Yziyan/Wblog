package com.xhy.wblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.xhy.wblog.controller.vo.comment.CommentListVo;
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
import java.util.List;
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

    // 修改评论数量
    private void updateCount(Integer dynamicId, String choose) {
        Dynamic dynamic = dynamicDao.selectById(dynamicId);
        Integer commentCount = dynamic.getCommentsCount();
        if (choose.equals("增加")) {
            commentCount++;
        } else {
            commentCount--;
        }
        dynamic.setCommentsCount(commentCount);
        dynamicDao.updateById(dynamic);
    }

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
            // 将评论数量+1
            updateCount(dynamicId, "增加");

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

    /**
     * 删除评论模块
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Integer id) {

        Integer dynamicId = commentDao.selectById(id).getDynamicId();
        if (commentDao.deleteById(id) > 0) { // 说明删除成功，还需要将动态的评论数减1
            updateCount(dynamicId, "减少");
            return true;
        } else {
            return false;
        }

    }

    /**
     * 查询评论信息
      * @param listVo 请求的次数和动态的id
     * @return 五条评论
     */
    @Override
    public List<Comment> listPage(CommentListVo listVo) {

        Integer pagNum = (listVo.getReqCount() - 1) * 5;
        PageHelper.startPage(pagNum,5);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dynamic_id", listVo.getDynamicId());
        List<Comment> comments = commentDao.selectList(queryWrapper);
        for (Comment comment : comments) {
            // 注入评论的用户，并且设置密码为null
            Integer userId = comment.getUserId();
            User user = userDao.selectById(userId);
            user.setPassword(null);
            comment.setUser(user);
        }
        return comments;
    }

    /**
     * 通过id查询评论
     * @param id 评论的id
     * @return 对应的一条评论
     */
    @Override
    public Comment get(Integer id) {
        return commentDao.selectById(id);
    }

}
