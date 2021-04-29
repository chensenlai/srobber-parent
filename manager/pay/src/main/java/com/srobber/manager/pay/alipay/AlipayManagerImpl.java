package com.srobber.manager.pay.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.srobber.common.exeption.BusinessException;
import com.srobber.common.util.DecimalUtil;
import com.srobber.common.util.JsonUtil;
import com.srobber.common.util.StringUtil;
import com.srobber.manager.pay.AlipayManager;
import com.srobber.manager.pay.alipay.dto.AlipayNotifyUrlDTO;
import com.srobber.manager.pay.alipay.dto.AlipayReturnUrlDTO;
import com.srobber.manager.pay.alipay.dto.AlipayTradeDTO;
import com.srobber.manager.pay.alipay.param.AlipayPayAppParam;
import com.srobber.manager.pay.alipay.param.AlipayPayPageParam;
import com.srobber.manager.pay.alipay.param.AlipayPayProgramParam;
import com.srobber.manager.pay.alipay.param.AlipayPayWapParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 支付宝支付客户端
 * 封装各种接口请求,包括下单,支付,查询,退款,对帐等
 *
 * @author chensenlai
 */
@Slf4j
@Data
public class AlipayManagerImpl implements AlipayManager {
	
	private static final String ALIPAY_GATEWAY = "https://openapi.alipay.com/gateway.do";
	private static final String CHARSET = "UTF-8";

	/**
	 * 商户ID
	 */
	private String sellerId;
	/**
	 * 应用AppID
	 * 开放平台申请应用AppID和权限
	 * https://open.alipay.com/platform/home.htm
	 */
	private String appId;
	/**
	 * 应用私钥
	 */
	private String appPrivateKey;
	/**
	 * 支付宝公钥
	 */
	private String alipayPublicKey;
	/**
	 * 网页支付结果返回地址
	 */
	private String returnUrl;
	/**
	 * 支付宝支付结果通知回调地址
	 */
	private String notifyUrl;
	/**
	 * RSA2
	 */
	private String signType;

	/**
	 * 支付宝提供接口客户端
	 * 线程安全
	 */
	private DefaultAlipayClient client;
	
	public void init() {
		client = new DefaultAlipayClient(
				ALIPAY_GATEWAY,
				appId,
				appPrivateKey,
				"json", 
				CHARSET, 
				alipayPublicKey,
				signType);
	}

	/**
	 * 统一下单 - APP支付
	 * @param appPayParam 	app支付参数
	 * @return 支付宝请求下单
	 */
	@Override
	public String appUnified(AlipayPayAppParam appPayParam) {
		String outTradeNo = appPayParam.getOutTradeNo();
		int totalFee = appPayParam.getTotalFee();
		String subject = appPayParam.getSubject();
		//单位是元
		String totalAmount = String.valueOf(DecimalUtil.div(totalFee, 100.0, 2));
		String timeoutExpress = appPayParam.getTimeoutExpress();
		Map<String, String> passBackParams = appPayParam.getPassbackParams();
		try {
			AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();
		    alipayRequest.setNotifyUrl(notifyUrl);
		    AlipayTradeAppPayModel bizModel = new AlipayTradeAppPayModel();
		    bizModel.setOutTradeNo(outTradeNo);
		    bizModel.setTotalAmount(totalAmount);
		    bizModel.setSubject(subject);
		    bizModel.setProductCode("QUICK_MSECURITY_PAY");
		    if(StringUtil.isNotBlank(timeoutExpress)) {
		    	bizModel.setTimeoutExpress(timeoutExpress);
		    }
		    if(passBackParams != null) {
		    	bizModel.setPassbackParams(URLEncoder.encode(JsonUtil.toStr(passBackParams), "UTF-8"));
		    }
		    alipayRequest.setBizModel(bizModel);
		    AlipayTradeAppPayResponse response = client.sdkExecute(alipayRequest);
		    if(!response.isSuccess()) {
		    	log.error("alipay-{} appUnified error, code {} msg {}", outTradeNo,
		    			response.getCode(), response.getMsg());
		    	throw new BusinessException("支付请求异常，"+response.getCode()+":"+response.getMsg());
		    }
		    String orderStr = response.getBody();
			log.info("alipay-{} appUnified body {}", outTradeNo, orderStr);
		    return orderStr;
		} catch (AlipayApiException | UnsupportedEncodingException e) {
			log.error("alipay-{} appUnified error", outTradeNo, e);
			throw new BusinessException("支付请求异常");
		}
	}

