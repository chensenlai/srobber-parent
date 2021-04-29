package com.srobber.third.apple.oauth.model;

import lombok.Data;

/**
 * 苹果公钥
 *
 * @author chensenlai
 * 2020-09-18 上午9:53
 */
@Data
public class AppleKey {

    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;
}
