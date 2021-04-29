package com.srobber.rocketmq;

import com.srobber.rocketmq.delay.DelayMQProducer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

import java.util.Date;

/**
 * 支持任意延迟RocketMQ模板类
 *
 * @author chensenlai
 * 2020-10-26 上午11:03
 */
@Slf4j
public class DelayRocketMQTemplate extends RocketMQTemplate {

    @Setter
    private DelayMQProducer delayMQProducer;

    public SendResult syncSendDelay(String destination, Object payload,
                               Date sendTime) {
        Message<?> message = this.doConvert(payload, null, null);
        return syncSendDelay(destination, message, sendTime);
    }

    public SendResult syncSendDelay(String destination, Object payload,
                                    Date sendTime, int diffLowerSec, int diffUpperSec) {
        Message<?> message = this.doConvert(payload, null, null);
        return syncSendDelay(destination, message, sendTime, diffLowerSec, diffUpperSec);
    }

    public SendResult syncSendDelay(String destination, Message<?> message,
                                    Date sendTime) {
        org.apache.rocketmq.common.message.Message rocketMsg = RocketMQUtil.convertToRocketMessage(super.getObjectMapper(), super.getCharset(),
                destination, message);
        try {
            return delayMQProducer.sendDelay(rocketMsg, sendTime, 0, 0);
        } catch (Exception e) {
            throw new MessagingException(e.getMessage(), e);
        }
    }

    public SendResult syncSendDelay(String destination, Message<?> message,
                                    Date sendTime, int diffLowerSec, int diffUpperSec) {
        org.apache.rocketmq.common.message.Message rocketMsg = RocketMQUtil.convertToRocketMessage(super.getObjectMapper(), super.getCharset(),
                destination, message);
        try {
            return delayMQProducer.sendDelay(rocketMsg, sendTime, diffLowerSec, diffUpperSec);
        } catch (Exception e) {
            throw new MessagingException(e.getMessage(), e);
        }
    }
}
