package com.xhy.wblog.utils.converter;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;

// url
public class ReqUrlStr {

    public static String getUrl( HttpServletRequest request) {
        String appContext = request.getContextPath();
        String basePath = request.getScheme() + "://"
                + request.getServerName() + ":"
                + request.getServerPort() + appContext + "/";
        return basePath;
    }


}
