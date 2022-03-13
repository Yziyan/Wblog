package com.xhy.wblog.controller.result;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 本类是拿来返回前端数据的实体类
@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString              // 生成toString方法
public class PublicResult {

    // 状态
    private Boolean success;
    // 操作结果编码
    private Integer code;
    // 操作数据结果
    private Object data;
    // 返回消息
    private String message;


}
