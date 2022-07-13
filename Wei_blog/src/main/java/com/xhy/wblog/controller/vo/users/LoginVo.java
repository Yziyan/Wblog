package com.xhy.wblog.controller.vo.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 接收前端登录数据实体类
@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString              // 生成toString方法
public class LoginVo {

    // 邮件
    private String email;
    // 密码
    private String password;
    // 验证码
    private String captcha;


}
