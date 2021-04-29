package com.srobber.manager.pay.wxpay;

import com.srobber.common.exeption.BusinessException;
import com.srobber.common.util.JsonUtil;
import com.srobber.common.util.StringUtil;
import com.srobber.manager.pay.WxpayManager;
import com.srobber.manager.pay.wxpay.dto.*;
import com.srobber.manager.pay.wxpay.param.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import wxsdk.*;
import wxsdk.WXPayConstants.SignType;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付客户端
 *
 * @author chensenlai
 */
@Slf4j
@Data
public class WxpayManagerImpl implements WxpayManager {

	/**
	 * 微信支付商户号 PartnerID 通过微信支付商户资料审核后邮件发送
	 * https://pay.weixin.qq.com
	 */
	private String mchId;
	/**
	 * 管理中心-移动应用
	 * https://open.weixin.qq.com/
	 */
	private String appId;
	/**
	 * 帐户设置-安全设置-API安全-API密钥-设置API密钥
	 * https://pay.weixin.qq.com
	 */
	private String key;
	/**
	 * 微信支付结果回调通知地址
	 */
	private String notifyUrl;
	/**
	 * MD5/HMACSHA256
	 */
	private String signType;

	/**
	 * 微信提供请求支付SDK
	 * 线程安全
	 */
	private WXPay sdk;

	public void init() {
		try {
			sdk = new WXPay(new WXPayConfig(){
						@Override
						public String getAppID() {
							return WxpayManagerImpl.this.appId;
						}
						@Override
						public String getMchID() {
							return WxpayManagerImpl.this.mchId;
						}
						@Override
						public String getKey() {
							return WxpayManagerImpl.this.key;
						}
						@Override
						public InputStream getCertStream() {
							ClassPathResource resource = new ClassPathResource("/apiclient_cert.p12");
							InputStream is = null;
							try {
								is = resource.getInputStream();
							} catch (IOException e) {
								log.error("WXPay resource {} not found", "apiclient_cert.p12");
							}
							return is;
						}
						@Override
						public IWXPayDomain getWXPayDomain() {
							return new IWXPayDomain(){
								@Override
								public void report(String domain,
										long elapsedTimeMillis, Exception ex) {
									if(elapsedTimeMillis > 2000) {
										log.warn("WXPay-domain {} elapsedTimeMillis {}", domain, elapsedTimeMillis, ex);
									}
								}

								@Override
								public DomainInfo getDomain(WXPayConfig config) {
									return new DomainInfo(WXPayConstants.DOMAIN_API, true);
								}
							};
						}
					}, 
					this.notifyUrl, 
					false, 
					false, 
					asSignType(this.signType));
		} catch (Exception e) {
			log.error("WXPay sdk init error.", e);
			System.exit(-1);
		}
	}
	
	private static SignType asSignType(String signType) {
		if("MD5".equals(signType)) {
			return SignType.MD5;
		} else if("HMACSHA256".equals(signType)) {
			return SignType.HMACSHA256;
		}
		return null;
	}
	
