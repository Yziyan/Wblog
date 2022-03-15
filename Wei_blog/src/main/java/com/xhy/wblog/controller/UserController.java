package com.xhy.wblog.controller;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.xhy.wblog.controller.result.Code;
import com.xhy.wblog.controller.result.PublicResult;
import com.xhy.wblog.controller.vo.dynamic.DynamicNew;
import com.xhy.wblog.controller.vo.users.RegisterVo;
import com.xhy.wblog.controller.vo.users.LoginVo;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.UserService;
import com.xhy.wblog.utils.exception.ExceptUtil;
import com.xhy.wblog.utils.sendemail.EmaiUtils;
import com.xhy.wblog.utils.upload.FileUpload;
import com.xhy.wblog.utils.upload.UploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import java.util.*;

/**
 * 使用Restful的风格进行数据交互。
 *
 * @RestController 这个包含了Json字符串转换
 * @RequestMapping("/users") 路径：在ctx下拼接 /users
 */

@RestController
@RequestMapping("/users")
public class UserController {

    /*
    我们统一一下
    读取： Get请求
    写入： Post请求
     */

    // 自动注入service
    @Autowired
    private UserService userService;

    //自动注入邮箱发送类
    @Autowired
    private JavaMailSender javaMailSender;


    //邮件发送验证码
    @RequestMapping("email")
    public PublicResult sendEmail(@RequestBody RegisterVo registerVo){
        EmaiUtils emaiUtils = new EmaiUtils();
        // 创建Kaptcha对象
        DefaultKaptcha dk = new DefaultKaptcha();
        // 验证码配置
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("kaptcha.properties")) {

            Properties properties = new Properties();
            properties.load(is);
            Config config = new Config(properties);
            dk.setConfig(config);
        } catch (IOException e) {
            return new PublicResult(true,Code.SAVE_ERROR,null,"网络波动，请重新申请！");
        }

        // 验证码字符串
        String code = dk.createText();

