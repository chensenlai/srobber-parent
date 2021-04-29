package com.srobber.third.apple.oauth.model;

import lombok.Data;

/**
 * 用户身份信息
 *
 * @author chensenlai
 * 2020-09-18 上午10:25
 */
@Data
public class UserIdentity {

    private String sub;
    private String email;
}
