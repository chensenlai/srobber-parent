package com.srobber.manager.pay.wxpay;

import javax.xml.bind.annotation.XmlRootElement;

import com.srobber.common.util.JaxbUtil;

@XmlRootElement(name="xml")
public class WxReturn {
	
	public static final String SUCCESS = JaxbUtil.toXml(new WxReturn("SUCCESS", ""));
	public static final String FAIL = JaxbUtil.toXml(new WxReturn("FAIL", ""));

	/**
	 * 返回状态码
	 */
	private String return_code;
	/**
	 * 返回信息
	 */
	private String return_msg;
	
	public WxReturn(){}
	
	public WxReturn(String code, String msg) {
		this.return_code = code;
		this.return_msg = msg;
	}
	
	public String getResult_msg() {
		return return_msg;
	}
	public void setResult_msg(String return_msg) {
		this.return_msg = return_msg;
	}
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
}
