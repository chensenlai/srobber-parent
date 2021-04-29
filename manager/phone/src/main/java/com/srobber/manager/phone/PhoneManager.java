package com.srobber.manager.phone;

/**
 * 号码一键获取
 * 本机号码验证
 *
 * @author chensenlai
 */
public interface PhoneManager {

    /**
     * 根据运营商token获取手机号码
     * @param token 运营商token
     * @return 手机号码
     */
    String getPhone(String token);

    /**
     * 校验本机号码
     * @param token 运营商token
     * @param phone 手机号码
     * @return 验证结果
     */
    PhoneVerifyResult verifyPhone(String token, String phone);
}
