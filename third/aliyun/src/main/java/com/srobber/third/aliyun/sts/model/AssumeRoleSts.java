package com.srobber.third.aliyun.sts.model;

import lombok.Data;

/**
 * 授权角色临时凭证
 *
 * @author chensenlai
 * 2020-09-24 上午11:38
 */
@Data
public class AssumeRoleSts {

    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private String expiration;
}