	@Override
	public WxpayUnifiedAppDTO appUnified(WxpayAppPayParam appPayParam) {
		String outTradeNo = appPayParam.getOutTradeNo();
		int totalFee = appPayParam.getTotalFee();
		String body = appPayParam.getBody();
		String spbillClientIp = appPayParam.getSpbillClientIp();
		Map<String, String> attach = appPayParam.getAttach();
		
		Map<String, String> respData = null;
		try {
			Map<String, String> reqData = new HashMap<String, String>();
			reqData.put("body", body);
			reqData.put("out_trade_no", outTradeNo);
			reqData.put("spbill_create_ip", spbillClientIp);
			reqData.put("total_fee", String.valueOf(totalFee));
			reqData.put("trade_type", "APP");
			if(attach != null) {
				reqData.put("attach", URLEncoder.encode(JsonUtil.toStr(attach), "UTF-8"));
			}
			respData = sdk.unifiedOrder(reqData);
		} catch (Exception e) {
			log.error("WXPay-{} appUnified error", outTradeNo, e);
			throw new BusinessException("支付请求异常");
		}
		String returnCode = respData.get("return_code");
		String returnMsg = respData.get("return_msg");
		if(!WXPayConstants.SUCCESS.equals(returnCode)) {
			log.error("WXPay-{} appUnified error, return_code:{}  return_msg:{}", 
					outTradeNo, returnCode, returnMsg);
			throw new BusinessException("支付请求异常，"+returnCode+":"+returnMsg);
		}
		String resultCode = respData.get("result_code");
		String errCode = respData.get("err_code");
		String errCodeDes = respData.get("err_code_des");
		if(!WXPayConstants.SUCCESS.equals(resultCode)) {
			log.error("WXPay-{} appUnified error,  err_code:{}  err_code_des:{}", 
					outTradeNo, errCode, errCodeDes);
			throw new BusinessException("支付请求异常，"+errCode+":"+errCodeDes);
		}
		try {
			if(!WXPayUtil.isSignatureValid(respData, this.key, asSignType(this.signType))) {
				log.warn("WXPay-{} appUnified sign error", outTradeNo);
				throw new BusinessException("签名验证失败");
			}
		} catch (Exception e) {
			log.error("WXPay-{} appUnified sign error", outTradeNo, e);
			throw new BusinessException("签名验证失败");
		}
		String prepayId = respData.get("prepay_id");
		String nonceStr = respData.get("nonce_str");
		
		WxpayUnifiedAppDTO appPayUnifiedDTO = new WxpayUnifiedAppDTO();
		appPayUnifiedDTO.setAppid(this.appId);
		appPayUnifiedDTO.setPartnerid(this.mchId);
		appPayUnifiedDTO.setPrepayid(prepayId);
		appPayUnifiedDTO.setPackageValue("Sign=WXPay");
		appPayUnifiedDTO.setNoncestr(nonceStr);
		appPayUnifiedDTO.setTimestamp(String.valueOf((int)(System.currentTimeMillis() /1000L)));
		String sign = "";
		try {
			sign = WXPayUtil.generateSignature(appPayUnifiedDTO.asMap(), this.key, asSignType(this.signType));
		} catch (Exception e) {
			throw new BusinessException("支付数据签名失败");
		}
		appPayUnifiedDTO.setSign(sign);
		return appPayUnifiedDTO;
	}
	
