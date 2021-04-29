package com.srobber.manager.realname;

import com.srobber.manager.realname.baidu.BaiduRealnameManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 实人和活体自动配置
 *
 * @author chensenlai
 * 2020-10-15 上午11:40
 */
@Configuration
@EnableConfigurationProperties(RealnameProperties.class)
public class RealnameAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="realnameManager")
    @ConditionalOnProperty(prefix = "srobber.manager.realname", name = "type", havingValue = "baidu", matchIfMissing = true)
    public RealnameManager baiduRealnameManager(RealnameProperties realnameProperties) {
        RealnameProperties.Baidu baidu = realnameProperties.getBaidu();
        BaiduRealnameManager realnameManager = new BaiduRealnameManager();
        realnameManager.setAppId(baidu.getAppId());
        realnameManager.setApiKey(baidu.getApiKey());
        realnameManager.setSecretKey(baidu.getSecretKey());
        realnameManager.setQualityControl(baidu.getQualityControl());
        realnameManager.setLivenessControl(baidu.getLivenessControl());
        realnameManager.setScore(baidu.getScore());
        realnameManager.init();
        return realnameManager;
    }
}
