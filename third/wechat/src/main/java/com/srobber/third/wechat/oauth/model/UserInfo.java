package com.srobber.third.wechat.oauth.model;

import lombok.Data;

/**
 * 用户信息
 *
 * @author chensenlai
 * 2020-09-17 下午7:43
 */
@Data
public class UserInfo {

    private String openid;
    private String nickname;
    /**
     * 1-男  2-女 其他-未知
     */
    private int sex;
    private String headimgurl;
    private String city;
    private String province;
    private String country;
    private String unionid;
}
