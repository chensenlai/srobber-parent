package com.srobber.third.apple.oauth.model;

import lombok.Data;

import java.util.List;

/**
 * 苹果公钥列表
 * @author chensenlai
 * 2020-11-23 下午4:23
 */
@Data
public class AppleKeys {

    private List<AppleKey> keys;
}
