package com.xhy.wblog.service.impl;

import com.xhy.wblog.dao.TopicDao;
import com.xhy.wblog.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicDao topicDao;



}
