package com.srobber.third.aliyun.sts;

import com.srobber.third.aliyun.sts.model.AssumeRoleSts;

/**
 * 阿里云STS 帐号临时授权
 * https://www.alibabacloud.com/help/zh/doc-detail/28756.htm?spm=a2c63.p38356.879954.7.748b4ca9KZm3gf#reference-ong-5nv-xdb
 *
 * @author chensenlai
 * 2020-09-24 上午11:42
 */
public interface StsThird {
    /**
    String policy = "{\n" +
            "    \"Version\": \"1\", \n" +
            "    \"Statement\": [\n" +
            "        {\n" +
            "            \"Action\": [\n" +
            "                \"oss:*\"\n" +
            "            ], \n" +
            "            \"Resource\": [\n" +
            "                \"acs:oss:*:*:*\" \n" +
            "            ], \n" +
            "            \"Effect\": \"Allow\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
     **/

    /**
     * 扮演角色
     *
     * @param roleSessionName 角色会话名
     * RoleSessionName 是临时Token的会话名称，自己指定用于标识你的用户，主要用于审计，或者用于区分Token颁发给谁
     * 但是注意RoleSessionName的长度和规则，不要有空格，只能有'-' '_' 字母和数字等字符
     *
     * @param roleArn 指定角色的ARN。
     *                格式：acs:ram::$accountID:role/$roleName 。
     * @param policy 权限策略。(可为空)
     * 生成STS Token时可以指定一个额外的权限策略，以进一步限制STS Token的权限。
     * 若不指定则返回的Token拥有指定角色的所有权限。
     * 长度为1~1024个字符。
     *
     * @return 授权信息
     */
    AssumeRoleSts assumeRole(String roleSessionName, String roleArn, String policy);
}
