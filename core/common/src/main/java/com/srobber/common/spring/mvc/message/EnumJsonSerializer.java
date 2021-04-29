package com.srobber.common.spring.mvc.message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.srobber.common.enums.BaseEnum;

import java.io.IOException;

/**
 * BaseEnum子类序列化(jackson)
 * BaseEnum类型返回给客户端是num数值类型
 * 见{@link org.springframework.web.bind.annotation.ResponseBody}
 *
 * @author chensenlai
 */
public class EnumJsonSerializer<T extends BaseEnum> extends JsonSerializer<T> {

    /**
     * 具体枚举类型, BaseEnum子类
     */
    private Class<T> enumClass;

    public EnumJsonSerializer(Class<T> clazz) {
        this.enumClass = clazz;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if(value == null) {
            gen.writeNull();
            return;
        }
        gen.writeNumber(value.getNum());
    }
}