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

@TableName("t_fans_attention")   // 说明实体表名
@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString
public class Fans {


    @TableId(type = IdType.AUTO)    //主键自增
    @TableField("id")
    private Integer id;

    //时间
    private Date createdTime;

    //被关注的人的id
    private Integer attentionUseId;

    //关注人的id
    private Integer fansUserId;

}
