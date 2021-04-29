package com.srobber.common.result;

import com.srobber.common.status.CommonStatus;
import lombok.Getter;

import java.util.List;

/**
 * 批量处理结果
 *
 * @author chensenlai
 */
@Getter
public class BatchResult<T> {

	/**
	 * 响应码
	 */
	private int code;
	/**
	 * 提示信息
	 */
	private String msg;
	/**
	 * 操作记录总数
	 */
	private int total;
	/**
	 * 操作记录成功数
	 */
	private int success;
	/**
	 * 业务数据列表
	 */
	private List<T> dataList;

	public static <T> BatchResult<T> ok(int total, int success, List<T> dataList) {
		BatchResult<T> br = new BatchResult<T>();
		br.code = CommonStatus.Success.getCode();
		br.total = total;
		br.success = success;
		br.dataList = dataList;
		return br;
	}
	
	public static <T> BatchResult<T> fail() {
		BatchResult<T> br = new BatchResult<T>();
		br.code = CommonStatus.Fail.getCode();
		br.msg = "批量处理异常";
		return br;
	}
	
	public static <T> BatchResult<T> fail(String msg) {
		BatchResult<T> br = new BatchResult<T>();
		br.code = CommonStatus.Fail.getCode();
		br.msg = msg;
		return br;
	}
	
	public boolean isSuccess() {
		return code == CommonStatus.Success.getCode();
	}
	
	public boolean isFail() {
		return !isSuccess();
	}
}