	/**
	 * 统一下单 - 手机网站(支付宝生活号内嵌H5)
	 * @param wapPayParam 	手机网站支付参数
	 * @return 请求支付页面
	 */
	@Override
	public String wapUnified(AlipayPayWapParam wapPayParam) {
		String outTradeNo = wapPayParam.getOutTradeNo();
		int totalFee = wapPayParam.getTotalFee();
		String subject = wapPayParam.getSubject();
		String totalAmount = String.valueOf(DecimalUtil.div(totalFee, 100.0, 2));  //单位是元
		String timeoutExpress = wapPayParam.getTimeoutExpress();
		Map<String, String> passBackParams = wapPayParam.getPassbackParams();
		try {
		    AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
		    alipayRequest.setReturnUrl(returnUrl);
		    alipayRequest.setNotifyUrl(notifyUrl);
		    AlipayTradeWapPayModel bizModel = new AlipayTradeWapPayModel();
		    bizModel.setOutTradeNo(outTradeNo);
		    bizModel.setTotalAmount(totalAmount);
		    bizModel.setSubject(subject);
		    bizModel.setProductCode("QUICK_WAP_WAY");
		    if(StringUtil.isNotBlank(timeoutExpress)) {
		    	bizModel.setTimeoutExpress(timeoutExpress);
		    }
		    if(passBackParams != null) {
		    	bizModel.setPassbackParams(URLEncoder.encode(JsonUtil.toStr(passBackParams), "UTF-8"));
		    }
		    alipayRequest.setBizModel(bizModel);
		    AlipayTradeWapPayResponse response = client.pageExecute(alipayRequest);
		    if(!response.isSuccess()) {
		    	log.error("alipay-{} wapUnified error, code {} msg {}", outTradeNo,
		    			response.getCode(), response.getMsg());
		    	throw new BusinessException("支付请求异常，"+response.getCode()+":"+response.getMsg());
		    }
		    String formHtml = response.getBody();
		    log.info("alipay-{} wapUnified body {}", outTradeNo, formHtml);
		    return formHtml;
		}catch (AlipayApiException | UnsupportedEncodingException e) {
			log.error("alipay-{} wapUnified error", outTradeNo, e);
			throw new BusinessException("支付请求异常");
		}
	}

