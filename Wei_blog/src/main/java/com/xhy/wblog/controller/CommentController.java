package com.xhy.wblog.controller;


import com.xhy.wblog.service.CommentService;
import com.xhy.wblog.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



// 评论模块

@RestController
@RequestMapping("/comment")
public class CommentController {

    // 自动注入service
    @Autowired
    private CommentService commentService;



}
