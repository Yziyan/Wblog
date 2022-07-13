package com.xhy.wblog.utils.upload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 本类是拿来文件上传结果的实体类
@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString              // 生成toString方法
public class UploadResult {

    // 文件名
    private String fileName;
    // 文件路径
    private String filePath;
    // 数据库的文件地址
    private String imagePath;

}
