package com.xhy.wblog.controller;


import com.xhy.wblog.controller.result.Code;
import com.xhy.wblog.controller.result.PublicResult;
import com.xhy.wblog.controller.vo.comment.PushCommentVo;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.CommentService;
import com.xhy.wblog.utils.exception.ExceptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


// 评论模块

@RestController
@RequestMapping("/comment")
public class CommentController {

    // 自动注入service
    @Autowired
    private CommentService commentService;


    // 发表评论

    @RequestMapping("/pushComment")
    public PublicResult pushComment(@RequestBody PushCommentVo bean, HttpServletRequest request) {
        try {
            // 获取登录的user
            User user = (User) request.getSession().getAttribute("user");

            if (user != null) { // 登录过了 ，可以操作
                // 保存评论
                Map<String, Object> map = commentService.save(bean);
                return new PublicResult(true, Code.PUSH_OK, map, "评论成功");

            } else {
                return new PublicResult(false, Code.PUSH_ERROR, null, "请登录");
            }
        } catch (Exception e) {
            return new PublicResult(false, Code.PUSH_ERROR, ExceptUtil.getSimpleException(e), "评论失败");
        }
    }


}
