package com.xhy.wblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhy.wblog.controller.result.Code;
import com.xhy.wblog.controller.result.PublicResult;
import com.xhy.wblog.controller.vo.users.PasswordVo;
import com.xhy.wblog.controller.vo.users.RegisterVo;
import com.xhy.wblog.controller.vo.users.LoginVo;
import com.xhy.wblog.dao.UserDao;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.UserService;
import com.xhy.wblog.utils.md5.Md5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    // 自动初始化改dao
    @Autowired
    private UserDao userDao;


    // 登录
    @Override
    public Map<String, Object> login(LoginVo bean) throws Exception {
        // 条件查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", bean.getEmail());
        User user = userDao.selectOne(queryWrapper);
        // 返回的map集合
        Map<String, Object> map = new HashMap<>();
        if (user != null) { // 代表改有改用户
            // 验证密码
            boolean verify = Md5.verify(bean.getPassword(), Md5.md5key, user.getPassword());
            if (verify) { // 代表是真
                // 将密码设置为空
                user.setPassword(null);
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
    public Map<String, Object> register(RegisterVo registerVo) throws Exception {

        QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("email", registerVo.getEmail());
        User user = userDao.selectOne(wrapper);
        Map<String, Object> map = new HashMap<>();
        if (user != null) {//如果不为空，证明已经被注册
            map.put("msg", "该邮件已经被注册!");
            map.put("flag", false);
        } else {//不然就创建user类，插入数据库
            user = new User();
            user.setEmail(registerVo.getEmail());
            String key = Md5.md5(registerVo.getPassword(), Md5.md5key);
            user.setPassword(key);
            user.setGender(registerVo.getGender());
            // 将
            userDao.insert(user);
            // 插入之后返回的id拼接到传过来的uri，保存到user的网址上
            user.setProfileUrl(registerVo.getProfileUrl() + user.getId());
            userDao.updateById(user);
            map.put("msg", "注册成功!");
            map.put("flag", true);
        }
        return map;
    }

    // 修改图片信息
    @Override
    public User update(User bean) {
        // 更新一下信息就行了
        User user;
        if (userDao.updateById(bean) > 0) {
            user = userDao.selectById(bean.getId());
        } else {
            user = null;
        }
        return user;
    }

    @Override
    public User selectById(int id) {
        User user = userDao.selectById(id);
        user.setPassword(null);
        return user;
    }


    // 修改密码
    @Override
    public Map<String, Object> updatePsd(PasswordVo bean) throws Exception {
        // 条件查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", bean.getUserId());
        User user = userDao.selectOne(queryWrapper);
        // 返回的map集合
        Map<String, Object> map = new HashMap<>();

        if (!Md5.verify(bean.getOldPassword(), Md5.md5key, user.getPassword())) {
            map.put("msg", "原密码错误");
            map.put("flag", false);
        } else { // 说明原密码相同，可以进行密码修改

            // 新密码
            String newPassword = Md5.md5(bean.getNewPassword(), Md5.md5key);

            if (newPassword.equals(user.getPassword())) { // 说明原密码相同和新密码相同
                map.put("msg", "新密码和原密码相同");
                map.put("flag", false);
            } else {
                user.setPassword(newPassword);
                if (userDao.updateById(user) > 0) {
                    map.put("msg", "修改成功,请重新登录！");
                    map.put("flag", true);
                } else {
                    map.put("msg", "出现了未知的错误");
                    map.put("flag", false);
                }
            }

        }

        return map;

    }

}
