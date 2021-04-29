package com.srobber.common.util;

/**
 * 跨域脚本攻击, 过滤掉可能的XSS脚本
 *
 * @author chensenlai
 */
public class XssFilterUtil {

    /**
     * 过滤可能XSS攻击
     * @param value 原始过滤字符串
     * @return 过滤后字符串
     */
    public static String filterXss(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("<","&lt;");
        value = value.replaceAll(">","&gt;");
        value = value.replaceAll("'","&apos;");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("(?i)<script.*?>.*?<script.*?>", "");
        value = value.replaceAll("(?i)<script.*?>.*?</script.*?>", "");
        value = value.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "");
        value = value.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", "");
        return value;
    }

}
