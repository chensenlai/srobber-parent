package com.srobber.rocketmq.delay;

import com.srobber.common.util.DateFormatUtil;
import com.srobber.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 任意时间延迟消息消费者
 *
 * 根据原消息消费时间, 重新分发消息.
 * 1. 如果消息消费时间满足真正消费时间,则还原消息并且直接发送到原始主题队列.
 * 2. 如果消息消费时间不满真正消费时间,则重新计算发送给延迟队列处理.
 *
 * @author chensenlai
 * 2020-10-23 下午5:28
 */
@Slf4j
public class DelayMQPushConsumer implements MessageListenerConcurrently {

    private String namesrvAddr;
    private DelayMQProducer delayMQProducer;
    private DefaultMQPushConsumer defaultMQPushConsumer;

    public DelayMQPushConsumer(String namesrvAddr, DelayMQProducer delayMQProducer) {
        this.namesrvAddr = namesrvAddr;
        this.delayMQProducer = delayMQProducer;
    }

    public void init() throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(DelayConstant.DELAY_GROUP_CONSUMER);
        defaultMQPushConsumer.setNamesrvAddr(namesrvAddr);
        defaultMQPushConsumer.subscribe(DelayConstant.DELAY_TOPIC, "*");
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        defaultMQPushConsumer.setMessageListener(this);
        try {
            defaultMQPushConsumer.start();
        } catch (MQClientException e) {
            log.error("RocketMQ init error.", e);
            System.exit(-1);
        }
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            for (MessageExt msg : msgs) {
                Map<String, String> properties = msg.getProperties();

                String topic = properties.get(DelayConstant.DELAY_ORIGINAL_TOPIC);
                String tags = properties.get(DelayConstant.DELAY_ORIGINAL_TAGS);
                String keys = properties.get(DelayConstant.DELAY_ORIGINAL_KEYS);

                Date sendTime = DateFormatUtil.parse(properties.get(DelayConstant.DELAY_ORIGINAL_SEND_TIME), DateFormatUtil.Pattern.YYYY_MM_DD_HH_MM_SS);
                int diffLowerSec = Integer.parseInt(properties.get(DelayConstant.DELAY_ORIGINAL_DIFF_LOWER_SEC));
                int diffUpperSec = Integer.parseInt(properties.get(DelayConstant.DELAY_ORIGINAL_DIFF_UPPER_SEC));

                Message message = new Message();
                message.setTopic(topic);
                if (StringUtil.isNotBlank(tags)) {
                    message.setTags(tags);
                }
                if (StringUtil.isNotBlank(keys)) {
                    message.setKeys(keys);
                }
                message.setBody(msg.getBody());
                // send消息方法只要不抛异常，就代表发送成功。
                // 这里成功指的是broker接收成功, 不代表消息落盘持久保存
                // 具体见: https://github.com/apache/rocketmq/blob/master/docs/cn/best_practice.md
                // SEND_OK 消息发送成功。要注意的是消息发送成功也不意味着它是可靠的。要确保不会丢失任何消息，还应启用同步Master服务器或同步刷盘，即SYNC_MASTER或SYNC_FLUSH。
                // FLUSH_DISK_TIMEOUT
                // FLUSH_SLAVE_TIMEOUT
                // SLAVE_NOT_AVAILABLE
                SendResult sendResult = delayMQProducer.sendDelay(message, sendTime, diffLowerSec, diffUpperSec);
                if(sendResult.getSendStatus() != SendStatus.SEND_OK) {
                    log.warn("RocketMQ message send not ok: topic={}, tags={}, keys={}, sendTime={}, diffLowerSec={}, diffUpperSec={}",
                            topic, tags, keys, sendTime, diffLowerSec, diffUpperSec);
                }
            }
        } catch (Exception e) {
            log.error("RocketMQ message send error.", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
