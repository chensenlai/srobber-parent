package com.srobber.manager.phone;

import com.srobber.common.enums.BaseEnum;
import com.srobber.manager.phone.aliyun.AliyunPhoneManager;
import com.srobber.manager.phone.shanyan.ShanyanPhoneManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 号码校验结果
 *
 * @author chensenlai
 */
@Getter
@AllArgsConstructor
public enum PhoneVerifyResult implements BaseEnum {

    /**
     * 一致
     */
    Pass(1, "一致"),
    /**
     * 不一致
     */
    Reject(2, "不一致"),
    /**
     * 不确定
     */
    Unknown(3, "不确定"),
    ;

    public static PhoneVerifyResult ofAliyunVerifyResult(String verifyResult) {
        PhoneVerifyResult result = PhoneVerifyResult.Unknown;
        if(Objects.equals(verifyResult, AliyunPhoneManager.VERIFY_RESULT_PASS)) {
            result = PhoneVerifyResult.Pass;
        } else if(Objects.equals(verifyResult, AliyunPhoneManager.VERIFY_RESULT_REJECT)) {
            result = PhoneVerifyResult.Reject;
        }
        return result;
    }

    public static PhoneVerifyResult ofShanyanVerifyResult(String isVerify) {
        PhoneVerifyResult result = PhoneVerifyResult.Unknown;
        if(Objects.equals(isVerify, ShanyanPhoneManager.VERIFY_RESULT_PASS)) {
            result = PhoneVerifyResult.Pass;
        } else if(Objects.equals(isVerify, ShanyanPhoneManager.VERIFY_RESULT_REJECT)) {
            result = PhoneVerifyResult.Reject;
        }
        return result;
    }

    private int num;
    private String name;
}
