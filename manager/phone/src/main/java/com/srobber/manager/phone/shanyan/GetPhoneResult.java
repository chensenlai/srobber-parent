package com.srobber.manager.phone.shanyan;

import lombok.Data;

/**
 * 闪验取号码响应结构体
 *
 * @author chensenlai
 */
@Data
public class GetPhoneResult {

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
    private Phone data;

    @Data
    public static class Phone {
        /**
         * 手机号密文 ，根据传入的encryptType值选择对应算法解密手机号。
         */
        private String mobileName;
        /**
         * 闪验的交易流水号
         */
        private String tradeNo;
    }
}
