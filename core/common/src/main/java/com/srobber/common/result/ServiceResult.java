package com.srobber.common.result;

import com.srobber.common.status.CommonStatus;
import lombok.Getter;

/**
 * 服务层处理结果
 *
 * @author chensenlai
 */
@Getter
public class ServiceResult<T>  {

	/**
	 * 操作码
	 */
	private int code;
	/**
	 * 提示信息
	 */
	private String msg = "";
	/**
	 * 业务数据
	 */
	private T data = null;
	
	public static <T> ServiceResult<T> ok() {
		ServiceResult<T> sr = new ServiceResult<T>();
		sr.code = CommonStatus.Success.getCode();
		return sr;
	}
	
	public static <T> ServiceResult<T> ok(T data) {
		ServiceResult<T> sr = new ServiceResult<T>();
		sr.code = CommonStatus.Success.getCode();
		sr.data = data;
		return sr;
	}
	
	public static <T> ServiceResult<T> fail() {
		ServiceResult<T> sr = new ServiceResult<T>();
		sr.code = CommonStatus.Fail.getCode();
		sr.msg = "服务调用异常";
		return sr;
	}
	
	public static <T> ServiceResult<T> fail(String msg) {
		ServiceResult<T> sr = new ServiceResult<T>();
		sr.code = CommonStatus.Fail.getCode();
		sr.msg = msg;
		return sr;
	}
	
	public static <T> ServiceResult<T> fail(int code) {
		ServiceResult<T> sr = new ServiceResult<T>();
		sr.code = code;
		sr.msg = "服务调用异常";
		return sr;
	}
	
	public static <T> ServiceResult<T> fail(int code, String msg) {
		ServiceResult<T> sr = new ServiceResult<T>();
		sr.code = code;
		sr.msg = msg;
		return sr;
	}
	
	public boolean isSuccess() {
		return code == CommonStatus.Success.getCode();
	}
	
	public boolean isFail() {
		return !isSuccess();
	}
}
