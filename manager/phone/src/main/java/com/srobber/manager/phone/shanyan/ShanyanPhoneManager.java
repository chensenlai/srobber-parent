package com.srobber.manager.phone.shanyan;

import com.srobber.common.util.HttpClient;
import com.srobber.common.util.JsonUtil;
import com.srobber.common.util.WebUtil;
import com.srobber.manager.phone.PhoneException;
import com.srobber.manager.phone.PhoneManager;
import com.srobber.manager.phone.PhoneVerifyResult;
import com.srobber.manager.phone.shanyan.utils.AESUtils;
import com.srobber.manager.phone.shanyan.utils.MD5;
import com.srobber.manager.phone.shanyan.utils.RSAUtils;
import com.srobber.manager.phone.shanyan.utils.SignUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 号码一键获取
 * 本机号码验证
 * http://shanyan.253.com/document/details?lid=300&cid=93&pc=28&pn=%25E9%2597%25AA%25E9%25AA%258CSDK
 * https://shanyan.253.com/document/details?lid=302&cid=94&pc=28&pn=%25E9%2597%25AA%25E9%25AA%258CSDK
 *
 * @author chensenlai
 */
@Slf4j
@Data
public class ShanyanPhoneManager implements PhoneManager {

    /**
     * 一键登陆响应码
     */
    private static final String FLASH_LOGIN_URL = "https://api.253.com/open/flashsdk/mobile-query";
    /**
     * 本机号码校验响应码
     */
    private static final String FLASH_VALIDATE_URL = "https://api.253.com/open/flashsdk/mobile-validate";

    /**
     * 成功响应码
     */
    private static final String CODE_OK = "200000";
    /**
     * 一致
     */
    public static final String VERIFY_RESULT_PASS = "1";
    /**
     * 不一致
     */
    public static final String VERIFY_RESULT_REJECT = "0";

    /**
     * 应用对应的闪验APPID
     */
    private String appId;
    /**
     * 应用对应的闪验APPKEY
     */
    private String appKey;
    /**
     * 手机号加解密方式 0 AES 1 RSA , 可以不传，不传则手机号解密直接使用AES解密
     */
    private String encryptType = "0";
    /**
     * 创建应用时填入的rsa公钥对应的私钥字符串
     */
    private String privateKey = "";

    @Override
    public String getPhone(String token) {
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("token", token);
            params.put("appId", this.appId);
            //可以不传，不传则解密直接使用AES解密
            params.put("encryptType", this.encryptType);
            params.put("sign", SignUtils.getSign(params, this.appKey));
            HttpClient.HttpResult<String> httpResult = HttpClient.post(FLASH_LOGIN_URL, WebUtil.getParameterStr(params));
            if(!WebUtil.isHttpSuccessful(httpResult.getStatusCode())) {
                log.warn("phone getPhone error, http not 200 {} {}", httpResult.getStatusCode(), httpResult.getContent());
                throw new PhoneException("号码获取失败, 网络异常");
            }
            String content = httpResult.getContent();
            GetPhoneResult getPhoneResult = JsonUtil.toObject(content, GetPhoneResult.class);
            String code = getPhoneResult.getCode();
            String message = getPhoneResult.getMessage();
            if (!Objects.equals(code, CODE_OK)) {
                log.warn("phone getPhone error, code not 200000 {} {}", code, message);
                throw new PhoneException("号码获取失败, 响应异常");
            }
            GetPhoneResult.Phone phone = getPhoneResult.getData();
            String mobile = phone.getMobileName();
            if (Objects.equals(this.encryptType, "0")) {
                String key = MD5.getMD5Code(this.appKey);
                mobile = AESUtils.decrypt(mobile, key.substring(0, 16), key.substring(16));
            } else if (Objects.equals(this.encryptType, "1")) {
                mobile = RSAUtils.decryptByPrivateKeyForLongStr(mobile, this.privateKey);
            }
            return mobile;
        } catch (Exception e) {
            log.warn("phone getPhone error", e);
            throw new PhoneException("号码获取失败");
        }
    }

    @Override
    public PhoneVerifyResult verifyPhone(String token, String phone) {
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("token", token);
            params.put("appId", this.appId);
            params.put("mobile", phone);
            params.put("sign", SignUtils.getSign(params, this.appKey));
            HttpClient.HttpResult<String> httpResult = HttpClient.post(FLASH_VALIDATE_URL, WebUtil.getParameterStr(params));
            if(!WebUtil.isHttpSuccessful(httpResult.getStatusCode())) {
                log.warn("phone verifyPhone error, http not 200 {} {}", httpResult.getStatusCode(), httpResult.getContent());
                throw new PhoneException("号码校验失败, 网络异常");
            }
            String content = httpResult.getContent();
            VerifyPhoneResult verifyPhoneResult  = JsonUtil.toObject(content, VerifyPhoneResult.class);
            String code = verifyPhoneResult.getCode();
            String message = verifyPhoneResult.getMessage();
            if (!Objects.equals(code, CODE_OK)) {
                log.warn("phone verifyPhone error, code not 200000 {} {}", code, message);
                throw new PhoneException("号码校验失败, 响应异常");
            }
            VerifyPhoneResult.Verify verify = verifyPhoneResult.getData();
            String isVerify = verify.getIsVerify();
            return PhoneVerifyResult.ofShanyanVerifyResult(isVerify);
        } catch (Exception e) {
            log.warn("phone verifyPhone error", e);
            throw new PhoneException("号码校验失败");
        }
    }
}
