package com.xhy.wblog.test;


import com.xhy.wblog.service.DynamicService;
import com.xhy.wblog.service.TopicService;
import com.xhy.wblog.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

// 该类是UserService的单元测试
// 整合工具类默认写法,配置之后就可以直接注入了。

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class

TopicServiceTest {

    @Autowired
    private TopicService topicService;


    @Test
    public void testSave() {
        String test = "#giao#";
        topicService.save(test);

    }


}
