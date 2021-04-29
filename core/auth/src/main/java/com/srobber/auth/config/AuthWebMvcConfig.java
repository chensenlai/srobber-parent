package com.srobber.auth.config;

import com.srobber.auth.TokenCacheManager;
import com.srobber.auth.UserAgentInfoStore;
import com.srobber.auth.UserDeviceInfoStore;
import com.srobber.auth.UserLoginInfoStore;
import com.srobber.auth.spring.mvc.interceptor.AuthInterceptor;
import com.srobber.auth.spring.mvc.resolver.UserAgentMethodArgumentResolver;
import com.srobber.auth.spring.mvc.resolver.UserDeviceMethodArgumentResolver;
import com.srobber.auth.spring.mvc.resolver.UserLoginMethodArgumentResolver;
import com.srobber.auth.support.DefaultUserAgentInfoStore;
import com.srobber.auth.support.DefaultUserDeviceInfoStore;
import com.srobber.auth.support.DefaultUserLoginInfoStore;
import com.srobber.auth.support.TokenCacheManagerImpl;
import com.srobber.cache.Cache;
import com.srobber.cache.MultiCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 认证springMVC配置
 * @author chensenlai
 * 2021-04-28 上午10:55
 */
@Configuration
public class AuthWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserAgentMethodArgumentResolver userAgentMethodArgumentResolver;
    @Autowired
    private UserLoginMethodArgumentResolver userLoginMethodArgumentResolver;
    @Autowired
    private UserDeviceMethodArgumentResolver userDeviceMethodArgumentResolver;
    @Autowired
    private AuthInterceptor authInterceptor;

    /**
     * <p>自定义参数解析器:</p>
     * <ul>
     * <li>{@link UserAgentMethodArgumentResolver} 用户客户端参数解析</li>
     * <li>{@link UserLoginMethodArgumentResolver} 用户登录端参数解析</li>
     * </ul>
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userAgentMethodArgumentResolver);
        resolvers.add(userLoginMethodArgumentResolver);
        resolvers.add(userDeviceMethodArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .excludePathPatterns("/error", "/v2/api-docs**", "/swagger**", "/static/**", "/webjars/**",
                        "/outter/**",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/**/*.png",
                        "/**/*.jpeg",
                        "/**/*.jpg")
                .addPathPatterns("/**");
    }

    @Bean
    public UserAgentMethodArgumentResolver userAgentMethodArgumentResolver(UserAgentInfoStore userAgentInfoStore) {
        UserAgentMethodArgumentResolver userAgentMethodArgumentResolver = new UserAgentMethodArgumentResolver();
        userAgentMethodArgumentResolver.setUserAgentInfoStore(userAgentInfoStore);
        return userAgentMethodArgumentResolver;
    }

    @Bean
    public UserLoginMethodArgumentResolver userLoginMethodArgumentResolver(UserLoginInfoStore userLoginInfoStore) {
        UserLoginMethodArgumentResolver userLoginMethodArgumentResolver = new UserLoginMethodArgumentResolver();
        userLoginMethodArgumentResolver.setUserLoginInfoStore(userLoginInfoStore);
        return userLoginMethodArgumentResolver;
    }

    @Bean
    public UserDeviceMethodArgumentResolver userDeviceMethodArgumentResolver(UserDeviceInfoStore userDeviceInfoStore) {
        UserDeviceMethodArgumentResolver userDeviceMethodArgumentResolver = new UserDeviceMethodArgumentResolver();
        userDeviceMethodArgumentResolver.setUserDeviceInfoStore(userDeviceInfoStore);
        return userDeviceMethodArgumentResolver;
    }

    @Bean
    @ConditionalOnMissingBean(name="authInterceptor")
    public AuthInterceptor authInterceptor(UserAgentInfoStore userAgentInfoStore,
                                           UserLoginInfoStore userLoginInfoStore) {
        AuthInterceptor authInterceptor = new AuthInterceptor();
        authInterceptor.setUserAgentInfoStore(userAgentInfoStore);
        authInterceptor.setUserLoginInfoStore(userLoginInfoStore);
        return authInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean(name="tokenCacheManager")
    @Autowired(required = false)
    public TokenCacheManager tokenCacheManager(MultiCache multiCache) {
        TokenCacheManagerImpl tokenCacheManager = new TokenCacheManagerImpl();
        if(multiCache != null) {
            Cache remoteTokenCache = multiCache.choose("token");
            if(remoteTokenCache == null) {
                remoteTokenCache = multiCache.choose("default");
            }
            tokenCacheManager.setRemoteTokenCache(remoteTokenCache);
        }
        return tokenCacheManager;
    }

    @Bean
    @ConditionalOnMissingBean(name="userAgentInfoStore")
    public UserAgentInfoStore userAgentInfoStore() {
        DefaultUserAgentInfoStore userAgentInfoStore = new DefaultUserAgentInfoStore();
        return userAgentInfoStore;
    }

    @Bean
    @ConditionalOnMissingBean(name="userDeviceInfoStore")
    public UserDeviceInfoStore userDeviceInfoStore() {
        DefaultUserDeviceInfoStore userDeviceInfoStore = new DefaultUserDeviceInfoStore();
        return userDeviceInfoStore;
    }

    @Bean
    @ConditionalOnMissingBean(name="userLoginInfoStore")
    public UserLoginInfoStore userLoginInfoStore(TokenCacheManager tokenCacheManager) {
        DefaultUserLoginInfoStore userLoginInfoStore = new DefaultUserLoginInfoStore();
        userLoginInfoStore.setTokenCacheManager(tokenCacheManager);
        return userLoginInfoStore;
    }
}
