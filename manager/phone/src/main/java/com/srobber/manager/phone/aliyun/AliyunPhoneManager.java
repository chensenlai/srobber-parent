package com.srobber.manager.phone.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dypnsapi.model.v20170525.GetMobileRequest;
import com.aliyuncs.dypnsapi.model.v20170525.GetMobileResponse;
import com.aliyuncs.dypnsapi.model.v20170525.VerifyMobileRequest;
import com.aliyuncs.dypnsapi.model.v20170525.VerifyMobileResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.srobber.manager.phone.PhoneException;
import com.srobber.manager.phone.PhoneManager;
import com.srobber.manager.phone.PhoneVerifyResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 号码一键获取
 * 本机号码验证
 *
 * @author chensenlai
 */
@Slf4j
@Data
public class AliyunPhoneManager implements PhoneManager {

    private static final String CODE_OK = "OK";

    /**
     * 一致
     */
    public static final String VERIFY_RESULT_PASS = "PASS";
    /**
     * 不一致
     */
    public static final String VERIFY_RESULT_REJECT = "REJECT";
    /**
     * 无法判断
     */
    public static final String VERIFY_RESULT_UNKNOWN = "UNKNOWN";


    private String regionId = "cn-hangzhou";
    private String accessKeyId;
    private String accessSecret;

    @Override
    public String getPhone(String token) {
        DefaultProfile profile = DefaultProfile.getProfile(this.regionId, this.accessKeyId, this.accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);
        try {
            GetMobileRequest request = new GetMobileRequest();
            request.setAccessToken(token);
            GetMobileResponse response = client.getAcsResponse(request);
            if(!Objects.equals(response.getCode(), CODE_OK)) {
                log.warn("phone getPhone error, {} {} {}", response.getRequestId(), response.getCode(), response.getMessage());
                throw new PhoneException("号码获取失败");
            }
            GetMobileResponse.GetMobileResultDTO getMobileResultDTO = response.getGetMobileResultDTO();
            return getMobileResultDTO.getMobile();
        } catch (ClientException e) {
            String requestId = e.getRequestId();
            String errorCode = e.getErrCode();
            String errorMessage = e.getErrMsg();
            log.error("phone getPhone error, {} {} {}", requestId, errorCode, errorMessage, e);
            throw new PhoneException("号码获取失败,错误码:"+errorCode+",错误消息:"+errorMessage);
        } finally {
            client.shutdown();
        }
    }

    @Override
    public PhoneVerifyResult verifyPhone(String token, String phone) {
        DefaultProfile profile = DefaultProfile.getProfile(this.regionId, this.accessKeyId, this.accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);
        try {
            VerifyMobileRequest request = new VerifyMobileRequest();
            request.setAccessCode(token);
            request.setPhoneNumber(phone);
            VerifyMobileResponse response = client.getAcsResponse(request);
            if(!Objects.equals(response.getCode(), CODE_OK)) {
                log.warn("phone verifyPhone error, {} {} {}", response.getRequestId(), response.getCode(), response.getMessage());
                throw new PhoneException("号码校验失败");
            }
            VerifyMobileResponse.GateVerifyResultDTO gateVerifyResultDTO = response.getGateVerifyResultDTO();
            String verifyResult = gateVerifyResultDTO.getVerifyResult();
            return PhoneVerifyResult.ofAliyunVerifyResult(verifyResult);
        } catch (ClientException e) {
            String requestId = e.getRequestId();
            String errorCode = e.getErrCode();
            String errorMessage = e.getErrMsg();
            log.error("phone verifyPhone error, {} {} {}", requestId, errorCode, errorMessage, e);
            throw new PhoneException("号码校验失败,错误码:"+errorCode+",错误消息:"+errorMessage);
        }
    }
}
