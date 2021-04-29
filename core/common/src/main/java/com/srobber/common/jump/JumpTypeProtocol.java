package com.srobber.common.jump;

import com.srobber.common.exeption.ParamException;
import com.srobber.common.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 统一跳转协议构造
 * eg: _inner_jump_://localhost?jumpType=app&android=urlencode(androidData)&iOS=urlencode(iOSData)
 *
 * @author chensenlai
 */
public class JumpTypeProtocol {


    public static String getJumpUrl(BaseJumpTypeEnum type, Map<String, Object> params) {
        String param = type.getParamBuilder().build(params);
        String jumpUrl = "_inner_jump_://localhost?jumpType="+type.getJumpType();
        if(StringUtil.isNotBlank(param)) {
            jumpUrl += "&"+param;
        }
        return jumpUrl;
    }

    public static String encode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ParamException(e.getMessage());
        }
    }
}
