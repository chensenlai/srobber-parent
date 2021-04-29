package com.srobber.third.wechat.jsapi;

import com.srobber.common.util.DateUtil;
import com.srobber.common.util.HttpClient;
import com.srobber.common.util.JsonUtil;
import com.srobber.common.util.StringUtil;
import com.srobber.third.wechat.jsapi.model.JsapiSign;
import com.srobber.third.wechat.jsapi.model.TokenTicket;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chensenlai
 * 2020-12-11 上午11:31
 */
@Slf4j
@Data
public class WechatJsapiThirdImpl implements WechatJsapiThird {

    private String appid;
    private String appSecret;

    private static Map<String, String> CACHE = new HashMap<>(8);

    @Override
    public JsapiSign sign(String url) {
        int timestamp = (int) (DateUtil.nowMillis() / 1000);
        JsapiSign sign = new JsapiSign(timestamp, appid);
        String expire_time = CACHE.get("expire_time");
        if (StringUtil.isBlank(expire_time) || Integer.parseInt(expire_time)<timestamp) {
            synchronized (CACHE) {
                expire_time = CACHE.get("expire_time");
                if (StringUtil.isBlank(expire_time) || Integer.parseInt(expire_time)<timestamp) {
                    TokenTicket token = getToken();
                    if (null != token) {
                        CACHE.put("access_token", token.getAccess_token());
                        CACHE.put("expire_time", timestamp + token.getExpires_in()+"");
                        token = getTicket(token.getAccess_token());
                        if (null != token) {
                            CACHE.put("ticket", token.getTicket());
                        }
                    }
                }
            }
        }
        String signStr = "jsapi_ticket="+CACHE.get("ticket")
                +"&noncestr="+sign.getNoncestr()
                +"&timestamp="+sign.getTimestamp()+"&url="+url;
        sign.setSignature(getSignature(signStr));
        return sign;
    }

    private TokenTicket getToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token" + "?grant_type=client_credential&appid="
                + appid + "&secret=" + appSecret;
        try {
            HttpClient.HttpResult<String> result = HttpClient.get(url);
            if (result.getStatusCode()==200 && StringUtil.isNotBlank(result.getContent())) {
                String tokenTicketStr = result.getContent();
                log.info("wechat jsapi getToken: {}", tokenTicketStr);
                TokenTicket tt = JsonUtil.toObject(tokenTicketStr, TokenTicket.class);
                if (tt.getErrcode() == 0) {
                    return tt;
                }
            }
        } catch (Exception e) {
            log.error("wechat jsapi error.", e);
        }
        return null;
    }

    private TokenTicket getTicket(String token) {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+token+"&type=jsapi";
        try {
            HttpClient.HttpResult<String> result = HttpClient.get(url);
            if (result.getStatusCode()==200 && StringUtil.isNotBlank(result.getContent())) {
                String tokenTicketStr = result.getContent();
                log.info("wechat jsapi getTicket: {}", tokenTicketStr);
                TokenTicket tt = JsonUtil.toObject(tokenTicketStr, TokenTicket.class);
                if (tt.getErrcode() == 0) {
                    return tt;
                }
            }
        } catch (Exception e) {
            log.error("wechat jsapi getTicket error.", e);
        }
        return null;
    }

    private static String getSignature(String sKey) {
        try {
            String ciphertext = null;
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(sKey.toString().getBytes());
            ciphertext = byteToStr(digest);
            return ciphertext.toLowerCase();
        } catch (Exception e) {
            log.error("wechat jsapi getSignature error", e);
            return null;
        }
    }


    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }
}