	@Override
	public WxpayUnifiedMPDTO mpUnified(WxpayMpPayParam mpPayParam) {
		String outTradeNo = mpPayParam.getOutTradeNo();
		int totalFee = mpPayParam.getTotalFee();
		String body = mpPayParam.getBody();
		String spbillClientIp = mpPayParam.getSpbillClientIp();
		String openid = mpPayParam.getOpenid();
		Map<String, String> attach = mpPayParam.getAttach();
		
		Map<String, String> respData = null;
		try {
			Map<String, String> reqData = new HashMap<String, String>();
			reqData.put("body", body);
			reqData.put("out_trade_no", outTradeNo);
			reqData.put("spbill_create_ip", spbillClientIp);
			reqData.put("total_fee", String.valueOf(totalFee));
			reqData.put("trade_type", "JSAPI");
			reqData.put("openid", openid);
			if(attach != null) {
				reqData.put("attach", URLEncoder.encode(JsonUtil.toStr(attach), "UTF-8"));
			}
			respData = sdk.unifiedOrder(reqData);
		} catch (Exception e) {
			log.error("WXPay-{} mpUnified error", outTradeNo, e);
			throw new BusinessException("支付请求异常");
		}
		String returnCode = respData.get("return_code");
		String returnMsg = respData.get("return_msg");
		if(!WXPayConstants.SUCCESS.equals(returnCode)) {
			log.error("WXPay-{} mpUnified error, return_code:{}  return_msg:{}", 
					outTradeNo, returnCode, returnMsg);
			throw new BusinessException("支付请求异常，"+returnCode+":"+returnMsg);
		}
		String resultCode = respData.get("result_code");
		String errCode = respData.get("err_code");
		String errCodeDes = respData.get("err_code_des");
		if(!WXPayConstants.SUCCESS.equals(resultCode)) {
			log.error("WXPay-{} mpUnified error,  err_code:{}  err_code_des:{}", 
					outTradeNo, errCode, errCodeDes);
			throw new BusinessException("支付请求异常，"+errCode+":"+errCodeDes);
		}
		try {
			if(!WXPayUtil.isSignatureValid(respData, this.key, asSignType(this.signType))) {
				log.warn("WXPay-{} mpUnified sign error", outTradeNo);
				throw new BusinessException("签名验证失败");
			}
		} catch (Exception e) {
			log.error("WXPay-{} mpUnified sign error", outTradeNo, e);
			throw new BusinessException("签名验证失败");
		}
		String prepayId = respData.get("prepay_id");
		String nonceStr = respData.get("nonce_str");
		
		WxpayUnifiedMPDTO mpPayUnifiedDTO = new WxpayUnifiedMPDTO();
		mpPayUnifiedDTO.setAppId(this.appId);
		mpPayUnifiedDTO.setTimeStamp(String.valueOf((int)(System.currentTimeMillis() /1000L)));
		mpPayUnifiedDTO.setNonceStr(nonceStr);
		mpPayUnifiedDTO.setPackageValue("prepay_id="+prepayId);
		mpPayUnifiedDTO.setSignType(this.signType);
		String sign = "";
		try {
			sign = WXPayUtil.generateSignature(mpPayUnifiedDTO.asMap(), this.key, asSignType(this.signType));
		} catch (Exception e) {
			throw new BusinessException("支付数据签名失败");
		}
		mpPayUnifiedDTO.setPaySign(sign);
		return mpPayUnifiedDTO;
	}
	
