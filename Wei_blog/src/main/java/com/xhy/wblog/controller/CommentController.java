package com.xhy.wblog.controller;


import com.xhy.wblog.controller.result.Code;
import com.xhy.wblog.controller.result.PublicResult;
import com.xhy.wblog.controller.vo.comment.CommentListVo;
import com.xhy.wblog.controller.vo.comment.PushCommentVo;
import com.xhy.wblog.entity.Comment;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.CommentService;
import com.xhy.wblog.service.DynamicService;
import com.xhy.wblog.service.UserService;
import com.xhy.wblog.utils.converter.ReqUrlStr;
import com.xhy.wblog.utils.exception.ExceptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


// 评论模块

@RestController
@RequestMapping("/comment")
public class CommentController {

    // 自动注入service
    @Autowired
    private CommentService commentService;

    // 自动注入service
    @Autowired
    private DynamicService dynamicService;

    // 自动注入service
    @Autowired
    private UserService userService;

    // 发表评论

    @RequestMapping("/pushComment")
    public PublicResult pushComment(@RequestBody PushCommentVo bean, HttpServletRequest request) {
        try {
            // 获取登录的user
            User user = (User) request.getSession().getAttribute("user");

            if (user != null) { // 登录过了 ，可以操作
                // 保存评论
                Comment comment = commentService.save(bean);
                return new PublicResult(true, Code.PUSH_OK, comment, "评论成功");

            } else {
                return new PublicResult(false, Code.PUSH_ERROR, null, "请登录");
            }
        } catch (Exception e) {
            return new PublicResult(false, Code.PUSH_ERROR, ExceptUtil.getSimpleException(e), "评论失败");
        }
    }

    // 删除评论
    @RequestMapping("/removeComment")
    public PublicResult removeComment(Integer commentId, HttpServletRequest request) {
        try {
            // 获取登录的user
            User user = (User) request.getSession().getAttribute("user");

            if (user != null) { // 登录过了 ，可以操作

                // 查出是哪个用户发了这条评论、若不是登录的用户，或者不是发动态的用户则无权限删除
                Comment comment = commentService.get(commentId);
                Integer commUserId = comment.getUserId();
                Integer userId = dynamicService.getById(comment.getDynamicId()).getUserId();
                Integer dynUserId = userService.selectById(userId).getId();
                if (commUserId != user.getId() && user.getId() != dynUserId)
                    return new PublicResult(false,
                        Code.DELETE_ERROR, null, "不是你的评论，你无法删除");
                if (commentService.removeById(commentId)) {
                    return new PublicResult(true, Code.DELETE_OK, null, "删除成功");
                } else {
                    return new PublicResult(false, Code.DELETE_ERROR,
                            null, "出现了一个未知的错误");
                }
            } else {
                return new PublicResult(false, Code.DELETE_ERROR, null, "请登录");
            }
        } catch (Exception e) {
            return new PublicResult(false, Code.DELETE_ERROR,
                    ExceptUtil.getSimpleException(e), "出现了未知错误!");
        }
    }

    // 查看评论
    @RequestMapping("list")
    public PublicResult list(@RequestBody CommentListVo listVo, HttpServletRequest request) {
        try {
            // 将url传入
            listVo.setReqUrl(ReqUrlStr.getUrl(request));
            List<Comment> comments = commentService.list(listVo);
            if (comments.size() > 0) {
                //  若有评论，则返回评论
                return new PublicResult(true, Code.QUERY_OK, comments, "评论加载成功");
            } else {
                return new PublicResult(true, Code.QUERY_OK, null, "还没有评论！");
            }
        } catch (Exception e) {
            return new PublicResult(false, Code.QUERY_ERROR,
                    ExceptUtil.getSimpleException(e), "出现了一个未知的错误");
        }
    }

    // 点赞
    @RequestMapping("setLike")
    public PublicResult setLike(Integer commentId) {
        try {
            if (commentService.updateHits(commentId, "set")) {
                // 设置成功返回当前的点赞数量
                return new PublicResult(true, Code.UPLOAD_OK,
                        commentService.getHits(commentId), "点赞成功");
            } else {
                return new PublicResult(false, Code.UPDATE_ERROR, null, "点赞失败");
            }

        } catch (Exception e) {
            return new PublicResult(false, Code.UPDATE_ERROR,
                    ExceptUtil.getSimpleException(e), "出现了一个未知的错误");
        }
    }

    // 点赞
    @RequestMapping("cancelLike")
    public PublicResult cancelLike(Integer commentId) {
        try {
            if (commentService.updateHits(commentId, "cancel")) {
                // 设置成功返回当前的点赞数量
                return new PublicResult(true, Code.UPLOAD_OK,
                        commentService.getHits(commentId), "取消点赞成功");
            } else {
                return new PublicResult(false, Code.UPDATE_ERROR, null, "取消点赞失败");
            }
        } catch (Exception e) {
            return new PublicResult(false, Code.UPDATE_ERROR,
                    ExceptUtil.getSimpleException(e), "出现了一个未知的错误");
        }
    }

}
