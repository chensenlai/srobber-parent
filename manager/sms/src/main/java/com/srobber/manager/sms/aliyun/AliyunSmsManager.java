package com.srobber.manager.sms.aliyun;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.srobber.common.util.JsonUtil;
import com.srobber.manager.sms.SmsException;
import com.srobber.manager.sms.SmsManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 阿里云短信客户端
 *
 * @author chensenlai
 */
@Slf4j
@Data
public class AliyunSmsManager implements SmsManager {

    private String accessKeyId;
    private String accessSecret;
    private String signName;
    private String domain = "dysmsapi.aliyuncs.com";
    private String version = "2017-05-25";
    private String regionId = "cn-hangzhou";

    @Override
    public boolean send(String phone, String templateId,
                        String templateContent, Map<String, String> templateParamMap) {

        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateId);
        request.putQueryParameter("TemplateParam", JsonUtil.toStr(templateParamMap));
        try {
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            if(data == null || !data.contains("OK")) {
                log.warn("sms send {} error. {} {}", phone, response.getHttpStatus(), data);
                return false;
            }
            log.info("sms send {} ok.", phone);
            return true;
        } catch (ClientException e) {
            String requestId = e.getRequestId();
            String errorCode = e.getErrCode();
            String errorMessage = e.getErrMsg();
            log.error("sms send error, {} {} {} {}", phone, requestId, errorCode, errorMessage, e);
            throw new SmsException("短信发送失败,错误码:"+errorCode+",错误消息:"+errorMessage);
        } finally {
            client.shutdown();
        }
    }

    @Override
    public boolean batchSend(List<String> phoneList, String templateId,
                             String templateContent, List<Map<String, String>> templateParamMapList) {

        List<String> signNameList = new ArrayList<>(phoneList.size());
        for(int i=0, len=phoneList.size(); i<len; i++) {
            signNameList.add(signName);
        }

        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("SendBatchSms");
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumberJson", JsonUtil.toStr(phoneList));
        request.putQueryParameter("SignNameJson", JsonUtil.toStr(signNameList));
        request.putQueryParameter("TemplateCode", templateId);
        request.putQueryParameter("TemplateParamJson", JsonUtil.toStr(templateParamMapList));
        try {
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            if(data == null || !data.contains("OK")) {
                log.warn("sms batchSend {} error. {} {}", Arrays.toString(phoneList.toArray()), response.getHttpStatus(), data);
                return false;
            }
            log.info("sms batchSend {} ok.", Arrays.toString(phoneList.toArray()));
            return true;
        } catch (ClientException e) {
            String requestId = e.getRequestId();
            String errorCode = e.getErrCode();
            String errorMessage = e.getErrMsg();
            log.error("sms batchSend error, {} {} {} {}", Arrays.toString(phoneList.toArray()), requestId, errorCode, errorMessage, e);
            throw new SmsException("短信发送失败,错误码:"+errorCode+",错误消息:"+errorMessage);
        } finally {
            client.shutdown();
        }
    }
}
