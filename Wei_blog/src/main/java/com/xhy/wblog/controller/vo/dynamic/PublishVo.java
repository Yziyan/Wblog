package com.xhy.wblog.controller.vo.dynamic;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 接收前端发布动态的数据实体类
@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString              // 生成toString方法
public class PublishVo {

    // 动态的id
    private Integer id;
    // 文本
    private String text;
    // 权限
    private Integer visible;
    // 是否转发
    private Integer forwardDynamicId;
    // 用户id
    private Integer UserId;
    // 文件数据
    private String fileVo;

}