	/**
	 * 统一下单 - 电脑网站支付
	 * @param pagePayParam 	网页请求支付参数
	 * @return 请求支付页面
	 */
	@Override
	public String pageUnified(AlipayPayPageParam pagePayParam) {
		String outTradeNo = pagePayParam.getOutTradeNo();
		int totalFee = pagePayParam.getTotalFee();
		String subject = pagePayParam.getSubject();
		String totalAmount = String.valueOf(DecimalUtil.div(totalFee, 100.0, 2));  //单位是元
		String timeoutExpress = pagePayParam.getTimeoutExpress();
		Map<String, String> passBackParams = pagePayParam.getPassbackParams();
		try {
			AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		    alipayRequest.setReturnUrl(returnUrl);
		    alipayRequest.setNotifyUrl(notifyUrl);
		    AlipayTradePagePayModel bizModel = new AlipayTradePagePayModel();
		    bizModel.setOutTradeNo(outTradeNo);
		    bizModel.setTotalAmount(totalAmount);
		    bizModel.setSubject(subject);
		    bizModel.setProductCode("FAST_INSTANT_TRADE_PAY");
		    if(StringUtil.isNotBlank(timeoutExpress)) {
		    	bizModel.setTimeoutExpress(timeoutExpress);
		    }
		    if(passBackParams != null) {
		    	bizModel.setPassbackParams(URLEncoder.encode(JsonUtil.toStr(passBackParams), "UTF-8"));
		    }
		    alipayRequest.setBizModel(bizModel);
		    AlipayTradePagePayResponse response = client.pageExecute(alipayRequest);
		    if(!response.isSuccess()) {
		    	log.error("alipay-{} pageUnified error, code {} msg {}", outTradeNo,
		    			response.getCode(), response.getMsg());
		    	throw new BusinessException("支付请求异常，"+response.getCode()+":"+response.getMsg());
		    }
		    String formHtml = response.getBody();
		    log.info("alipay-{} pageUnified body {}", outTradeNo, formHtml);
		    return formHtml;
		}catch (AlipayApiException | UnsupportedEncodingException e) {
			log.error("alipay-{} pageUnified error", outTradeNo, e);
			throw new BusinessException("支付请求异常");
		}
	}

	/**
	 * 统一下单 - 支付宝小程序
	 * @param programPayParam 	小程序支付请求参数
	 * @return 小程序客户端根据orderStr发起调用
	 */
	@Override
	public String programUnified(AlipayPayProgramParam programPayParam) {
		String outTradeNo = programPayParam.getOutTradeNo();
		int totalFee = programPayParam.getTotalFee();
		String subject = programPayParam.getSubject();
		String totalAmount = String.valueOf(DecimalUtil.div(totalFee, 100.0, 2));  //单位是元
		String timeoutExpress = programPayParam.getTimeoutExpress();
		Map<String, String> passBackParams = programPayParam.getPassbackParams();
		try {
			AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();
		    alipayRequest.setNotifyUrl(notifyUrl);
		    AlipayTradeAppPayModel bizModel = new AlipayTradeAppPayModel();
		    bizModel.setOutTradeNo(outTradeNo);
		    bizModel.setTotalAmount(totalAmount);
		    bizModel.setSubject(subject);
		    bizModel.setProductCode("QUICK_MSECURITY_PAY");
		    if(StringUtil.isNotBlank(timeoutExpress)) {
		    	bizModel.setTimeoutExpress(timeoutExpress);
		    }
		    if(passBackParams != null) {
		    	bizModel.setPassbackParams(URLEncoder.encode(JsonUtil.toStr(passBackParams), "UTF-8"));
		    }
		    alipayRequest.setBizModel(bizModel);
		    AlipayTradeAppPayResponse response = client.sdkExecute(alipayRequest);
		    if(!response.isSuccess()) {
		    	log.error("alipay-{} programUnified error, code {} msg {}", outTradeNo,
		    			response.getCode(), response.getMsg());
		    	throw new BusinessException("支付请求异常，"+response.getCode()+":"+response.getMsg());
		    }
		    String orderStr = response.getBody();
		    log.info("alipay-{} programUnified body {}", outTradeNo, orderStr);
		    return orderStr;
		}catch (AlipayApiException | UnsupportedEncodingException e) {
			log.error("alipay-{} programUnified error", outTradeNo, e);
			throw new BusinessException("支付请求异常");
		}
	}

