package com.srobber.rocketmq.delay;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.Date;

/**
 * 任意时间延迟生产者
 * FIXME 通过计算延迟时间, 选择最靠近定时法送点, 接收MQ消息后判断是否到达截止时间, 是则调用业务处理方法, 否则重新计算延迟时间发送MQ
 *       适合吐出量不高,时间精度要求不高
 *       待优化, 比如使用HashTimeWheel
 *
 * @author chensenlai
 * 2020-10-23 下午5:08
 */
@Slf4j
public class DelayMQProducer {

    private String namesrvAddr;
    private DefaultMQProducer defaultMQProducer;

    public DelayMQProducer(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void init() {
        defaultMQProducer = new DefaultMQProducer(DelayConstant.DELAY_GROUP_PRODUCER);
        defaultMQProducer.setNamesrvAddr(namesrvAddr);
        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            log.error("RocketMQ init error.", e);
            System.exit(-1);
        }
    }

    /**
     * 发送延迟消息
     * @param message 消息
     * @param sendTime 实际期望发送时间
     * @param diffLowerSec 误差下限(秒) 非正数
     * @param diffUpperSec 误差上限(秒) 非负数
     * @return 发送结果
     */
    public SendResult sendDelay(Message message,
                                Date sendTime, int diffLowerSec, int diffUpperSec) throws MQClientException, RemotingException, MQBrokerException, InterruptedException{
        //检查消息是否合法
        if(message == null) {
            throw new NullPointerException("message null");
        }
        DelayMessageUtil.checkUseDelayKeyword(message);
        if(sendTime == null) {
            throw new NullPointerException("sendTime null");
        }
        if(diffLowerSec > 0) {
            throw new IllegalArgumentException("diffLowerSec can not >0");
        }
        if(diffUpperSec < 0) {
            throw new IllegalArgumentException("diffUpperSec can not <0");
        }

        //选择正确的发送级别, 如果是延迟发送, 重新构造发送消息
        DelayLevelChooseResult chooseResult = DelayMessageUtil.chooseRightLevel(sendTime, diffLowerSec, diffUpperSec);
        if(chooseResult.getSendType() == DelayLevelChooseResult.SendType.DirectSend) {
            ;
        }
        if(chooseResult.getSendType() == DelayLevelChooseResult.SendType.SetDelayTimeLevelSend) {
            message.setDelayTimeLevel(chooseResult.getLevel().getNum());
        }
        if(chooseResult.getSendType() == DelayLevelChooseResult.SendType.FillDelayPropertiesSend) {
            DelayMessageUtil.fillDelayProperty(message, sendTime, diffLowerSec, diffUpperSec, chooseResult.getLevel());
        }
        return defaultMQProducer.send(message);
    }

}
