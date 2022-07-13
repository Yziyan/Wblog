package com.xhy.wblog.service;

import com.xhy.wblog.entity.Topic;
import com.xhy.wblog.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 话题模块
@Transactional(readOnly = true)
public interface TopicService {


    // 保存话题
    @Transactional(readOnly = false)
    boolean save(String topicStr);

    // 通过主题搜索话题
    @Transactional(readOnly = false)
    Topic getByTheme(String theme);
}