	@Override
	public WxpayUnifiedH5DTO h5Unified(WxpayH5PayParam h5PayParam) {
		String outTradeNo = h5PayParam.getOutTradeNo();
		int totalFee = h5PayParam.getTotalFee();
		String body = h5PayParam.getBody();
		String spbillClientIp = h5PayParam.getSpbillClientIp();
		String redirectUrl = h5PayParam.getRedirectUrl();
		Map<String, String> attach = h5PayParam.getAttach();
		
		Map<String, String> respData = null;
		try {
			Map<String, String> reqData = new HashMap<String, String>();
			reqData.put("body", body);
			reqData.put("out_trade_no", outTradeNo);
			reqData.put("spbill_create_ip", spbillClientIp);
			reqData.put("total_fee", String.valueOf(totalFee));
			reqData.put("trade_type", "MWEB");
			if(attach != null) {
				reqData.put("attach", URLEncoder.encode(JsonUtil.toStr(attach), "UTF-8"));
			}
			respData = sdk.unifiedOrder(reqData);
		} catch (Exception e) {
			log.error("WXPay-{} h5Unified error", outTradeNo, e);
			throw new BusinessException("支付请求异常");
		}
		String returnCode = respData.get("return_code");
		String returnMsg = respData.get("return_msg");
		if(!WXPayConstants.SUCCESS.equals(returnCode)) {
			log.error("WXPay-{} h5Unified error, return_code:{}  return_msg:{}", 
					outTradeNo, returnCode, returnMsg);
			throw new BusinessException("支付请求异常，"+returnCode+":"+returnMsg);
		}
		String resultCode = respData.get("result_code");
		String errCode = respData.get("err_code");
		String errCodeDes = respData.get("err_code_des");
		if(!WXPayConstants.SUCCESS.equals(resultCode)) {
			log.error("WXPay-{} h5Unified error,  err_code:{}  err_code_des:{}", 
					outTradeNo, errCode, errCodeDes);
			throw new BusinessException("支付请求异常，"+errCode+":"+errCodeDes);
		}
		try {
			if(!WXPayUtil.isSignatureValid(respData, this.key, asSignType(this.signType))) {
				log.warn("WXPay-{} h5Unified sign error", outTradeNo);
				throw new BusinessException("签名验证失败");
			}
		} catch (Exception e) {
			log.error("WXPay-{} h5Unified sign error", outTradeNo, e);
			throw new BusinessException("签名验证失败");
		}
		String mwebUrl = respData.get("mweb_url");
		if(StringUtil.isNotBlank(redirectUrl)) {
			try {
				mwebUrl += ("&redirect_url="+URLEncoder.encode(redirectUrl, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				log.error("WXPay-{} h5Unified error", outTradeNo, e);
			}
		}
		
		WxpayUnifiedH5DTO h5PayUnifiedDTO = new WxpayUnifiedH5DTO();
		h5PayUnifiedDTO.setMwebUrl(mwebUrl);
		return h5PayUnifiedDTO;
	}
	
	@Override
	public WxpayUnifiedProgramDTO programUnified(WxpayProgramPayParam programPayParam) {
		String outTradeNo = programPayParam.getOutTradeNo();
		int totalFee = programPayParam.getTotalFee();
		String body = programPayParam.getBody();
		String spbillClientIp = programPayParam.getSpbillClientIp();
		String openid = programPayParam.getOpenid();
		Map<String, String> attach = programPayParam.getAttach();
		
		Map<String, String> respData = null;
		try {
			Map<String, String> reqData = new HashMap<String, String>();
			reqData.put("body", body);
			reqData.put("out_trade_no", outTradeNo);
			reqData.put("spbill_create_ip", spbillClientIp);
			reqData.put("total_fee", String.valueOf(totalFee));
			reqData.put("trade_type", "JSAPI");
			reqData.put("openid", openid);
			if(attach != null) {
				reqData.put("attach", URLEncoder.encode(JsonUtil.toStr(attach), "UTF-8"));
			}
			respData = sdk.unifiedOrder(reqData);
		} catch (Exception e) {
			log.error("WXPay-{} programUnified error", outTradeNo, e);
			throw new BusinessException("支付请求异常");
		}
		String returnCode = respData.get("return_code");
		String returnMsg = respData.get("return_msg");
		if(!WXPayConstants.SUCCESS.equals(returnCode)) {
			log.error("WXPay-{} programUnified error, return_code:{}  return_msg:{}", 
					outTradeNo, returnCode, returnMsg);
			throw new BusinessException("支付请求异常，"+returnCode+":"+returnMsg);
		}
		String resultCode = respData.get("result_code");
		String errCode = respData.get("err_code");
		String errCodeDes = respData.get("err_code_des");
		if(!WXPayConstants.SUCCESS.equals(resultCode)) {
			log.error("WXPay-{} programUnified error,  err_code:{}  err_code_des:{}", 
					outTradeNo, errCode, errCodeDes);
			throw new BusinessException("支付请求异常，"+errCode+":"+errCodeDes);
		}
		try {
			if(!WXPayUtil.isSignatureValid(respData, this.key, asSignType(this.signType))) {
				log.warn("WXPay-{} programUnified sign error", outTradeNo);
				throw new BusinessException("签名验证失败");
			}
		} catch (Exception e) {
			log.error("WXPay-{} programUnified sign error", outTradeNo, e);
			throw new BusinessException("签名验证失败");
		}
		String prepayId = respData.get("prepay_id");
		String nonceStr = respData.get("nonce_str");
		
		WxpayUnifiedProgramDTO programPayUnifiedDTO = new WxpayUnifiedProgramDTO();
		programPayUnifiedDTO.setAppId(this.appId);
		programPayUnifiedDTO.setTimeStamp(String.valueOf((int)(System.currentTimeMillis() /1000L)));
		programPayUnifiedDTO.setNonceStr(nonceStr);
		programPayUnifiedDTO.setPackageValue("prepay_id="+prepayId);
		programPayUnifiedDTO.setSignType(this.signType);
		String sign = "";
		try {
			sign = WXPayUtil.generateSignature(programPayUnifiedDTO.asMap(), this.key, asSignType(this.signType));
		} catch (Exception e) {
			throw new BusinessException("支付数据签名失败");
		}
		programPayUnifiedDTO.setPaySign(sign);
		return programPayUnifiedDTO;
	}
	
	@Override
	public WxpayUnifiedNativeDTO nativeUnified(WxpayNativePayParam nativePayParam) {
		String outTradeNo = nativePayParam.getOutTradeNo();
		int totalFee = nativePayParam.getTotalFee();
		String body = nativePayParam.getBody();
		String spbillClientIp = nativePayParam.getSpbillClientIp();
		String productId = nativePayParam.getProductId();
		Map<String, String> attach = nativePayParam.getAttach();
		
		Map<String, String> respData = null;
		try {
			Map<String, String> reqData = new HashMap<String, String>();
			reqData.put("body", body);
			reqData.put("out_trade_no", outTradeNo);
			reqData.put("spbill_create_ip", spbillClientIp);
			reqData.put("total_fee", String.valueOf(totalFee));
			reqData.put("trade_type", "NATIVE");
			reqData.put("product_id", productId);
			if(attach != null) {
				reqData.put("attach", URLEncoder.encode(JsonUtil.toStr(attach), "UTF-8"));
			}
			respData = sdk.unifiedOrder(reqData);
		} catch (Exception e) {
			log.error("WXPay-{} nativeUnified error", outTradeNo, e);
			throw new BusinessException("支付请求异常");
		}
		String returnCode = respData.get("return_code");
		String returnMsg = respData.get("return_msg");
		if(!WXPayConstants.SUCCESS.equals(returnCode)) {
			log.error("WXPay-{} nativeUnified error, return_code:{}  return_msg:{}", 
					outTradeNo, returnCode, returnMsg);
			throw new BusinessException("支付请求异常，"+returnCode+":"+returnMsg);
		}
		String resultCode = respData.get("result_code");
		String errCode = respData.get("err_code");
		String errCodeDes = respData.get("err_code_des");
		if(!WXPayConstants.SUCCESS.equals(resultCode)) {
			log.error("WXPay-{} nativeUnified error,  err_code:{}  err_code_des:{}", 
					outTradeNo, errCode, errCodeDes);
			throw new BusinessException("支付请求异常，"+errCode+":"+errCodeDes);
		}
		try {
			if(!WXPayUtil.isSignatureValid(respData, this.key, asSignType(this.signType))) {
				log.warn("WXPay-{} nativeUnified sign error", outTradeNo);
				throw new BusinessException("签名验证失败");
			}
		} catch (Exception e) {
			log.error("WXPay-{} nativeUnified sign error", outTradeNo, e);
			throw new BusinessException("签名验证失败");
		}
		String codeUrl = respData.get("code_url");
		
		WxpayUnifiedNativeDTO unified = new WxpayUnifiedNativeDTO();
		unified.setCodeUrl(codeUrl);
		return unified;
	}
	
	/**
	 * 微信支付查询
	 **//*
	public ServiceResult<WXPayQuery> query(WXPayQueryModel model) {
		String outTradeNo = model.getOutTradeNo();
		String transactionId = model.getTransactionId();
		
		Map<String, String> reqData = new HashMap<>();
		reqData.put("out_trade_no", outTradeNo);
		reqData.put("transaction_id", transactionId);
		Map<String, String> respData = null;
		try {
			respData = SDK.orderQuery(reqData);
		} catch (Exception e) {
			logger.error("WXPay-{}-{} query error", outTradeNo, transactionId, e);
			return ServiceResult.fail("支付查询异常");
		}
		try {
			if(!WXPayUtil.isSignatureValid(respData, SDK.getKey())) {
				logger.warn("WXPay-{}-{} query sign fail", outTradeNo, transactionId);
				return ServiceResult.fail("签名验证失败");
			}
		} catch (Exception e) {
			logger.error("WXPay-{}-{} query sign error", outTradeNo, transactionId, e);
			return ServiceResult.fail("签名验证失败");
		}
		String returnCode = respData.get("return_code");
		String returnMsg = respData.get("return_msg");
		if(!WXPayConstants.SUCCESS.equals(returnCode)) {
			logger.error("WXPay-{}-{} query error, return_code:{}  return_msg:{}", 
					outTradeNo, transactionId, returnCode, returnMsg);
			return ServiceResult.fail(returnCode+":"+returnMsg);
		}
		String resultCode = respData.get("result_code");
		String errCode = respData.get("err_code");
		String errCodeDes = respData.get("err_code_des");
		if(!WXPayConstants.SUCCESS.equals(resultCode)) {
			logger.error("WXPay-{}-{} query error, err_code:{}  err_code_des:{}", 
					outTradeNo, transactionId, errCode, errCodeDes);
			return ServiceResult.fail(errCode+":"+errCodeDes);
		}
		transactionId = respData.get("transaction_id");
		outTradeNo = respData.get("out_trade_no");
		String totalFee = respData.get("total_fee");
		String tradeState = respData.get("trade_state");
		WXPayQuery query = new WXPayQuery();
		query.setTransactionId(transactionId);
		query.setOutTradeNo(outTradeNo);
		query.setTotalFee(totalFee);
		query.setTradeState(tradeState);
		return ServiceResult.ok(query);
	}
	
	
	*//**
	 * 微信支付订单关闭
	 **//*
	public ServiceResult<WXPayClose> close(WXPayCloseModel model) {
		String outTradeNo = model.getOutTradeNo();
		
		Map<String, String> reqData = new HashMap<>();
		reqData.put("out_trade_no", outTradeNo);
		Map<String, String> respData = null;
		try {
			respData = SDK.closeOrder(reqData);
		} catch (Exception e) {
			logger.error("WXPay-{} close error", outTradeNo, e);
			return ServiceResult.fail("支付关闭异常");
		}
		try {
			if(!WXPayUtil.isSignatureValid(respData, SDK.getKey())) {
				logger.warn("WXPay-{} close sign fail", outTradeNo);
				return ServiceResult.fail("签名验证失败");
			}
		} catch (Exception e) {
			logger.error("WXPay-{} close sign error", outTradeNo, e);
			return ServiceResult.fail("签名验证失败");
		}
		String returnCode = respData.get("return_code");
		String returnMsg = respData.get("return_msg");
		if(!WXPayConstants.SUCCESS.equals(returnCode)) {
			logger.error("WXPay-{} close error, return_code:{}  return_msg:{}", 
					outTradeNo, returnCode, returnMsg);
			return ServiceResult.fail(returnCode+":"+returnMsg);
		}
		String resultCode = respData.get("result_code");
		String errCode = respData.get("err_code");
		String errCodeDes = respData.get("err_code_des");
		if(!WXPayConstants.SUCCESS.equals(resultCode)) {
			logger.error("WXPay-{} close error, err_code:{}  err_code_des:{}", 
					outTradeNo, errCode, errCodeDes);
			return ServiceResult.fail(errCode+":"+errCodeDes);
		}
		WXPayClose close = new WXPayClose();
		return ServiceResult.ok(close);
	}
	
	*//**
	 * 微信支付退款
	 *//*
	public ServiceResult<WXPayRefund> refund(WXPayRefundParam model) {
		String outRefundNo = model.getOutRefundNo();
		String transactionId = model.getTransactionId();
		String outTradeNo = model.getOutTradeNo();
		String totalFee = model.getTotalFee();
		String refundFee = model.getRefundFee();
		
		Map<String, String> reqData = new HashMap<String, String>();
		reqData.put("out_trade_no", outTradeNo);
		reqData.put("out_refund_no", outRefundNo);
		reqData.put("total_fee", totalFee);
		reqData.put("refund_fee", refundFee);
		Map<String, String> respData = null;
		try {
			respData = SDK.refund(reqData);
		} catch (Exception e) {
			logger.error("WXPay-{} refund error", outTradeNo, e);
			return ServiceResult.fail("支付退款异常");
		}
		try {
			if(!WXPayUtil.isSignatureValid(respData, SDK.getKey())) {
				logger.warn("WXPay-{} refund sign fail", outTradeNo);
				return ServiceResult.fail("签名验证失败");
			}
		} catch (Exception e) {
			logger.error("WXPay-{} refund sign error", outTradeNo, e);
			return ServiceResult.fail("签名验证失败");
		}
		String returnCode = respData.get("return_code");
		String returnMsg = respData.get("return_msg");
		if(!WXPayConstants.SUCCESS.equals(returnCode)) {
			logger.error("WXPay-{} refund error, return_code:{}  return_msg:{}", 
					outTradeNo, returnCode, returnMsg);
			return ServiceResult.fail(returnCode+":"+returnMsg);
		}
		String resultCode = respData.get("result_code");
		String errCode = respData.get("err_code");
		String errCodeDes = respData.get("err_code_des");
		if(!WXPayConstants.SUCCESS.equals(resultCode)) {
			logger.error("WXPay-{} refund error, err_code:{}  err_code_des:{}", 
					outTradeNo, errCode, errCodeDes);
			return ServiceResult.fail(errCode+":"+errCodeDes);
		}
		String refundId = respData.get("refund_id");
		outRefundNo = respData.get("out_refund_no");
		transactionId = respData.get("transactonId_id");
		outTradeNo = respData.get("out_trade_no");
		totalFee = respData.get("total_fee");
		refundFee = respData.get("refund_fee");
		
		WXPayRefund refund = new WXPayRefund();
		refund.setTransactionId(transactionId);
		refund.setOutTradeNo(outTradeNo);
		refund.setOutRefundNo(outRefundNo);
		refund.setRefundId(refundId);
		refund.setRefundFee(refundFee);
		refund.setTotalFee(totalFee);
		return ServiceResult.ok(refund);
	}*/
	
	@Override
	public WxpayNotifyUrlDTO checkAndGetNotifyData(Map<String, String> params) {
		if (params == null) {
			throw new BusinessException("微信支付通知参数为空");
		}
		String returnCode = params.get("return_code");
		String returnMsg = params.get("return_msg");
		if (!WXPayConstants.SUCCESS.equals(returnCode)) {
			throw new BusinessException("微信支付异常，"+returnCode+":"+returnMsg);
		}
		String resultCode = params.get("result_code");
		String errCode = params.get("err_code");
		String errCodeDesc = params.get("err_code_des");
		if (!WXPayConstants.SUCCESS.equals(resultCode)) {
			throw new BusinessException("微信业务异常，"+errCode+":"+errCodeDesc);
		}
		String appid = params.get("appid");
		if (!this.appId.equals(appid)) {
			throw new BusinessException("应用ID不一致");
		}
		String mchId = params.get("mch_id");
		if(!this.mchId.equals(mchId)) {
			throw new BusinessException("商户号不一致");
		}
		String outTradeNo = params.get("out_trade_no");
		if (StringUtils.isBlank(outTradeNo)) {
			throw new BusinessException("商户订单号为空");
		}
		String transactionId = params.get("transaction_id");
		if(StringUtil.isBlank(transactionId)) {
			throw new BusinessException("支付号为空");
		}
		boolean signatureValid = false;
		try {
			signatureValid = WXPayUtil.isSignatureValid(params, this.key, asSignType(this.signType));
			if(!signatureValid) {
				throw new BusinessException("签名验证失败");
			}
		} catch (Exception e) {
			log.error("WXPay-{} notifyUrl error", outTradeNo, e);
			throw new BusinessException("签名验证失败");
		}
		int totalFee = Integer.parseInt(params.get("total_fee"));
		int cashFee = Integer.parseInt(params.get("cash_fee"));
		WxpayNotifyUrlDTO notifyUrlDTO = new WxpayNotifyUrlDTO();
		notifyUrlDTO.setAppid(appid);
		notifyUrlDTO.setMchId(mchId);
		notifyUrlDTO.setTotalFee(totalFee);
		notifyUrlDTO.setCashFee(cashFee);
		notifyUrlDTO.setTransactionId(transactionId);
		notifyUrlDTO.setOutTradeNo(outTradeNo);
		return notifyUrlDTO;
	}
}
