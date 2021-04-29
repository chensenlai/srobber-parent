package com.srobber.manager.sms;

import java.util.List;
import java.util.Map;

/**
 * 手机发送短信
 *
 * @author chensenlai
 */
public interface SmsManager {

    /**
     * 单个短信发送
     * @param phone 手机号码
     * @param templateId 模板编号
     * @param templateContent 模板内容, ${}作为占位符
     * @param templateParamMap 模板参数
     * @return 发送结果
     */
    boolean send(String phone, String templateId,
                 String templateContent, Map<String, String> templateParamMap);


    /**
     * 批量短信发送
     * @param phoneList 手机号码列表
     * @param templateId 模板编号
     * @param templateContent 模板内容, ${}作为占位符
     * @param templateParamMapList 模板参数
     * @return 发送结果
     */
    boolean batchSend(List<String> phoneList, String templateId,
                      String templateContent, List<Map<String, String>> templateParamMapList);
}
