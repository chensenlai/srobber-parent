package com.srobber.rocketmq.delay;

import com.srobber.common.util.DateFormatUtil;
import com.srobber.common.util.DateUtil;
import com.srobber.common.util.StringUtil;
import org.apache.rocketmq.common.message.Message;

import java.util.Date;
import java.util.Objects;

/**
 * 延迟消息工具类
 *
 * @author chensenlai
 * 2020-10-23 下午5:11
 */
public class DelayMessageUtil extends Message {

    /**
     * 检查是否使用延迟系统占用属性
     * @param message 消息
     */
    public static void checkUseDelayKeyword(Message message) {
        if(message.getDelayTimeLevel() > 0) {
            throw new IllegalArgumentException("delayTimeLevel can not be set");
        }
        if(Objects.equals(message.getTopic(), DelayConstant.DELAY_TOPIC)) {
            throw new IllegalArgumentException("topic "+DelayConstant.DELAY_TOPIC+" is used by delay system");
        }
        String[] properties = new String[]{
                DelayConstant.DELAY_ORIGINAL_TOPIC,
                DelayConstant.DELAY_ORIGINAL_TAGS,
                DelayConstant.DELAY_ORIGINAL_KEYS,
                DelayConstant.DELAY_ORIGINAL_SEND_TIME,
                DelayConstant.DELAY_ORIGINAL_DIFF_LOWER_SEC,
                DelayConstant.DELAY_ORIGINAL_DIFF_UPPER_SEC
        };
        for(String property : properties) {
            if(StringUtil.isNotBlank(message.getUserProperty(property))) {
                throw new IllegalArgumentException("property "+property+" is used by delay system");
            }
        }
    }

    /**
     * 填充延迟消息属性
     * @param message 原始消息
     * @param sendTime  发送时间
     * @param diffLowerSec 误差下限(秒) 非正数
     * @param diffUpperSec 误差上限(秒) 非负数
     * @param level 延迟级别
     * @return 延迟消息
     */
    public static void fillDelayProperty(Message message,
                                            Date sendTime, int diffLowerSec, int diffUpperSec,
                                            DelayLevelEnum level) {
        //保存原来消息topic, tags, keys, sendTime, diffLowerSec, diffUpperSec等
        String topic = message.getTopic();
        if(StringUtil.isNotBlank(topic)) {
            message.putUserProperty(DelayConstant.DELAY_ORIGINAL_TOPIC, topic);
        }

        String tags = message.getTags();
        if(StringUtil.isNotBlank(tags)) {
            message.putUserProperty(DelayConstant.DELAY_ORIGINAL_TAGS, tags);
        }

        String keys = message.getKeys();
        if(StringUtil.isNotBlank(keys)) {
            message.putUserProperty(DelayConstant.DELAY_ORIGINAL_KEYS, keys);
        }

        message.putUserProperty(DelayConstant.DELAY_ORIGINAL_SEND_TIME, DateFormatUtil.format(sendTime, DateFormatUtil.Pattern.YYYY_MM_DD_HH_MM_SS));
        message.putUserProperty(DelayConstant.DELAY_ORIGINAL_DIFF_LOWER_SEC, String.valueOf(diffLowerSec));
        message.putUserProperty(DelayConstant.DELAY_ORIGINAL_DIFF_UPPER_SEC, String.valueOf(diffUpperSec));

        //更新消息topic, 发送到系统指定延迟队列处理
        message.setTopic(DelayConstant.DELAY_TOPIC);
        message.setDelayTimeLevel(level.getNum());
    }

    /**
     * 擦除延迟属性
     *
     * @param message 延迟消息
     * @return 原始消息
     */
    public static void clearDelayProperty(Message message) {
        String topic = message.getUserProperty(DelayConstant.DELAY_ORIGINAL_TOPIC);
        String tags = message.getUserProperty(DelayConstant.DELAY_ORIGINAL_TAGS);
        String keys = message.getUserProperty(DelayConstant.DELAY_ORIGINAL_KEYS);

        message.setTopic(topic);
        message.setTags(tags);
        message.setKeys(keys);
        message.setDelayTimeLevel(0);
    }

    /**
     * 1.如果发送时间小于当前时间,则返回空,表示立即发送
     * 2.如果发送时间间隔秒数在小于有效范围最小值, 则查找最大那个级别
     * 3.如果发送时间间隔秒数在有效范围内,则查找误差秒数最小那个级别
     *
     * levelList 按升序排列, 靠前秒数少, 靠后秒数多
     *
     * rightSec = 最正确的发送延迟秒数
     * minRightSec = 正确发送延迟秒数下限
     * maxRightSec = 正确发送延迟秒数上限
     *
     * @param sendTime 实际期望发送时间
     * @param diffLowerSec 允许误差秒数下限(非正数)
     * @param diffUpperSec 允许误差秒数上限(正数)
     * @return 选择结果
     */
    public static DelayLevelChooseResult chooseRightLevel(Date sendTime, int diffLowerSec, int diffUpperSec) {
        //1.如果发送时间小于当前时间,则返回空,表示立即发送
        int rightSec = DateUtil.secondsCompare(DateUtil.now(), sendTime);
        if(rightSec < 1) {
            return new DelayLevelChooseResult(DelayLevelChooseResult.SendType.DirectSend, null);
        }

        int minRightSec = rightSec+diffLowerSec;
        int maxRightSec = rightSec+diffUpperSec;

        DelayLevelEnum chooseLevel = null;
        int chooseDiffSec = -1;
        for(DelayLevelEnum level : DelayLevelEnum.values()) {
            int sec = level.getSec();
            if(sec < minRightSec) {
                //在小于有效范围内, 查找最大那个
                chooseLevel = level;
            } else if(sec>=minRightSec && sec<=maxRightSec) {
                //在有效范围内, 查找和间隔时间最小的发送出去
                int diffSec = Math.abs(rightSec-sec);
                if(chooseDiffSec == -1
                    || diffSec<chooseDiffSec){
                    chooseLevel = level;
                    chooseDiffSec = diffSec;
                }
            } else {
                break;
            }
        }

        if(chooseLevel.getSec()>=minRightSec && chooseLevel.getSec()<=maxRightSec) {
            return new DelayLevelChooseResult(DelayLevelChooseResult.SendType.SetDelayTimeLevelSend, chooseLevel);
        }
        return new DelayLevelChooseResult(DelayLevelChooseResult.SendType.FillDelayPropertiesSend, chooseLevel);
    }

    public static void main(String[] args) {
        //测试需要来回发送次数
        for(int i=0; i<24*60*60; i++) {
            Date now = DateUtil.now();
            Date sendTime = DateUtil.plusSeconds(now, i);
            int time = 0;
            DelayLevelChooseResult chooseResult = null;
            do {
                time ++;
                chooseResult = chooseRightLevel(sendTime, -60, 60);
                if(chooseResult.getSendType() == DelayLevelChooseResult.SendType.FillDelayPropertiesSend) {
                    sendTime = DateUtil.plusSeconds(sendTime, -chooseResult.getLevel().getSec());
                }
            } while (chooseResult.getSendType() == DelayLevelChooseResult.SendType.FillDelayPropertiesSend);
            System.out.println(i+":"+time);
        }
    }
}
