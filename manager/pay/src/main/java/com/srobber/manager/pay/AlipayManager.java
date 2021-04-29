package com.srobber.manager.pay;

import com.srobber.manager.pay.alipay.dto.AlipayNotifyUrlDTO;
import com.srobber.manager.pay.alipay.dto.AlipayReturnUrlDTO;
import com.srobber.manager.pay.alipay.dto.AlipayTradeDTO;
import com.srobber.manager.pay.alipay.param.AlipayPayAppParam;
import com.srobber.manager.pay.alipay.param.AlipayPayPageParam;
import com.srobber.manager.pay.alipay.param.AlipayPayProgramParam;
import com.srobber.manager.pay.alipay.param.AlipayPayWapParam;

import java.util.Map;

/**
 * 支付宝支付管理器
 * 封装支付宝支付各种接口请求
 *
 * @author chensenlai
 */
public interface AlipayManager {

    /**
     * 统一下单 - APP支付
     * @param appPayParam 	app支付参数
     * @return 支付宝请求下单
     */
    String appUnified(AlipayPayAppParam appPayParam);

    /**
     * 统一下单 - 手机网站(支付宝生活号内嵌H5)
     * @param wapPayParam 	手机网站支付参数
     * @return 请求支付页面
     */
    String wapUnified(AlipayPayWapParam wapPayParam);

    /**
     * 统一下单 - 电脑网站支付
     * @param pagePayParam 	网页请求支付参数
     * @return 请求支付页面
     */
    String pageUnified(AlipayPayPageParam pagePayParam);

    /**
     * 统一下单 - 支付宝小程序
     * @param programPayParam 	小程序支付请求参数
     * @return 小程序客户端根据orderStr发起调用
     */
    String programUnified(AlipayPayProgramParam programPayParam);

    /**
     * 支付宝return_url地址回调参数获取
     * 如果参数校验不通过抛出运行时异常
     * @param params 支付回调参数
     * @return 通知数据
     */
    AlipayReturnUrlDTO checkAndGetReturnData(Map<String, String> params);

    /**
     * 支付宝notify_url地址回调参数获取,需要验证签名和数据有效性
     * 如果参数校验不通过抛出运行时异常
     * @param params 支付回调参数
     * @return 通知数据
     */
    AlipayNotifyUrlDTO checkAndGetNotifyData(Map<String, String>  params);

    /**
     * 根据外部交易号(业务方交易号)查询支付订单
     * @param outTradeNo 业务方交易号
     * @return 交易信息
     */
    AlipayTradeDTO queryByOutTradeNo(String outTradeNo);

    /**
     * 根据交易号(支付平台交易号)查询支付订单
     * @param tradeNo 支付平台交易号
     * @return 交易信息
     */
    AlipayTradeDTO queryByTradeNo(String tradeNo);

    /**
     * 根据交易号(支付平台交易号)关闭支付订单
     * @param tradeNo 支付平台交易号
     * @param operatorId 操作员编号
     * @return 操作结果
     */
    boolean close(String tradeNo, String operatorId);

    /**
     * 根据交易号(支付平台交易号)发起退款请求
     * @param tradeNo	支付平台交易号
     * @param refundFee	退款金额
     * @param operatorId 操作员编号
     * @return 操作结果
     */
    boolean refund(String tradeNo, int refundFee, String operatorId);
}
