package com.xhy.wblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xhy.wblog.dao.TopicDao;
import com.xhy.wblog.entity.Topic;
import com.xhy.wblog.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicDao topicDao;


    /**
     * 保存话题
     *
     * @param topicStr：话题的字符串
     * @return 是否成功
     */
    @Override
    public boolean save(String topicStr) {
        try {
            // 将话题拆分
            String[] topicsStr = topicStr.split(",");
            // 存入数据库
            for (String topStr : topicsStr) {
                // 如果是已经存在的话题，那么就跳过，不用保存了
                if (getByTheme(topStr) != null) continue;
                Topic topic = new Topic();
                topic.setTheme(topStr);
                topicDao.insert(topic);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通过主题搜索话题
     * @param theme：话题关键字
     * @return ：对应的话题记录
     */
    @Override
    public Topic getByTheme(String theme) {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("theme", theme);
        Topic topic = topicDao.selectOne(queryWrapper);

        if (topic != null) {
            topic.setQueryCount(topic.getQueryCount() + 1);
            topicDao.updateById(topic);
        }

        return topic;
    }


}
