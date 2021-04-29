package com.srobber.manager.pay.apple;

import com.srobber.common.spring.EnvironmentContext;
import com.srobber.common.util.HttpClient;
import com.srobber.common.util.HttpClient.HttpResult;
import com.srobber.common.util.JsonUtil;
import com.srobber.manager.pay.ApplePayManager;
import com.srobber.manager.pay.apple.constant.ReceiptVerifyStatus;
import com.srobber.manager.pay.apple.dto.ReceiptVerifyResultDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 苹果支付客户端
 *
 * @author chensenlai
 */
@Slf4j
@Data
public class ApplePayManagerImpl implements ApplePayManager {

    /**
     * 正式地址
     */
	private static String PRODUCT_URL = "https://buy.itunes.apple.com/verifyReceipt";
	/**
	 * 沙箱地址
	 */
	private static String SANDBOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";
	
	/**
	 * 苹果支付密码
	 */
    private String password;
    
	@Override
	public ReceiptVerifyResultDTO verifyReceipt(String receiptData) {
		Map<String, String> paramMap = new HashMap<>(8);
		paramMap.put("receipt-data", receiptData);
		paramMap.put("password", password);
        
		ReceiptVerifyResultDTO receiptVerifyDTO = null;
		if(EnvironmentContext.isProdEnv()) {
			//生产环境使用生产地址
			receiptVerifyDTO = doVerifyReceipt(PRODUCT_URL, paramMap);
			//但是苹果要求提测也用沙箱地址, 此时响应码21007
			if(receiptVerifyDTO!=null && receiptVerifyDTO.getStatus()==ReceiptVerifyStatus.STATUS_USE_SANDBOX) {
				receiptVerifyDTO = doVerifyReceipt(SANDBOX_URL, paramMap);
				//throw new IllegalStateException("沙箱票据, 禁止解析");
			}
		} else {
			//测试环境使用沙箱地址
			receiptVerifyDTO = doVerifyReceipt(SANDBOX_URL, paramMap);
		}
		return receiptVerifyDTO;
	}
    
    private ReceiptVerifyResultDTO doVerifyReceipt(String url, Map<String, String> paramMap) {
    	long s = System.currentTimeMillis();
    	try {
    		Map<String, String> headerMap = new HashMap<>();
    		headerMap.put(HttpClient.CONTENT_TYPE, HttpClient.APPLICATION_JSON);
    		HttpResult<String> hr = HttpClient.post(url, JsonUtil.toStr(paramMap), Charset.forName("UTF-8"));
    		if(hr.getStatusCode() != 200) {
    			log.warn("ApplePay receipt verify http not200, http status={}, content={}", hr.getStatusCode(), hr.getContent());
    			return null;
    		}
    		String receiptVerifyJsonStr = hr.getContent();
    		ReceiptVerifyResultDTO receiptVerifyDTO = JsonUtil.toObject(receiptVerifyJsonStr, ReceiptVerifyResultDTO.class);
    		if(receiptVerifyDTO.getStatus() != ReceiptVerifyStatus.STATUS_SUCCESS) {
				log.warn("ApplePay receipt verify status notSuccess, status={}", receiptVerifyDTO.getStatus());
    		}
    		long e = System.currentTimeMillis();
			log.warn("ApplePay cost : "+(e-s)+"ms");
    		return receiptVerifyDTO;
    	} catch (Exception e) {
			log.error("ApplePay receipt verify error.", e);
			return null;
    	}
    }
}
