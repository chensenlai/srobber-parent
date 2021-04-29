package com.srobber.common.spring.mvc.formatter;

import com.srobber.common.util.StringUtil;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * Double和字符串互转
 *
 * @author chensenlai
 */
public class DoubleFormatter implements Formatter<Double> {

    @Override
    public Double parse(String text, Locale locale) throws ParseException {
        if(StringUtil.isBlank(text)) {
            return null;
        }
        return Double.parseDouble(text);
    }

    @Override
    public String print(Double object, Locale locale) {
        if(object == null) {
            return null;
        }
        return object.toString();
    }
}
