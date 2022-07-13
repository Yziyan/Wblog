package com.xhy.wblog.utils.md5;

import org.apache.commons.codec.digest.DigestUtils;

// 加密工具类

public class Md5 {

    public final static String md5key = "xhy";


    /**
     * 用来加密
     *
     * @param text 明文 222
     * @param key  密钥 xhy
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text, String key) throws Exception {
        // 加密后的字符串 加盐操作
        String encodeStr = DigestUtils.md5Hex(text + key);
        return encodeStr;
    }

    /**
     * 用来验证
     *
     * @param text 明文
     * @param key  密钥
     * @param key  密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String key, String md5) throws Exception {
        // 根据传入的密钥进行验证
        String md5Text = md5(text, key);
        if (md5Text.equalsIgnoreCase(md5)) { // 相同
            System.out.println("MD5验证通过");
            return true;
        }
        return false;
    }

}
