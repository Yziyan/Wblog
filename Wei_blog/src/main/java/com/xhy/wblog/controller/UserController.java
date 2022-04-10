package com.xhy.wblog.controller;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.xhy.wblog.controller.result.Code;
import com.xhy.wblog.controller.result.PublicResult;
import com.xhy.wblog.controller.vo.users.PasswordVo;
import com.xhy.wblog.controller.vo.users.RegisterVo;
import com.xhy.wblog.controller.vo.users.LoginVo;
import com.xhy.wblog.entity.Dynamic;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.DynamicService;
import com.xhy.wblog.service.FansService;
import com.xhy.wblog.service.UserService;
import com.xhy.wblog.utils.converter.ReqUrlStr;
import com.xhy.wblog.utils.exception.ExceptUtil;
import com.xhy.wblog.utils.sendemail.EmaiUtils;
import com.xhy.wblog.utils.upload.FileUpload;
import com.xhy.wblog.utils.upload.UploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
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
@CrossOrigin(value = "http://172.20.10.4:8080", allowCredentials = "true")
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

    // 自动组人dynamicService
    @Autowired
    private DynamicService dynamicService;
    // 自动组人fansService
    @Autowired
    private FansService fansService;
    //自动注入邮箱发送类
    @Autowired
    private JavaMailSender javaMailSender;


    //邮件发送验证码
    @RequestMapping("/email")
    public PublicResult sendEmail(@RequestBody RegisterVo registerVo,HttpServletRequest request) {
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
            return new PublicResult(true, Code.SAVE_ERROR, null, "网络波动，请重新申请！");
        }

        // 验证码字符串
        String code = dk.createText();
        // 将其字符串保存到session中
        HttpSession session = request.getSession();
        session.setAttribute("email", code.toLowerCase());

        if ("发送成功".equals(emaiUtils.sendMail("欢迎注册！验证码为:" + code, "验证码", null, registerVo.getEmail(),
                javaMailSender, false))) {
            return new PublicResult(true, Code.SAVE_OK, null, "邮箱已发送，请接收！");
        } else {
            return new PublicResult(true, Code.SAVE_ERROR, null, "邮箱发送失败！请检查邮箱是否正确并重新发送");
        }
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

            String code = (String) request.getSession().getAttribute("code");
            String captcha = bean.getCaptcha().toLowerCase();
            if (!captcha.equals(code)) { // 验证码，错误
                return new PublicResult(false, Code.LOGIN_ERROR, null, "验证码错误");
            } else { // 验证码正确

                Map<String, Object> map = userService.login(bean);
                if ((boolean) map.get("flag")) {
                    User user = (User) map.get("user");
                    // 查出这个用户的动态
                    List<Dynamic> dynamics = dynamicService.getByUserId(user.getId());
                    // 保存到map中返回给前端
                    map.put("dynamic", dynamics);

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

    @RequestMapping("/register")
    public PublicResult register(@RequestBody RegisterVo registerVo, HttpServletRequest request) {
        try {
            String captcha = registerVo.getCaptcha().toLowerCase();
            String code = (String) request.getSession().getAttribute("email");
            if (!captcha.equals(code)) { // 验证码，错误!captcha.equals(code)
                return new PublicResult(false, Code.REGISTER_ERROR, null, "验证码错误");
            } else { // 验证码正确
                String uri = ReqUrlStr.getUrl(request);
                // http://localhost:8080/wblog/users   将这个传入service
                String userUrl = uri.substring(0, uri.lastIndexOf("/")) + "/users/u";
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
    public PublicResult update(@RequestBody User bean, HttpServletRequest request) {
        try {
            User user = (User) request.getSession().getAttribute("user");

            if (user != null) { // 说明登录过了的
                // 将该用户的邮箱和id 还有原密码 放入传过来的 bean
                bean.setId(user.getId());
                bean.setEmail(user.getEmail());
                bean.setPassword(user.getPassword());

                // 将整合好的参数更新到数据库,并且将用户最新的信息返回
                User resUser = userService.update(bean);
                // 更新session
                request.getSession().setAttribute("user", resUser);
                return new PublicResult(true, Code.UPDATE_OK, resUser, "修改成功");

            } else {
                return new PublicResult(false, Code.UPDATE_ERROR, null, "请登录");
            }

        } catch (Exception e) {
            return new PublicResult(false, Code.UPDATE_ERROR, ExceptUtil.getSimpleException(e), "保存失败");
        }

    }

    // 修改头像
    @RequestMapping("/photoImage")
    public PublicResult update(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request) {

        try {
            // 获取登录的user
            User user = (User) request.getSession().getAttribute("user");

            if (user != null) { // 登录过了 ，可以操作
                // 将信息
                Map<String, Object> map = new HashMap<>();
                String oldPhoto = user.getPhoto();
                UploadResult result;
                if (oldPhoto != null && oldPhoto.length() > 0) { // 说明以前有头像,才需要把以前的头像传进去
                    String oldFile = oldPhoto.substring(user.getPhoto().lastIndexOf("upload/"));
                    result = FileUpload.uploadImage(file, request, oldFile);
                } else { // 以前没有头像，不需要传以前的地址
                    result = FileUpload.uploadImage(file, request, null);
                }
                map.put("file", result);
                // 将图片信息保存到数据库
                user.setPhoto(result.getImagePath());
                User resUser = userService.update(user);
                resUser.setPassword(null);
                resUser.setPhoto(result.getFilePath());
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

    @RequestMapping("u{id}")
    public PublicResult admin(@PathVariable Integer id, HttpServletRequest request) {
        try {

            String basePath = ReqUrlStr.getUrl(request);
            // 拿到登录用户的id
            User loginUser = (User) request.getSession().getAttribute("user");
            // 查询此用户的信息、动态
            User user = userService.selectById(id);
            String userPhoto = user.getPhoto();
            String userBGround = user.getBackground();
            user.setPhoto(userPhoto != null ? basePath + userPhoto : null);
            user.setBackground(userBGround != null ? basePath + userBGround : null);
            // 如果登录了，才设置是否关注
            if (loginUser != null) {
                Integer loginUserId = loginUser.getId();
                // 给用户注入是否关注字段
                user.setIsSubscript(fansService.urlIsSubscript(loginUserId, id));
            } else {
                user.setIsSubscript(false);
            }

            List<Dynamic> dynamics = dynamicService.getByUserId(id, basePath);

            // 返回给客户端
            Map<String, Object> map = new HashMap<>();
            map.put("user", user);
            map.put("dynamic", dynamics);
            return new PublicResult(true, Code.QUERY_OK, map, user.getName() + "的主页");
        } catch (Exception e) {
            return new PublicResult(false, Code.QUERY_ERROR, ExceptUtil.getSimpleException(e), "有一个未知的错误！");
        }
    }
    // 修改背景
    @RequestMapping("/bagImage")
    public PublicResult updateBackground(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request) {

        try {
            // 获取登录的user
            User user = (User) request.getSession().getAttribute("user");

            if (user != null) { // 登录过了 ，可以操作
                // 将信息放入map中
                Map<String, Object> map = new HashMap<>();
                String oldPhoto = user.getBackground();
                String oldPhoto2 = user.getPhoto();
                UploadResult result;
                if (oldPhoto != null && oldPhoto.length() > 0) { // 说明以前有背景,才需要把以前的背景传进去
                    String oldFile = oldPhoto.substring(user.getBackground().lastIndexOf("upload/"));
                    result = FileUpload.uploadImage(file, request, oldFile);
                } else { // 以前没有背景，不需要传以前的地址
                    result = FileUpload.uploadImage(file, request, null);
                }
                map.put("file", result);
                // 将图片信息保存到数据库
                user.setBackground(result.getImagePath());
                User resUser = userService.update(user);
                resUser.setPassword(null);
                resUser.setBackground(result.getFilePath());
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
    // 修改密码
    @RequestMapping("/updatePsd")
    public PublicResult updatePsd(@RequestBody PasswordVo passwordVo, HttpSession session) {

        try {
            // 获取登录的user
            User user = (User) session.getAttribute("user");

            if (user != null) { // 登录过了 ，可以操作

                // 注入登录用户的id
                passwordVo.setUserId(user.getId());
                Map<String, Object> map = userService.updatePsd(passwordVo);
                if ((boolean)map.get("flag")) { // 说明修改成功了
                    // 清空session中的user
                    session.removeAttribute("user");
                    return new PublicResult(true, Code.UPLOAD_OK, map, "修改成功");
                } else {
                    return new PublicResult(false, Code.UPLOAD_ERROR, map, "修改失败");
                }
            } else {
                return new PublicResult(false, Code.UPLOAD_ERROR, null, "请登录");
            }


        } catch (Exception e) {
            return new PublicResult(true, Code.UPLOAD_ERROR, ExceptUtil.getSimpleException(e), "出现了未知的错误");
        }
    }

    // 退出登录
    @RequestMapping("/loginOut")
    public PublicResult loginOut(HttpSession session) {

        try {

            // 清空session就行
            session.removeAttribute("user");
            return new PublicResult(true, 20061, null, "退出成功");

        } catch (Exception e) {
            return new PublicResult(true, 40060, ExceptUtil.getSimpleException(e), "出现了未知的错误");
        }

    }

}
