package com.srobber.common.jump.support;

import com.srobber.common.exeption.ParamException;
import com.srobber.common.jump.BaseJumpTypeEnum;
import com.srobber.common.jump.BaseParamBuilder;
import com.srobber.common.jump.ParamDefinition;
import com.srobber.common.jump.JumpTypeProtocol;
import com.srobber.common.util.JsonUtil;
import com.srobber.common.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 统一跳转类型定义
 *
 * @author chensenlai
 */
@Getter
@AllArgsConstructor
public enum JumpTypeEnum implements BaseJumpTypeEnum {

    /**
     * 不需要跳转类型
     */
    None(1, "不需要跳转类型",
            "none",
            Collections.emptyList(),
            (params)->{return null;}),

    /**
     * H5跳转类型
     */
    H5(2, "H5页面统一跳转",
            "H5",
            Arrays.asList(new ParamDefinition("url", "网页地址", true)),
            (params)->{
                String url = (String)params.get("url");
                if(StringUtil.isBlank(url)) {
                    throw new ParamException("网页地址为空");
                }
                boolean isHttp = false;
                final String[] httpSchemas = new String[]{"http", "https"};
                for(String httpSchema : httpSchemas) {
                    if(url.startsWith(httpSchema)) {
                        isHttp = true;
                        break;
                    }
                }
                if(!isHttp) {
                    throw new ParamException("跳转地址"+url+"协议不支持, 目前支持："+Arrays.toString(httpSchemas));
                }
                return "url="+ JumpTypeProtocol.encode(url);
            }),

    /**
     * 微信小程序跳转类型
     */
    Miniptogram(3, "微信小程序统一跳转",
            "miniptogram",
                Arrays.asList(new ParamDefinition("appId", "微信小程序地址", true)),
                (params)->{
                    String appId = (String)params.get("appId");
                    if(StringUtil.isBlank(appId)) {
                        throw new ParamException("微信小程序为空");
                    }
                    return "appId="+JumpTypeProtocol.encode(appId);
                }),

    App(4, "APP页面",
            "app",
            Arrays.asList(new ParamDefinition("android", "安卓跳转参数", true),
                    new ParamDefinition("iOS", "iOS跳转参数", true)
            ),
            (jumpParams) -> {
                StringBuilder paramBuf = new StringBuilder();
                Object androidParam = jumpParams.get("android");
                if(androidParam != null) {
                    if(androidParam instanceof String) {
                        paramBuf.append("android="+ androidParam);
                    } else {
                        paramBuf.append("android="+ JumpTypeProtocol.encode(JsonUtil.toStr(androidParam)));
                    }
                }

                Object iOSParam = jumpParams.get("iOS");
                if(iOSParam != null) {
                    if(paramBuf.length()>1) {
                        paramBuf.append("&");
                    }
                    if(iOSParam instanceof String) {
                        paramBuf.append("iOS="+ iOSParam);
                    } else {
                        paramBuf.append("iOS="+ JumpTypeProtocol.encode(JsonUtil.toStr(iOSParam)));
                    }
                }
                return paramBuf.toString();
            })
    ;

    private int num;
    private String name;
    private String jumpType;
    private List<ParamDefinition> paramDefinitions;
    private BaseParamBuilder paramBuilder;
}
