package com.srobber.rocketmq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srobber.rocketmq.DelayRocketMQTemplate;
import com.srobber.rocketmq.delay.DelayMQProducer;
import com.srobber.rocketmq.delay.DelayMQPushConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.config.RocketMQConfigUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ延迟消息配置
 *
 * @author chensenlai
 * 2020-10-24 下午3:56
 */
@Slf4j
@AutoConfigureBefore(RocketMQAutoConfiguration.class)
@Configuration
public class DelayRocketMQConfig {

    @Bean(name = RocketMQConfigUtils.ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME, destroyMethod = "destroy")
    public DelayRocketMQTemplate rocketMQTemplate(DefaultMQProducer mqProducer, ObjectMapper rocketMQMessageObjectMapper,
                                                  DelayMQProducer delayMQProducer) {
        DelayRocketMQTemplate rocketMQTemplate = new DelayRocketMQTemplate();
        rocketMQTemplate.setProducer(mqProducer);
        rocketMQTemplate.setObjectMapper(rocketMQMessageObjectMapper);
        rocketMQTemplate.setDelayMQProducer(delayMQProducer);
        return rocketMQTemplate;
    }

    @Bean
    public DelayMQProducer delayMQProducer(RocketMQProperties rocketMQProperties) {
        DelayMQProducer delayMQProducer = new DelayMQProducer(rocketMQProperties.getNameServer());
        delayMQProducer.init();
        return delayMQProducer;
    }

    @Bean
    public DelayMQPushConsumer delayMQPushConsumer(RocketMQProperties rocketMQProperties, DelayMQProducer delayMQProducer) {
        DelayMQPushConsumer delayMQPushConsumer = new DelayMQPushConsumer(rocketMQProperties.getNameServer(), delayMQProducer);
        try {
            delayMQPushConsumer.init();
        } catch (MQClientException e) {
            log.warn("delay consumer init error", e);
        }
        return delayMQPushConsumer;
    }
}
