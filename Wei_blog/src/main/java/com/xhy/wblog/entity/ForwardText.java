package com.xhy.wblog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString
public class ForwardText {

    private String profileUrl;
    private String name;
    private String text;
    private List<String> filePath;
}
