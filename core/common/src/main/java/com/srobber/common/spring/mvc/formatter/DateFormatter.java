package com.srobber.common.spring.mvc.formatter;

import com.srobber.common.util.DateFormatUtil;
import com.srobber.common.util.StringUtil;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * 日志与字符串转化
 * @author chensenlai
 * 2020-10-21 下午5:45
 */
public class DateFormatter implements Formatter<Date> {

    @Override
    public Date parse(String text, Locale locale) throws ParseException {
        if(StringUtil.isBlank(text)) {
            return null;
        }
        return DateFormatUtil.parse(text, DateFormatUtil.Pattern.YYYY_MM_DD_HH_MM_SS);
    }

    @Override
    public String print(Date object, Locale locale) {
        if(object == null) {
            return null;
        }
        return DateFormatUtil.format(object, DateFormatUtil.Pattern.YYYY_MM_DD_HH_MM_SS);
    }
}
