package com.xhy.wblog.controller.result;


 // 状态码

public class Code {

    // 操作结果编码
    // 成功
    public static final Integer SAVE_OK = 20011;
    public static final Integer UPDATE_OK = 20021;
    public static final Integer DELETE_OK = 20031;
    public static final Integer QUERY_OK = 20041;
    public static final Integer LOGIN_OK = 20051;
    public static final Integer REGISTER_OK = 20061;
    public static final Integer CAPTCHA_OK = 20071; // 验证码
    public static final Integer UPLOAD_OK = 20081; // 文件上传
    public static final Integer PUBLISH_OK = 20091; // 文件上传
    // 失败
    public static final Integer SAVE_ERROR = 40010;
    public static final Integer UPDATE_ERROR = 40020;
    public static final Integer DELETE_ERROR = 40030;
    public static final Integer QUERY_ERROR = 40040;
    public static final Integer LOGIN_ERROR = 40050;
    public static final Integer REGISTER_ERROR = 40060;
    public static final Integer CAPTCHA_ERROR= 40070; // 验证码
    public static final Integer UPLOAD_ERROR= 40080; // 文件上传
    public static final Integer PUBLISH_ERROR= 40090; // 文件上传




}
