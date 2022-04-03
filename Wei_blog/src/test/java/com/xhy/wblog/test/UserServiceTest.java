package com.xhy.wblog.test;


import com.xhy.wblog.dao.UserDao;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.DynamicService;
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
public class UserServiceTest {

    @Autowired
    private UserService service;

    @Autowired
    private UserDao userDao;


    @Autowired
    private DynamicService dynamicService;

    @Test
    public void test(){

        for(int i = 0;i<3;i++){
            User user = userDao.getUser(4);
            user.setPhoto("hello");
            System.out.println(user.getPhoto());
        }

    }


}
