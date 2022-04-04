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
import java.util.List;

/**
 * 用户信息表(TUser)实体类
 *
 * @author ZhiYan
 * @since 2022-03-12 22:04:36
 */

@TableName("t_user")   // 说明实体表名
@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString              // 生成toString方法
public class User implements Serializable {

    @TableId(type = IdType.AUTO)    //主键自增
    @TableField("id")               // 表的属性名，若属性与表字段相同，或者可以进行驼峰映射，可以不写（下面我都没写）
    private Integer id;

    private Date createdTime;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 用户昵称
     */
    private String name;
    /**
     * 用户生日
     */
    private Date birthday;
    /**
     * 用户地址
     */
    private String address;
    /**
     * 用户电话
     */
    private String phone;
    /**
     * 用户头像，单文件
     */
    private String photo;
    /**
     * 用户职业
     */
    private String job;
    /**
     * 用户简介
     */
    private String intro;
    /**
     * 用户特质
     */
    private String trait;
    /**
     * 用户兴趣
     */
    private String interests;
    /**
     * 用户性别
            0：男
            1：女
     */
    private Integer gender;
    /**
     * 默认是0
            用户的微博数量
     */
    private Integer dynamicCount;
    /**
     * 默认是0
            用户的关注数量
     */
    private Integer friendsCount;
    /**
     * 默认是0
            用户的粉丝数
     */
    private Integer fansCount;
    /**
     * 用户的唯一网址
            点击头像或者姓名，可以跳转到个人界面
     */
    private String profileUrl;
    /**
     * 是否关注我，默认是0
            0：否
            1：是
     */
    private Integer followMe;
    /**
     * 是不是我的关注，默认是0
            0：否
            1：是
     */
    private Integer following;
    /**
     * 背景图片
     */
    private String background;

    @TableField(exist = false)
    private Boolean isSubscript;
}

