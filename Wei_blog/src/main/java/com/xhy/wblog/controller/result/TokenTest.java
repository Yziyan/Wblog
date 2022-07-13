package com.xhy.wblog.controller.result;

import com.xhy.wblog.utils.md5.Md5;

public class TokenTest {
    //秘钥
    private final static String password = "xhy";

    public static String addPassword(String getMsg) {//getMsg 比如验证码/email、password。。。。
        try {
            return Md5.md5(getMsg, password);
        } catch (Exception e) {
            return addPassword(getMsg);
        }
    }

    public static boolean checkToken(String getMsg, String token) {
        return token.equals(addPassword(getMsg));
    }


}
