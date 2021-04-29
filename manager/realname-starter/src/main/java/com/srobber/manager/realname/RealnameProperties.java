package com.srobber.manager.realname;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 实人和活体客户端类型
 *
 * @author chensenlai
 * 2020-10-15 上午11:42
 */
@Data
@ConfigurationProperties(prefix = "srobber.manager.realname")
public class RealnameProperties {

    /**
     * 实人认证具体实现
     */
    private RealnameClientType type;
    /**
     * 百度实人认证
     */
    private final Baidu baidu = new Baidu();

    @Data
    public static class Baidu {

        private String appId;
        private String apiKey;
        private String secretKey;

        /**
         * 图片质量控制
         * NONE: 不进行控制
         * LOW:较低的质量要求
         * NORMAL: 一般的质量要求
         * HIGH: 较高的质量要求
         * 默认 NONE
         */
        private String qualityControl = "NORMAL";
        /**
         * 活体检测控制
         * NONE: 不进行控制
         * LOW:较低的活体要求(高通过率 低攻击拒绝率)
         * NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率)
         * HIGH: 较高的活体要求(高攻击拒绝率 低通过率)
         * 默认NONE
         */
        private String livenessControl = "NORMAL";

        /**
         * 与公安小图相似度可能性，用于验证生活照与公安小图是否为同一人，有正常分数时为[0~1]，
         * 推荐阈值0.8，超过即判断为同一人
         */
        private double score = 8.0;
    }
}
