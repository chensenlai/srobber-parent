package com.srobber.manager.pay.wxpay.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信小程序支付统一下单返回数据
 *
 * @author chensenlai
 */
@Data
public class WxpayUnifiedProgramDTO {
	
	private String appId;
	private String timeStamp;
	private String nonceStr;
	@JsonProperty(value = "package")
	private String packageValue;
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
