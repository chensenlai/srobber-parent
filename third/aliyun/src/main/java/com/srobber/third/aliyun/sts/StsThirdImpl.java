package com.srobber.third.aliyun.sts;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.srobber.common.exeption.WrapException;
import com.srobber.third.aliyun.sts.model.AssumeRoleSts;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云STS 临时访问权限管理服务。
 * 提供客户端临时token, 直接访问阿里云基础设施
 *
 * 基本概念:
 *
 * RAM角色（RAM role）
 * 一种虚拟的RAM用户。
 *
 * 角色ARN（Role ARN）
 * 角色ARN是角色的资源名称（Aliyun Resource Name，简称ARN）。角色ARN全局唯一，用来指定具体的RAM角色。ARN遵循阿里云的命名规范，格式为：acs:ram::$accountID:role/$roleName。
 *
 * 可信实体（Trusted entity）
 * RAM角色的可信实体是指可以扮演角色的实体用户身份。创建角色时必须指定可信实体，RAM角色只能被受信的实体扮演。可信实体可以是阿里云账号、阿里云服务或身份提供商。
 *
 * 扮演角色（Assume role）
 * 扮演角色是实体用户获取角色身份的安全令牌的方法。一个实体用户调用STS API AssumeRole可以获得角色的安全令牌，使用安全令牌可以访问云服务API。
 *
 * @author chensenlai
 * 2020-09-24 上午11:42
 */
@Slf4j
@Data
public class StsThirdImpl implements StsThird {

    /**
     * assumeRole权限用户accessKeyId
     */
    private String accessKeyId;
    /**
     * assumeRole权限用户accessKeyId
     */
    private String accessKeySecret;

    @Override
    public AssumeRoleSts assumeRole(String roleSessionName, String roleArn, String policy) {

        try {
            //构造default profile（参数留空，无需添加Region ID）
            IClientProfile profile = DefaultProfile.getProfile("", accessKeyId, accessKeySecret);
            //用profile构造client
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysEndpoint("sts.aliyuncs.com");
            request.setSysMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy);
            final AssumeRoleResponse response = client.getAcsResponse(request);
            AssumeRoleResponse.Credentials credentials = response.getCredentials();

            AssumeRoleSts sts = new AssumeRoleSts();
            sts.setExpiration(credentials.getExpiration());
            sts.setAccessKeyId(credentials.getAccessKeyId());
            sts.setAccessKeySecret(credentials.getAccessKeySecret());
            sts.setSecurityToken(credentials.getSecurityToken());
            return sts;
        } catch (ClientException e) {
            log.warn("sts roleSessionName {} grant failed, Error code: {}, Error message: {}, RequestId: {}",
                    roleSessionName, e.getErrCode(), e.getErrCode(), e.getRequestId());
            throw new WrapException(e);
        }
    }
}
