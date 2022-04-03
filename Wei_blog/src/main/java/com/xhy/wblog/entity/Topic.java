package com.xhy.wblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * (TTopic)实体类
 *
 *
 * @since 2022-04-03 10:57:51
 */
@TableName("t_topic")   // 说明实体表名
@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法

public class Topic {
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
     * 话题
     */
    private String theme;
    /**
     * 搜索次数
     */
    private Integer queryCount;




}

