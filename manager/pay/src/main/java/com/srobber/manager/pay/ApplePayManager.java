package com.srobber.manager.pay;

import com.srobber.manager.pay.apple.dto.ReceiptVerifyResultDTO;

/**
 * 苹果支付管理器
 *
 * @author chensenlai
 * 2020-10-12 下午3:57
 */
public interface ApplePayManager {

    /**
     * 苹果票据校验
     * @param receiptData 票据
     * @return 验证结果
     */
    ReceiptVerifyResultDTO verifyReceipt(String receiptData);
}
