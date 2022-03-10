package com.xhy.wblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xhy.wblog.controller.result.Code;
import com.xhy.wblog.controller.result.PublicResult;
import com.xhy.wblog.controller.vo.RegisterVo;
import com.xhy.wblog.controller.vo.LoginVo;
import com.xhy.wblog.controller.result.Code;
import com.xhy.wblog.controller.result.PublicResult;
import com.xhy.wblog.controller.vo.RegisterVo;
import com.xhy.wblog.dao.UserDao;
import com.xhy.wblog.domain.User;
import com.xhy.wblog.service.UserService;
import com.xhy.wblog.utils.md5.Md5;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    // 自动初始化改dao
    @Autowired
    private UserDao dao;

    // 下面是CRUD的基本实现

    @Override
    public boolean save(User user) {
        return dao.insert(user) > 0;
    }

    @Override
    public boolean update(User user) {
        return dao.updateById(user) > 0;
    }

    @Override
    public boolean remove(Integer id) {
        return dao.deleteById(id) > 0;
    }

    @Override
    public User get(Integer id) {
        return dao.selectById(id);
    }

    @Override
    public List<User> list() {

         // 这个可以做条件查询，具体mybatis-plus的使用，自行百度。无条件就是查询全部
         QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        return dao.selectList(queryWrapper);
    }

    // 登录
    @Override
    public Map<String, Object> login(LoginVo bean) throws Exception {
        // 条件查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", bean.getEmail());
        User user = dao.selectOne(queryWrapper);

        // 返回的map集合
        Map<String, Object> map = new HashMap<>();
        if (user != null) { // 代表改有改用户
            // 验证密码
            boolean verify = Md5.verify(bean.getPassword(), Md5.md5key, user.getPassword());
            if (verify) { // 代表是真
                map.put("msg", "登陆成功");
                map.put("user", user);
                map.put("flag", true);
            } else {
                map.put("msg", "密码错误");
                map.put("flag", false);
            }
        } else {
            map.put("msg", "用户名不存在");
            map.put("flag", false);
        }
        return map;

    }

    //注册用--用email查找user
    @Override
    @Transactional(readOnly = false)
    public PublicResult register(RegisterVo registerVo) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("email",registerVo.getEmail());
        User user = dao.selectOne(wrapper);
        try {
            if(user!=null){//如果不为空，证明已经被注册
                return new PublicResult(false, Code.REGISTER_ERROR,null,"该邮件已经被注册!");
            }else{//不然就创建user类，插入数据库
                user = new User();
                user.setEmail(registerVo.getEmail());
                String key = Md5.md5(registerVo.getPassword(), Md5.md5key);
                user.setPassword(key);
                user.setGender(registerVo.getGender());
                dao.insert(user);
                return new PublicResult(true,Code.REGISTER_OK,null,"注册成功!");
            }
        } catch (Exception e) {
            // 来到这说明失败了
            e.printStackTrace();
            return new PublicResult(false,Code.REGISTER_ERROR, null,"出现了未知错误!");
        }
    }
}
