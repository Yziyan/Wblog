package com.xhy.wblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Comparator;
import java.util.Date;
import java.io.Serializable;
import java.util.List;

/**
 * 这是一张动态表(TDynamic)实体类
 *
 * @author
 * @since 2022-03-14 21:29:06
 */

@TableName("t_dynamic")   // 说明实体表名
@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString              // 生成toString方法
public class Dynamic implements Serializable {
    /**
     * 唯一标识
     */
    @TableId(type = IdType.AUTO)    //主键自增
    @TableField("id")
    private Integer id;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 文本内容
     */
    private String text;

    @TableField(exist = false)
    private List<ForwardText> forwardTexts;

    /**
     * 文件内容
     */
    private String file;

    //文件路径
    @TableField(exist = false)
    private List<String> filePath;
    /**
     * 默认是0
            0 ： 公开
            10：粉丝
            20：仅自己
     */
    private Integer visible;
    /**
     * 点赞数量，默认是0
     */
    private Integer hits;
    /**
     * 评论的数量，默认是0
     */
    private Integer commentsCount;
    /**
     * 默认是0：
            不是转发的：0
            是转发的 ： 转发的动态的 id
     */
    private Integer forwardDynamicId;

    /**
     * 发布动态用户的id
     */
    private Integer userId;
    /**
     *
     *  是否可见
     */
    private Integer enable;
    /**
     *
     *  话题
     */
    private String theme;

    /**
     *  发布动态的用户
     *  表示此属性不映射到数据库
     */
    @TableField(exist = false)
    private User user;

    //转发的动态
    @TableField(exist = false)
    private Dynamic forwardDynamic;

}

