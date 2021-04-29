package com.srobber.manager.phone.shanyan;

import lombok.Data;

/**
 * 本机号码校验结果
 *
 * @author chensenlai
 */
@Data
public class VerifyPhoneResult {

    /**
     * 200000表示成功，其他代码都为失败，详情参考附录。
     */
    private String code;
    /**
     * 响应代码描述
     */
    private String message;
    /**
     * 是否收费，枚举值：1:收费/0:不收费
     */
    private int chargeStatus;
    /**
     * 数据内容
     */
    private Verify data;

    @Data
    public static class Verify {
        /**
         * 值：1 是本机号码 0 非本机号码
         */
        private String isVerify;
        /**
         * 闪验的交易流水号
         */
        private String tradeNo;
    }
}
