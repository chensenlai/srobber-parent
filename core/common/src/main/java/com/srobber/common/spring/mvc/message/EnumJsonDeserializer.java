package com.srobber.common.spring.mvc.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.srobber.common.enums.BaseEnum;
import com.srobber.common.util.BaseEnumUtil;

import java.io.IOException;

/**
 * BaseEnum子类返序列化(jackson)
 * 客户端传入数值num转成BaseEnum类型
 * 见{@link org.springframework.web.bind.annotation.RequestBody}
 *
 * @author chensenlai
 */
public class EnumJsonDeserializer<T extends BaseEnum> extends JsonDeserializer<T> {

    /**
     * 具体枚举类型, BaseEnum子类
     */
    private Class<T> enumClass;

    public EnumJsonDeserializer(Class<T> clazz) {
        this.enumClass = clazz;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String numStr = p.getText();
        if(numStr==null || numStr.isEmpty()) {
            return null;
        }
        return BaseEnumUtil.of(enumClass, Integer.parseInt(numStr));
    }
}