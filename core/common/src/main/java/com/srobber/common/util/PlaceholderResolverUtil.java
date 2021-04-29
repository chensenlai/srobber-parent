package com.srobber.common.util;

import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Map;
import java.util.Properties;

/**
 * 占位符${}解析器
 *
 * @author chensenlai
 */
public class PlaceholderResolverUtil {

    /**
     * 占位符解析器 ${}
     * @param templateContent 模板内容
     * @param templateParamMap 模板参数
     * @return 解析后的模板内容
     */
    public static String resolver(String templateContent, Map<String, String> templateParamMap) {
        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");
        Properties properties = new Properties();
        if(templateParamMap != null) {
            for(Map.Entry<String, String> entry : templateParamMap.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
        }
        return helper.replacePlaceholders(templateContent, properties);
    }

    private PlaceholderResolverUtil(){}
}
