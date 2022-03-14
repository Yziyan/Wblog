package com.xhy.wblog.service.impl;

import com.xhy.wblog.dao.DynamicDao;
import com.xhy.wblog.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DynamicServiceImpl implements DynamicService {
    // 自动初始化改dao
    @Autowired
    private DynamicDao dao;




}
