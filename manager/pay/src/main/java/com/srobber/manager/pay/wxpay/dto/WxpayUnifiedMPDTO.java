package com.srobber.manager.pay.wxpay.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信wap(H5网页)统一下单返回数据
 *
 * @author chensenlai
 */
@Data
public class WxpayUnifiedMPDTO {

	private String appId;
	private String timeStamp;
	private String nonceStr;
	/**
	 * 统一下单接口返回的prepay_id参数值，提交格式如：prepay_id=***
	 */
	@JsonProperty(value = "package")
	private String packageValue;
	/**
	 * 此处需与统一下单的签名类型一致
	 */
	private String signType;
	private String paySign;
	
	public Map<String, String> asMap() {
		Map<String, String> map = new HashMap<>(16);
		map.put("appId", this.appId);
		map.put("timeStamp", this.timeStamp);
		map.put("nonceStr", this.nonceStr);
		map.put("package", this.packageValue);
		map.put("signType", this.signType);
		return map;
	}
}