        if("发送成功".equals(emaiUtils.sendMail("欢迎注册！验证码为:"+code, "验证码", null, registerVo.getEmail(),
                javaMailSender, false))){
            return new PublicResult(true,Code.SAVE_OK,code,"邮箱已发送，请接收！");
        }else {
            return new PublicResult(true,Code.SAVE_ERROR,null,"邮箱发送失败！请检查邮箱是否正确并重新发送");
        }
//        return emaiUtils.sendMail("，欢迎注册！验证码为:", "验证码", null,
//                "2218094687@qq.com", javaMailSender, false);
    }

    // 验证码
    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 创建Kaptcha对象
        DefaultKaptcha dk = new DefaultKaptcha();
        // 验证码配置
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("kaptcha.properties")) {

            Properties properties = new Properties();
            properties.load(is);
            Config config = new Config(properties);
            dk.setConfig(config);
        }

        // 验证码字符串
        String code = dk.createText();
        // 将其字符串保存到session中
        HttpSession session = request.getSession();
        session.setAttribute("code", code.toLowerCase());

        // 将验证码字符串转换成验证码图片
        BufferedImage img = dk.createImage(code);
        response.setContentType("image/jpeg");
        ImageIO.write(img, "jpg", response.getOutputStream());

    }

    @RequestMapping("/login")
    public PublicResult login(@RequestBody LoginVo bean, HttpServletRequest request) {

        try {
            Map<String, Object> map = userService.login(bean);
            String code = (String) request.getSession().getAttribute("code");
            String captcha = bean.getCaptcha().toLowerCase();
            if (!captcha.equals(code)) { // 验证码，错误
                return new PublicResult(false, Code.LOGIN_ERROR, null, "验证码错误");
            } else { // 验证码正确
                if ((boolean) map.get("flag")) {
                    User user = (User) map.get("user");
                    // 保存用户id及其access_token到session中
                    HttpSession session = request.getSession();
                    String access_token = UUID.randomUUID().toString(); // 保证不一样就行
                    // 这是到时候拿来关联其他用的
                    session.setAttribute("access_token", access_token);
                    session.setAttribute("user_id", user.getId());
                    // 这是本页面用的user界面用的 不需要登录
                    session.setAttribute("user", user);

                    // 将查询信息响应给前台
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("access_token", access_token);
                    resultMap.put("user_id", user.getId());
                    return new PublicResult(true, Code.LOGIN_OK, map, (String) map.get("msg"));

                } else {
                    return new PublicResult(false, Code.LOGIN_ERROR, null, (String) map.get("msg"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new PublicResult(false, Code.LOGIN_ERROR, ExceptUtil.getSimpleException(e), "出错了！");
        }

    }

    @RequestMapping("register")
    public PublicResult register(@RequestBody RegisterVo registerVo, HttpServletRequest request) {
        try {
            String captcha = registerVo.getCaptcha().toLowerCase();
            String code = (String) request.getSession().getAttribute("code");
            if (!captcha.equals(code)) { // 验证码，错误
                return new PublicResult(false, Code.REGISTER_ERROR, null, "验证码错误");
            } else { // 验证码正确
                String url = String.valueOf(request.getRequestURL());
                // http://localhost:8080/wblog/users   将这个传入service
                String userUrl = url.substring(0, url.lastIndexOf("/") - 1);
                registerVo.setProfileUrl(userUrl);
                Map<String, Object> map = userService.register(registerVo);

                if ((boolean) map.get("flag")) {
                    return new PublicResult(true, Code.REGISTER_OK, null, (String) map.get("msg"));
                }
                return new PublicResult(true, Code.REGISTER_OK, null, (String) map.get("msg"));
            }
        } catch (Exception e) {
            // 来到这说明失败了
            return new PublicResult(false, Code.REGISTER_ERROR, ExceptUtil.getSimpleException(e), "出现了未知错误!");
        }
    }

    // 修改个人信息
    @RequestMapping("/update")
    public PublicResult update(@RequestBody User bean, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");

            if (user != null) { // 说明登录过了的
                // 将该用户的邮箱和id 还有原密码 放入传过来的 bean
                bean.setId(user.getId());
                bean.setEmail(user.getEmail());
                bean.setPassword(user.getPassword());

                // 将整合好的参数更新到数据库,并且将用户最新的信息返回
                User resUser = userService.update(bean);
                return new PublicResult(true, Code.UPDATE_OK, resUser, "修改成功");

            } else {
                return new PublicResult(false, Code.UPDATE_ERROR, null, "请登录");
            }

        } catch (Exception e) {
            return new PublicResult(false, Code.UPDATE_ERROR, ExceptUtil.getSimpleException(e), "保存失败");
        }

    }

    // 修改头像
    @RequestMapping("/fileUpload")
    public PublicResult update(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request) {

        try {
            // 获取登录的user
            User user = (User) request.getSession().getAttribute("user");

            if (user != null) { // 登录过了 ，可以操作
                // 将信息
                Map<String, Object> map = new HashMap<>();
                UploadResult result = FileUpload.uploadImage(file, request, user.getPhoto());
                map.put("fileName", result.getFileName());
                map.put("filePath", result.getFilePath());
                map.put("imagePath", result.getImagePath());
                // 将图片信息保存到数据库
                user.setPhoto(result.getImagePath());
                User resUser = userService.update(user);
                map.put("user", resUser);

                // 将文件名和文件路径返回，进行响应
                return new PublicResult(true, Code.UPLOAD_OK, map, "图片上传成功");

            } else {
                return new PublicResult(false, Code.UPLOAD_ERROR, null, "请登录");
            }


        } catch (Exception e) {
            return new PublicResult(true, Code.UPLOAD_ERROR, ExceptUtil.getSimpleException(e), "图片上传失败");
        }

    }
}