	/**
	 * 支付宝return_url地址回调参数获取
	 * 如果参数校验不通过抛出运行时异常
	 * @param params 支付回调参数
	 * @return 通知数据
	 */
	@Override
	public AlipayReturnUrlDTO checkAndGetReturnData(Map<String, String> params) {
		try {
			boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, CHARSET, signType);
			if(!signVerified) {
				throw new BusinessException("签名验证失败");
			}
		} catch (AlipayApiException e) {
			log.error("alipay-{} returnUrl error", params.get("out_trade_no"), e);
			throw new BusinessException("签名验证失败");
		}
		String appId = params.get("app_id");
		String sellerId = params.get("seller_id");
		String tradeNo = params.get("trade_no");
		String outTradeNo = params.get("out_trade_no");
		String totalAmount = params.get("total_amount");
		String tradeStatus = params.get("trade_status");
		if(!this.appId.equals(appId)) {
			throw new BusinessException("appId不正确");
		}
		if(!this.sellerId.equals(sellerId)) {
			throw new BusinessException("sellerId不正确");
		}
		int totalFee = (int)(DecimalUtil.mul(Double.parseDouble(totalAmount), 100)); //转成分
		
		AlipayReturnUrlDTO returnUrl = new AlipayReturnUrlDTO();
		returnUrl.setAppId(appId);
		returnUrl.setSellerId(sellerId);
		returnUrl.setTradeNo(tradeNo);
		returnUrl.setOutTradeNo(outTradeNo);
		returnUrl.setTotalFee(totalFee);
		returnUrl.setTradeStatus(tradeStatus);
		return returnUrl;
	}

	/**
	 * 支付宝notify_url地址回调参数获取,需要验证签名和数据有效性
	 * 如果参数校验不通过抛出运行时异常
	 * @param params 支付回调参数
	 * @return 通知数据
	 */
	@Override
	public AlipayNotifyUrlDTO checkAndGetNotifyData(Map<String, String> params) {
		try {
			boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, CHARSET, signType);
			if(!signVerified) {
				throw new BusinessException("签名验证失败");
			}
		} catch (Exception e) {
			log.error("alipay-{} notifyUrl error", params.get("out_trade_no"), e);
			throw new BusinessException("签名验证失败");
		}
		String appId = params.get("app_id");
		String sellerId = params.get("seller_id");
		String tradeNo = params.get("trade_no");
		String outTradeNo = params.get("out_trade_no");
		String totalAmount = params.get("total_amount");
		String tradeStatus = params.get("trade_status");
		if(!this.appId.equals(appId)) {
			throw new BusinessException("appId不正确");
		}
		if(!this.sellerId.equals(sellerId)) {
			throw new BusinessException("sellerId不正确");
		}
		int totalFee = (int)(DecimalUtil.mul(Double.parseDouble(totalAmount), 100)); //转成分
		
		AlipayNotifyUrlDTO notifyUrl = new AlipayNotifyUrlDTO();
		notifyUrl.setAppId(appId);
		notifyUrl.setSellerId(sellerId);
		notifyUrl.setTradeNo(tradeNo);
		notifyUrl.setOutTradeNo(outTradeNo);
		notifyUrl.setTotalFee(totalFee);
		notifyUrl.setTradeStatus(tradeStatus);
		return notifyUrl;
	}

	/**
	 * 根据外部交易号(业务方交易号)查询支付订单
	 * @param outTradeNo 业务方交易号
	 * @return 交易信息
	 */
	@Override
	public AlipayTradeDTO queryByOutTradeNo(String outTradeNo) {
		try {
			AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
			AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
			bizModel.setOutTradeNo(outTradeNo);
			alipayRequest.setBizModel(bizModel);
		    AlipayTradeQueryResponse response = client.execute(alipayRequest);
		    if(!response.isSuccess()) {
		    	log.error("alipay-{} query error, code {} msg {}",
		    			outTradeNo, response.getCode(), response.getMsg());
		    	throw new BusinessException("支付查询异常，"+response.getCode()+":"+response.getMsg());
		    }
		    String orderStr = response.getBody();
		    log.info("alipay-{} query body {}", outTradeNo, orderStr);
		    AlipayTradeDTO trade = JsonUtil.toObject(orderStr, AlipayTradeDTO.class);
		    return trade;
		}catch (AlipayApiException e) {
			log.error("alipay-{} query error", outTradeNo, e);
			throw new BusinessException("支付查询异常");
		}
	}

	/**
	 * 根据交易号(支付平台交易号)查询支付订单
	 * @param tradeNo 支付平台交易号
	 * @return 交易信息
	 */
	@Override
	public AlipayTradeDTO queryByTradeNo(String tradeNo) {
		try {
			AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
			AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
			bizModel.setTradeNo(tradeNo);
			alipayRequest.setBizModel(bizModel);
		    AlipayTradeQueryResponse response = client.execute(alipayRequest);
		    if(!response.isSuccess()) {
		    	log.error("alipay-{} query error, code {} msg {}",
		    			tradeNo, response.getCode(), response.getMsg());
		    	throw new BusinessException("支付查询异常，"+response.getCode()+":"+response.getMsg());
		    }
		    String orderStr = response.getBody();
			log.info("alipay-{} query body {}", tradeNo, orderStr);
		    AlipayTradeDTO trade = JsonUtil.toObject(response.getBody(), AlipayTradeDTO.class);
		    return trade;
		}catch (AlipayApiException e) {
			log.error("alipay-{} query error", tradeNo, e);
			throw new BusinessException("支付查询异常");
		}
	}

	/**
	 * 根据交易号(支付平台交易号)关闭支付订单
	 * @param tradeNo 支付平台交易号
	 * @param operatorId 操作员编号
	 * @return 操作结果
	 */
	@Override
	public boolean close(String tradeNo, String operatorId) {
		try {
			AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();
			AlipayTradeCloseModel bizModel = new AlipayTradeCloseModel();
			bizModel.setTradeNo(tradeNo);
			bizModel.setOperatorId(operatorId);
			alipayRequest.setBizModel(bizModel);
		    AlipayTradeCloseResponse response = client.execute(alipayRequest);
		    if(!response.isSuccess()) {
		    	log.error("alipay-{} close error, code {} msg {}", tradeNo,
		    			response.getCode(), response.getMsg());
		    	throw new BusinessException("支付关闭异常，"+response.getCode()+":"+response.getMsg());
		    }
		    String closeStr = response.getBody();
			log.info("alipay-{} close body {}", tradeNo, closeStr);
		    return true;
		}catch (AlipayApiException e) {
			log.error("alipay-{} close error", tradeNo, e);
			throw new BusinessException("支付关闭异常");
		}
	}

	/**
	 * 根据交易号(支付平台交易号)发起退款请求
	 * @param tradeNo	支付平台交易号
	 * @param refundFee	退款金额
	 * @param operatorId 操作员编号
	 * @return 操作结果
	 */
	@Override
	public boolean refund(String tradeNo, int refundFee, String operatorId) {
		String refundAmount = String.valueOf(DecimalUtil.div(refundFee, 100.0, 2));  //单位是元
		try {
			AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
		    alipayRequest.setNotifyUrl(notifyUrl);
		    AlipayTradeRefundModel bizModel = new AlipayTradeRefundModel();
		    bizModel.setTradeNo(tradeNo);
		    bizModel.setRefundAmount(refundAmount);
		    bizModel.setOperatorId(operatorId);
		    alipayRequest.setBizModel(bizModel);
		    AlipayTradeRefundResponse response = client.execute(alipayRequest);
		    if(!response.isSuccess()) {
		    	log.error("alipay-{} refund error, code {} msg {}", tradeNo,
		    			response.getCode(), response.getMsg());
		    	throw new BusinessException("支付退款异常，"+response.getCode()+":"+response.getMsg());
		    }
		    String refundStr = response.getBody();
		    log.info("alipay-{} refund body {}", tradeNo, refundStr);
		    return true;
		}catch (AlipayApiException e) {
			log.error("alipay-{} refund error", tradeNo, e);
			throw new BusinessException("支付退款异常");
		}
	}
}
