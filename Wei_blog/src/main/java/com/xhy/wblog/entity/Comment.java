package com.xhy.wblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.io.Serializable;

/**
 * 这是用来装所有评论的表(Comment)实体类
 *
 * @author
 * @since 2022-03-15 13:35:48
 */
@TableName("t_comment")   // 说明实体表名
@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString              // 生成toString方法
public class Comment implements Serializable {

    @TableId(type = IdType.AUTO)    //主键自增
    @TableField("id")
    private Integer id;
    
    private Date createdTime;

    /**
     * 评论的内容
     */
    private String text;

    /**
     * 评论的点赞数量
     */
    private Integer hits;
    /**
     * 评论者的id
     */
    private Integer userId;
    /**
     * 动态的id
     */
    private Integer dynamicId;
    /**
     * 这条评论是否是回复评论的：
            不是 ： 0
            是 ： 那条评论的id
            
     */
    private Integer replyId;

    // 返回的回复的信息
    @TableField(exist = false)
    private ReplyText replyText;

    /**
     *  发表评论的用户
     *  表示此属性不映射到数据库
     */
    @TableField(exist = false)
    private User user;

    /**
     *  楼层Id
     */
    private Integer floorId;
    /**
     *  是否可见
     */
    private Integer enable;



}

