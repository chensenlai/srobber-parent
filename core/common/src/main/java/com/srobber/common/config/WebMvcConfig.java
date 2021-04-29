package com.srobber.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srobber.common.enums.BaseEnum;
import com.srobber.common.spring.mvc.formatter.*;
import com.srobber.common.spring.mvc.interceptor.ExecuteInterceptor;
import com.srobber.common.util.BaseEnumUtil;
import com.srobber.common.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Date;
import java.util.List;

/**
 * SpringMVC配置管理
 *
 * @author chensenlai
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ExecuteInterceptor executeInterceptor;

    /**
     * <p>http消息自定义转化器</p>
     * <ul>
     * <li>1.BaseEnum子类型序列化(BaseEnum子类型返回客户端转成int类型),
     * 见{@link org.springframework.web.bind.annotation.ResponseBody}</li>
     * <li>2.BaseEnum子类型反序列化(int类型转成java内部对应BaseEnum子类型),
     * 见{@link org.springframework.web.bind.annotation.RequestBody}</li>
     * </ul>
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for(HttpMessageConverter<?> converter : converters) {
            if(converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter c = (MappingJackson2HttpMessageConverter)converter;
                ObjectMapper mapper = c.getObjectMapper();
                JsonUtil.initMapper(mapper);
            }
        }
    }

    /**
     * <p>参数自定义类型转化器:</p>
     * <ul>
     * <li>1.BaseEnum子类型反序列化(int类型转成java内部对应BaseEnum子类型),
     * 见{@link org.springframework.web.bind.annotation.RequestParam}
     * {@link org.springframework.web.bind.annotation.RequestHeader}
     * {@link org.springframework.web.bind.annotation.CookieValue}
     * {@link org.springframework.web.bind.annotation.PathVariable}</li>
     * <li>2.Integer</li>
     * <li>3.Long</li>
     * <li>4.Double</li>
     * <li>5.Float</li>
     * </ul>
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        for(final Class<? extends BaseEnum> enumClass : BaseEnumUtil.all()) {
            registry.addFormatterForFieldType(enumClass, new EnumFormatter<>(enumClass));
        }
        registry.addFormatterForFieldType(Integer.class, new IntegerFormatter());
        registry.addFormatterForFieldType(Long.class, new LongFormatter());
        registry.addFormatterForFieldType(Double.class, new DoubleFormatter());
        registry.addFormatterForFieldType(Float.class, new FloatFormatter());
        registry.addFormatterForFieldType(Date.class, new DateFormatter());
    }

    /**
     * 跨域配置
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/h5/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(3600L);
    }

    /**
     * 公用拦截器配置
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(executeInterceptor)
                .excludePathPatterns("/error", "/static/**", "/webjars/**",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/**/*.png",
                        "/**/*.jpeg",
                        "/**/*.jpg")
                .addPathPatterns("/**");
    }

    @Bean
    public ExecuteInterceptor getExecuteInterceptor() {
        return new ExecuteInterceptor();
    }
}
