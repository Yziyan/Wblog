package com.xhy.wblog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 返回的回复信息
@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString
public class ReplyText {
    // 用户首页
    private String profileUrl;
    // 用户姓名
    private String name;
    // 一级评论的文本
    private String text;
}
