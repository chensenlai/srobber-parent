package com.srobber.common.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * 接口签名
 *
 * @author chensenlai
 */
public class SignatureUtil {

    public static final String SIGN = "_sign";
    public static final String TIMESTAMP = "_timestamp";
    public static final String SIGN_KEY = "_signKey";

    /**
     * APP接口参数签名
     * @param paramMap 请求参数
     * @param signKey 参数key
     * @return 签名字符串
     */
    public static String genSignature(final Map<String, String> paramMap, final String signKey) {
        String paramsStr = paramsStr(paramMap, signKey);
        return DigestUtil.md5Hex(paramsStr).toUpperCase();
    }

    public static String paramsStr(final Map<String, String> paramMap, final String signKey) {
        Set<String> keySet = paramMap.keySet();
        String[] keyArray = keySet.toArray(new String[0]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String key : keyArray) {
            //签名值，不参与签名
            if(key.equals(SIGN)) {
                continue;
            }
            // 参数值为null，不参与签名
            String value = paramMap.get(key);
            if(value == null) {
                continue;
            }
            sb.append(key).append("=").append(value).append("&");
        }
        sb.append(SIGN_KEY).append("=").append(signKey);
        return sb.toString();
    }
}
