package com.srobber.rocketmq.delay;

/**
 * 任意时间延迟消息常量配置
 *
 * @author chensenlai
 * 2020-10-23 下午5:18
 */
public class DelayConstant {

    /**
     * 延迟分组生产者
     */
    public static final String DELAY_GROUP_PRODUCER = "delay_group_producer";

    /**
     * 延迟分组消费者
     */
    public static final String DELAY_GROUP_CONSUMER = "delay_group_consumer";

    /**
     * 系统占用, 任意时间延迟内部使用topic
     */
    public static final String DELAY_TOPIC = "delay_topic";

    /**
     * 原消息topic
     */
    public static final String DELAY_ORIGINAL_TOPIC = "delay_original_topic";

    /**
     * 原消息tag
     */
    public static final String DELAY_ORIGINAL_TAGS   = "delay_original_tags";

    /**
     * 原消息keys
     */
    public static final String DELAY_ORIGINAL_KEYS   = "delay_original_keys";

    /**
     * 实际发送时间
     */
    public static final String DELAY_ORIGINAL_SEND_TIME = "delay_original_send_time";

    /**
     * 实际发送时间可容忍误差下限值
     * 限制是非正数, 表示允许小于误差值多少秒
     */
    public static final String DELAY_ORIGINAL_DIFF_LOWER_SEC = "delay_original_diff_lower_sec";

    /**
     * 实际发送时间可容忍误差上限值
     * 限制是非负数, 表示允许小于误差值多少秒
     */
    public static final String DELAY_ORIGINAL_DIFF_UPPER_SEC = "delay_original_diff_upper_sec";

}
