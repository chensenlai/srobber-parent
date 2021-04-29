package com.srobber.manager.realname;

/**
 * 实名制活体认证管理器
 * (身份证和活体对比认证)
 *
 * @author chensenlai
 */
public interface RealnameManager {

    /**
     * 活体校验
     * 校验活体和身份证和名字是否对应
     * @param realName 姓名
     * @param idCard 身份证
     * @param imgUrl 图片地址(全路径, 百度限制10M)
     * @return 校验结果
     */
    RealnameResultEnum personVerify(String realName, String idCard, String imgUrl);

}
