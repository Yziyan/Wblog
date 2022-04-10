package com.xhy.wblog.utils.converter;

import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// 日期格式的转换；

public class DateConverter implements Converter<String, Date> {

    // 可以有，多种形式的日期。
    private List<String> patterns;

    // 用来注入属性
    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public Date convert(String source) {

        // 遍历所有的日期格式
        for (String pat : patterns) {
            SimpleDateFormat format = new SimpleDateFormat(pat);
            try {
                return format.parse(source);
            } catch (Exception e) {
                // 可以不用打印异常信息。因为他会去匹配配置的日期格式。
                // e.printStackTrace();
            }
        }

        return null;
    }
}
