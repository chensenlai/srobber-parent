package com.srobber.common.spring.mvc.formatter;

import com.srobber.common.util.StringUtil;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * Float和字符串互转
 *
 * @author chensenlai
 */
public class FloatFormatter implements Formatter<Float> {

    @Override
    public Float parse(String text, Locale locale) throws ParseException {
        if(StringUtil.isBlank(text)) {
            return null;
        }
        return Float.parseFloat(text);
    }

    @Override
    public String print(Float object, Locale locale) {
        return object.toString();
    }
}
