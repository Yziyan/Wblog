package com.xhy.wblog.controller;


import com.xhy.wblog.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// 电台发布模块

@RestController
@Configuration
@RequestMapping("/dynamic")
public class DynamicController {


    // 自动注入service
    @Autowired
    private DynamicService service;



}
