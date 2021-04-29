package com.srobber.rocketmq.delay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 延迟级别选择结果
 * @author chensenlai
 * 2020-10-24 下午2:06
 */
@Getter
@AllArgsConstructor
public class DelayLevelChooseResult {

    private SendType sendType;
    private DelayLevelEnum level;

    enum SendType {
        /**
         * 直接发送
         */
        DirectSend,
        /**
         * 设置延迟级别再发送
         */
        SetDelayTimeLevelSend,
        /**
         * 填充延迟属性再发送
         */
        FillDelayPropertiesSend,
        ;
    }

    @Override
    public String toString() {
        return "DelayLevelChooseResult{" +
                "sendType=" + sendType +
                ", level=" + level +
                '}';
    }
}
