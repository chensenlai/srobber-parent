package com.srobber.rocketmq.delay;

import com.srobber.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 延迟队列级别
 * 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
 *
 * @author chensenlai
 * 2020-10-23 下午6:16
 */
@Getter
@AllArgsConstructor
public enum DelayLevelEnum implements BaseEnum {

    Level_1_1s(1, "1s", 1),
    Level_2_5s(2, "5s", 5),
    Level_3_10s(3, "10s", 10),
    Level_4_30s(4, "30s", 30),
    Level_5_1m(5, "1m", 60),
    Level_6_2m(6, "2m", 2*60),
    Level_7_3m(7, "3m", 3*60),
    Level_8_4m(8, "4m", 4*60),
    Level_9_5m(9, "5m", 5*60),
    Level_10_6m(10, "6m", 6*60),
    Level_11_7m(11, "7m", 7*60),
    Level_12_8m(12, "8m", 8*60),
    Level_13_9m(13, "9m", 9*60),
    Level_14_10m(14, "10m", 10*60),
    Level_15_20m(15, "20m", 20*60),
    Level_16_30m(16, "30m", 30*60),
    Level_17_1h(17, "1h", 60*60),
    Level_18_2h(18, "2h", 2*60*60),
    ;


    /**
     * RocketMQ broker的延迟级别
     */
    private int num;

    /**
     * RocketMQ broker的延迟名称
     */
    private String name;

    /**
     * RocketMQ broker的延迟级别对应秒数
     */
    private int sec;
}
