package com.srobber.rocketmq.util;

import com.srobber.common.util.StringUtil;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Spring RocketMQTemplate工具类
 *
 * @author chensenlai
 * 2020-10-27 下午2:32
 */
public class RocketMQTemplateUtil {

    /**
     * 转化成SpringMQ destination
     * @param topic 主题
     * @param tag 标签
     * @return destination
     */
    public static String covertDestination(String topic, @Nullable String tag) {
        if(StringUtil.isBlank(tag)) {
            return topic;
        }
        return topic+":"+tag;
    }

    /**
     * 转化成SpringMQ Message
     * @param payload RocketMQ 消息内容
     * @param keys RocketMQ keys
     * @param <T> 泛型
     * @return Message
     */
    public static <T> Message<T> covertMessage(T payload, @Nullable Object keys) {
        Message<T> message = new Message<T>() {
            @Override
            public T getPayload() {
                return payload;
            }

            @Override
            public MessageHeaders getHeaders() {
                if(Objects.isNull(keys)) {
                    return null;
                }
                Map<String, Object> headers = new HashMap<>(4);
                headers.put(RocketMQHeaders.KEYS, keys);
                return new MessageHeaders(headers);
            }
        };
        return message;
    }
}
