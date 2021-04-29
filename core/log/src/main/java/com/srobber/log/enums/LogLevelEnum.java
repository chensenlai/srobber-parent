package com.srobber.log.enums;

import com.srobber.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日志级别
 *
 * @author chensenlai
 */
@Getter
@AllArgsConstructor
public enum LogLevelEnum implements BaseEnum {

	/**
	 * 正常但重要的消息
	 */
	Info(1, "正常(重要信息)"),
	/**
	 * 不正常但是影响小
	 */
	Warn(2, "告警(影响比较小，不影响正常业务，需要通知)"),
	/**
	 * 不正常但是影响大
	 */
	Error(3, "错误(影响比较大，需要通知)")
	;

	private int num;
	private String name;
}
