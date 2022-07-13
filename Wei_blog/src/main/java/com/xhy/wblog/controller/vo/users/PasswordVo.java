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
public class PasswordVo {

    // 旧密码
    private String oldPassword;

    // 新密码
    private String newPassword;
    // 用户id
    private Integer userId;
}
