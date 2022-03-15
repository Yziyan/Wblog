package com.xhy.wblog.utils.exception;

// 返回简单异常的工具类
public class ExceptUtil {

    public static String getSimpleException(Exception e) {
        e.printStackTrace();
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause.getClass().getSimpleName() + ": " + cause.getMessage();

    }

}
