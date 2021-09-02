package com.srobber.common.spring.mvc.formatter;

import com.srobber.common.util.StringUtil;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;

/**
 * Long与字符串转化
 *
 * @author chensenlai
 */
public class LongFormatter implements Formatter<Long> {

    @Override
    public Long parse(String text, Locale locale) throws ParseException {
        if(StringUtil.isBlank(text)) {
            return null;
        }
        return Long.parseLong(text);
    }

    @Override
    public String print(Long object, Locale locale) {
        return object.toString();
    }
}
