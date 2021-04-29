package com.srobber.swagger;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Predicates;
import com.srobber.common.config.CoreConfig;
import com.srobber.common.spring.EnvironmentContext;
import com.srobber.common.status.CommonStatus;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义Docket
 *
 * @author chensenlai
 */
public class CustomDocket extends Docket {

    public static boolean AUTH_PRESENT = false;
    private static Class<?> USER_AGENT_INFO_CLAZZ = null;
    private static Class<?> USER_LOGIN_INFO_CLAZZ = null;


    static {
        ClassLoader classLoader = CustomDocket.class.getClassLoader();
        AUTH_PRESENT = ClassUtils.isPresent("com.srobber.auth.UserAgentInfo", classLoader)
                        && ClassUtils.isPresent("com.srobber.auth.UserLoginInfo", classLoader);
        if(AUTH_PRESENT) {
            try {
                USER_AGENT_INFO_CLAZZ = ClassUtils.forName("com.srobber.auth.UserAgentInfo", classLoader);
                USER_LOGIN_INFO_CLAZZ = ClassUtils.forName("com.srobber.auth.UserLoginInfo", classLoader);
            } catch (ClassNotFoundException e) {
                ;
            }
        }
    }

    public CustomDocket(DocumentationType documentationType) {
        super(documentationType);
        //全局参数
        List<Parameter> parameters = new ArrayList<>(2);
        if(AUTH_PRESENT) {
            //1.客户端user-agent
            Parameter userAgentParameter = new ParameterBuilder()
                    .parameterType("header")
                    .name(CoreConfig.SECURITY_HEADER_USER_AGENT)
                    .defaultValue("Swagger#Swagger#ISP#Swagger-UI#100")
                    .modelRef(new ModelRef("string"))
                    .description("客户端系统iOS/Android/H5/PC#应用市场#运营商#客户端信息#app版本号(int)")
                    .required(true)
                    .build();
            parameters.add(userAgentParameter);

            //2.客户端设备唯一码
            Parameter deviceParameter = new ParameterBuilder()
                    .parameterType("header")
                    .name(CoreConfig.SECURITY_HEADER_DEVICE)
                    .defaultValue("123456")
                    .modelRef(new ModelRef("string"))
                    .description("设备唯一码")
                    .required(false)
                    .build();
            parameters.add(deviceParameter);
        }

        //自定义响应表
        CommonStatus[] statuses = CommonStatus.values();
        List<ResponseMessage> responseMessages = new ArrayList<>(statuses.length);
        for(CommonStatus status : statuses) {
            ResponseMessage message = new ResponseMessageBuilder()
                    .code(status.getCode())
                    .message(status.getMsg())
                    .build();
            responseMessages.add(message);
        }

        // 错误路径不监控
        // 对根下所有路径进行监控
        this.select()
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .paths(PathSelectors.regex("/.*"))
                .build();
        Docket docket = this.apiInfo(apiInfo());
        docket.useDefaultResponseMessages(false)
            //.alternateTypeRules(AlternateTypeRules.newRule(
            //        typeResolver.resolve(QueryPage.class),
            //        typeResolver.resolve(QueryPageTemplate.class)))
            .globalResponseMessage(RequestMethod.GET, responseMessages)
            .globalResponseMessage(RequestMethod.POST, responseMessages)
            .globalResponseMessage(RequestMethod.PUT, responseMessages)
            .globalResponseMessage(RequestMethod.PATCH, responseMessages)
            .globalResponseMessage(RequestMethod.DELETE, responseMessages)

            .globalOperationParameters(parameters)

            .ignoredParameterTypes(Page.class)
            ;
        if(USER_AGENT_INFO_CLAZZ != null) {
            docket.ignoredParameterTypes(USER_AGENT_INFO_CLAZZ);
        }
        if(USER_LOGIN_INFO_CLAZZ != null) {
            docket.ignoredParameterTypes(USER_LOGIN_INFO_CLAZZ);
        }

        //生产环境屏蔽swagger
        if(EnvironmentContext.isProdEnv()){
            this.enable(false);
        }
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("接口文档")
                .version("1.0.0")
                .build();
        return apiInfo;
    }
}
