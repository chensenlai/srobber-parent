package com.srobber.manager.pay;

import com.srobber.manager.pay.wxpay.dto.*;
import com.srobber.manager.pay.wxpay.param.*;

import java.util.Map;

/**
 * 微信支付管理器
 * 封装微信支付请求各个接口
 *
 * @author chensenlai
 */
public interface WxpayManager {

    /**
     * 统一下单 - APP支付
     * @param appPayParam 	app支付参数
     * @return
     */
    WxpayUnifiedAppDTO appUnified(WxpayAppPayParam appPayParam);

    /**
     * 统一下单 - 微信公众号(JSAPI)
     * 备注： 微信5.0版本后才加入微信支付模块（可以通过判断userAgent判断微信客户端版本号）
     * @param mpPayParam	jspapi支付参数
     * @return
     */
    WxpayUnifiedMPDTO mpUnified(WxpayMpPayParam mpPayParam);

    /**
     * 统一下单 - H5手机网站
     * @param h5PayParam
     * @return
     */
    WxpayUnifiedH5DTO h5Unified(WxpayH5PayParam h5PayParam);

    /**
     * 统一下单 -微信小程序
     * @param programPayParam
     * @return
     */
    WxpayUnifiedProgramDTO programUnified(WxpayProgramPayParam programPayParam);

    /**
     * 统一下单 - 扫码支付
     * @param nativePayParam
     * @return
     */
    WxpayUnifiedNativeDTO nativeUnified(WxpayNativePayParam nativePayParam);

    /**
     * 微信notify_url地址回调参数获取
     * 如果参数校验不通过抛出运行时异常
     * @param params
     * @return
     */
    WxpayNotifyUrlDTO checkAndGetNotifyData(Map<String, String> params);
}
