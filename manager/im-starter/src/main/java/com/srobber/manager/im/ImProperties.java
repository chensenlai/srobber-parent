package com.srobber.manager.im;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * IM配置属性
 *
 * @author chensenlai
 * 2020-10-26 下午6:34
 */
@Data
@ConfigurationProperties(prefix = "srobber.manager.im")
public class ImProperties {

    /**
     * IM具体实现
     */
    private ImClientType type;

    /**
     * 融云配置
     */
    private Rongcloud rongcloud = new Rongcloud();

    @Data
    public static class Rongcloud {
        private String appKey;
        private String appSecret;
    }

}
