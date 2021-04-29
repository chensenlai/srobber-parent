package com.srobber.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srobber.common.exeption.WrapException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * JSON工具类
 * 序列化带上类型
 *
 * @author chensenlai
 * 2020-10-29 下午12:09
 */
@Slf4j
public class JsonTypeAwareUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        initMapper(mapper);
    }

    public static void initMapper(ObjectMapper mapper) {
        JsonUtil.initMapper(mapper);
        //序列化所有字段
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //序列化带上类型
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }

    /**
     * 转成对象
     * 对象类型可感知, 因为json序列化带上实例类型, 故能运行时反序列化成原来对象
     * @param jsonStr json字符串
     * @return 对象8
     */
    public static Object toObject(String jsonStr) {
        try {
            return mapper.readValue(jsonStr, Object.class);
        } catch (IOException e) {
            log.error("toObject error. {}", e.getMessage());
            throw new WrapException(e);
        }
    }

    /**
     * 对象转字符串
     * @param obj 对象
     * @return json字符串
     */
    public static String toStr(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("toStr error. {}", e.getMessage());
            throw new WrapException(e);
        }
    }

}
