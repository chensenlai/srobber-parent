package com.srobber.manager.pay.wxpay.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信APP统一下单返回数据
 *
 * @author chensenlai
 */
@Data
public class WxpayUnifiedAppDTO {

	private String appid;
	private String partnerid;
	private String prepayid;
	@JsonProperty(value = "package")
	private String packageValue;
	private String noncestr;
	private String timestamp;
	private String sign;
	
	public Map<String, String> asMap() {
		Map<String, String> map = new HashMap<>(16);
		map.put("appid", this.appid);
		map.put("partnerid", this.partnerid);
		map.put("prepayid", this.prepayid);
		map.put("package", this.packageValue);
		map.put("noncestr", this.noncestr);
		map.put("timestamp", this.timestamp);
		return map;
	}
}
