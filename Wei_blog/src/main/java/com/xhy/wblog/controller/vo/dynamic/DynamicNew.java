package com.xhy.wblog.controller.vo.dynamic;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data   // 生成get和set
@AllArgsConstructor    // 生成全参构造
@NoArgsConstructor     // 生成空构造方法
@ToString
public class DynamicNew {

    private Integer nums;

    private Integer count;
}
