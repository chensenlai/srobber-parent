package com.srobber.third.apple.oauth.model;

import lombok.Data;

/**
 * 用户身份标识头部
 *
 * @author chensenlai
 * 2020-09-18 上午10:07
 */
@Data
public class IdentityTokenHeader {

    private String kid;
    private String alg;
}
