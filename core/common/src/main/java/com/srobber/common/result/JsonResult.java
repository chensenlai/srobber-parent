package com.srobber.common.result;

import com.srobber.common.status.CommonStatus;
import com.srobber.common.status.BaseStatus;
import lombok.Data;

/**
 * 控制层返回结果
 *
 * @author chensenlai
 */
@Data
public class JsonResult<T> {

	private int code;
	private String msg;
	private T data;

	/**
	 * 只返回处理成功，不带数据体。
	 * @param <T> 业务泛型
	 * @return json结果
	 */
	public static <T> JsonResult<T> ok() {
		CommonStatus status = CommonStatus.Success;
		JsonResult<T> m = new JsonResult<>();
		m.setCode(status.getCode());
		m.setMsg("");
		m.setData(null);
		return m;
	}

	/**
	 * 返回成功，并需要返回的数据体
	 * @param data 业务数据
	 * @param <T> 业务泛型
	 * @return json结果
	 */
	public static <T> JsonResult<T> ok(T data) {
		CommonStatus status = CommonStatus.Success;
		JsonResult<T> m = new JsonResult<>();
		m.setCode(status.getCode());
		m.setMsg("");
		m.setData(data);
		return m;
	}

	/**
	 * 只返回失败状态和消息，消息可自己指定。
	 * @param msg 结果提示
	 * @param <T> 业务数据泛型
	 * @return json结果
	 */
	public static <T> JsonResult<T> fail(String msg) {
		CommonStatus status = CommonStatus.Fail;
		JsonResult<T> m = new JsonResult<>();
		m.setCode(status.getCode());
		m.setMsg(msg == null ? status.getMsg() : msg);
		return m;
	}

	/**
	 * 只返回失败状态和消息。
	 * @param <T> 业务数据泛型
	 * @return json结果
	 */
	public static <T> JsonResult<T> fail() {
		CommonStatus status = CommonStatus.Fail;
		JsonResult<T> m = new JsonResult<>();
		m.setCode(status.getCode());
		m.setMsg(status.getMsg());
		return m;
	}

	/**
	 * 只返回指定的状态码和消息
	 * @param status 结果状态
	 * @param <T> 业务数据泛型
	 * @return json结果
	 */
	public static <T> JsonResult<T> resp(BaseStatus status) {
		JsonResult<T> m = new JsonResult<>();
		m.setCode(status.getCode());
		m.setMsg(status.getMsg());
		return m;
	}

	/**
	 * 只返回指定的状态码和消息， 消息可自指定。
	 * @param status 结果状态
	 * @param msg 结果提示
	 * @param <T> 业务数据泛型
	 * @return json结果
	 */
	public static <T> JsonResult<T> resp(BaseStatus status, String msg) {
		JsonResult<T> m = new JsonResult<>();
		m.setCode(status.getCode());
		m.setMsg(msg);
		return m;
	}
}
