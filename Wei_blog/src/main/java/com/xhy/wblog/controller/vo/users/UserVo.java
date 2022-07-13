package com.xhy.wblog.controller.vo.users;

// 本处是用来接收前端数据的实体对象

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString              // 生成toString方法
public class UserVo {

    private String name;
    private String password;
    private Integer gender;

}
