package com.xhy.wblog.controller.vo.comment;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 接收前端发布动态的数据实体类
@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString              // 生成toString方法
public class PushCommentVo {

    // 评论的id
    private Integer id;
    // 文本
    private String text;
    // 权限
    private Integer replyId;
    // 评论的动态id
    private Integer dynamicId;
    // 用户id
    private Integer userId;

}
