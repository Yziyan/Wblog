package com.xhy.wblog.controller;


import com.xhy.wblog.service.FansService;
import com.xhy.wblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/fans")
public class FansController {

    //用户
    @Autowired
    private UserService userService;

    @Autowired
    private FansService fansService;



}
