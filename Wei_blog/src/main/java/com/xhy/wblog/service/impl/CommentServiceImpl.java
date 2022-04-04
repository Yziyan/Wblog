package com.xhy.wblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xhy.wblog.controller.vo.comment.CommentListVo;
import com.xhy.wblog.controller.vo.comment.PushCommentVo;
import com.xhy.wblog.dao.CommentDao;
import com.xhy.wblog.dao.DynamicDao;
import com.xhy.wblog.dao.UserDao;
import com.xhy.wblog.entity.Comment;
import com.xhy.wblog.entity.Dynamic;
import com.xhy.wblog.entity.ReplyText;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.CommentService;
import com.xhy.wblog.utils.converter.ReqUrlStr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


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

    public Comment getCommentById(Integer dynamicId) {
        return commentDao.selectById(dynamicId);
    }

    private ReplyText getReplyText(Integer replyId) {
        if (replyId == 0) return null;
        // 返回的回复信息的类
        ReplyText replyText = new ReplyText();
        // 查出回复的那一条评论
        Comment replyComment = commentDao.selectById(replyId);
        User replyUser = userDao.selectById(replyComment.getUserId());
        replyText.setText(replyComment.getText());
        replyText.setName(replyUser.getName());
        replyText.setProfileUrl(replyUser.getProfileUrl());
        return replyText;
    }

    // 保存评论、
    @Override
    public Comment save(PushCommentVo bean) {

        // 拿到四者的id待会查数据
        Integer replyId = bean.getReplyId();
        Integer dynamicId = bean.getDynamicId();
        Integer floorID = bean.getFloorId();
        Integer userId = bean.getUserId();
        Comment comment = new Comment();


        // 注入评论所需的 字段
        comment.setText(bean.getText());
        comment.setDynamicId(dynamicId);
        comment.setReplyId(replyId);
        comment.setFloorId(floorID);
        comment.setUserId(userId);


        if (commentDao.insert(comment) > 0) { // 保存评论成功


            // 查出必须的，评论的是哪一条动态、是谁评论的
            Dynamic dynamic = dynamicDao.selectById(dynamicId);
            // 将评论数量+1
            updateCount(dynamicId, "增加");

            // 将这条评论的信息返回，并且注写这条评论的用户
            User user = userDao.selectById(userId);
            user.setPassword(null);
            comment = commentDao.selectById(comment.getId());
            comment.setUser(user);
            if (replyId != 0) { // 说明是回复
                // 注入回复所需的信息
                comment.setReplyText(getReplyText(replyId));
            }

            return comment;
        } else {
            return null;
        }

    }

    /**
     * 删除评论模块
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Integer id) {


        Integer dynamicId = commentDao.selectById(id).getDynamicId();
        // 将改评论的设置成不可见
        Comment comment = get(id);
        comment.setEnable(0);
        if (commentDao.updateById(comment) > 0) { // 说明删除成功，还需要将动态的评论数减1
            updateCount(dynamicId, "减少");
            return true;
        } else {
            return false;
        }

    }

    /**
     * 查询评论信息
     *
     * @param listVo 动态的id和所在楼
     * @return 五条评论
     */
    @Override
    public List<Comment> list(CommentListVo listVo) {

        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();

            queryWrapper.eq("dynamic_id", listVo.getDynamicId())
                    .eq("floor_id", listVo.getFloorId())
                    .eq("enable", 1).orderByDesc("created_time");

        List<Comment> comments = commentDao.selectList(queryWrapper);

        // 将评论注入用户信息
        for (Comment comment : comments) {
            // 注入评论的用户，并且设置密码为null
            Integer userId = comment.getUserId();
            User user = userDao.getUser(userId);
            user.setPassword(null);
            String photo = listVo.getReqUrl() + user.getPhoto();
            user.setPhoto(photo);
            comment.setUser(user);
            // 若是回复的，那么注入ReplyText
            comment.setReplyText(getReplyText(comment.getReplyId()));
            // 若是一级评论。那么返回评论数
            if (comment.getReplyId() == 0) {
                QueryWrapper<Comment> wrapperCount = new QueryWrapper();
                wrapperCount.eq("floor_id", comment.getId());
                Long replyCount = commentDao.selectCount(wrapperCount);
                comment.setReplyCount(replyCount);
            }
        }
        return comments;
    }

    /**
     * 通过id查询评论
     *
     * @param id 评论的id
     * @return 对应的一条评论
     */
    @Override
    public Comment get(Integer id) {
        return commentDao.selectById(id);
    }

    /**
     * 点赞功能、取消点赞
     *
     * @param commentId : 点赞评论的id
     * @return
     */
    @Override
    public boolean updateHits(Integer commentId, String choose) {
        // 查出要点赞的评论
        Comment comment = commentDao.selectById(commentId);
        if (choose.equals("set")) { // 若是点赞就 + 1
            comment.setHits(comment.getHits() + 1);
        } else {
            comment.setHits(comment.getHits() - 1);
        }
        return commentDao.updateById(comment) > 0;
    }

    /**
     * 获取当前评论的点赞数
     *
     * @param commentId :评论id
     * @return ： 点赞数
     */
    @Override
    public Integer getHits(Integer commentId) {
        return commentDao.selectById(commentId).getHits();
    }

    /**
     *
     * @param dynamicId ： 动态的id
     * @return 是否成功
     */
    @Override
    public boolean removeAll(Integer dynamicId) {
        // 删除动态，顺带删除所有的评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dynamic_id", dynamicId);
        return commentDao.delete(queryWrapper) > 0;

    }

}
