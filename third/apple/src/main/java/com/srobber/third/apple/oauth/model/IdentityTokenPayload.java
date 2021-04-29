package com.srobber.third.apple.oauth.model;

import lombok.Data;

/**
 * 用户身份标识主体
 *
 * @author chensenlai
 * 2020-09-18 上午10:11
 */
@Data
public class IdentityTokenPayload {

    private String iss;
    private String aud;
    private String exp;
    private String sub;
    private String c_hash;
    private String auth_time;
}
