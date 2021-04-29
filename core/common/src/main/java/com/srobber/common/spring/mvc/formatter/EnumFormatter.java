package com.srobber.common.spring.mvc.formatter;

import com.srobber.common.enums.BaseEnum;
import com.srobber.common.util.BaseEnumUtil;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * SpringMVC参数转化使用,客户端num数值请求参数转成BaseEnum类型
 * {@link org.springframework.web.bind.annotation.RequestParam}
 * {@link org.springframework.web.bind.annotation.RequestHeader}
 * {@link org.springframework.web.bind.annotation.CookieValue}
 * {@link org.springframework.web.bind.annotation.PathVariable}
 *
 * @author chensenlai
 */
public class EnumFormatter<T extends BaseEnum> implements Formatter<T> {

    /**
     * 具体枚举类型, BaseEnum子类
     */
    private Class<T> enumClass;

    public EnumFormatter(Class<T> clazz) {
        this.enumClass = clazz;
    }

    @Override
    public T parse(String text, Locale locale) throws ParseException {
        if(text==null || text.isEmpty()) {
            return null;
        }

        int num = Integer.parseInt(text);
        return BaseEnumUtil.of(enumClass, num);
    }

    @Override
    public String print(T object, Locale locale) {
        return object.toString();
    }
}