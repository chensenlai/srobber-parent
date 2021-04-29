package com.srobber.swagger.config;

import com.fasterxml.classmate.TypeResolver;
import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.srobber.swagger.CustomDocket;
import com.srobber.swagger.CustomModelPropertyBuilderPlugin;
import com.srobber.swagger.CustomOperationBuilderPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档配置
 *
 * @author chensenlai
 */
@Slf4j
@EnableSwagger2
@EnableSwaggerBootstrapUI
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket customDocket(){
        return new CustomDocket(DocumentationType.SWAGGER_2);
    }

    @Bean
    public OperationBuilderPlugin customOperationBuilderPlugin(TypeNameExtractor nameExtractor, TypeResolver resolver) {
        return new CustomOperationBuilderPlugin(nameExtractor, resolver);
    }

    @Bean
    public ModelPropertyBuilderPlugin customModelPropertyBuilderPlugin() {
        return new CustomModelPropertyBuilderPlugin();
    }
}
