package com.srobber.swagger;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.srobber.common.config.CoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.ResolvedTypes;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.List;

/**
 * 方法构建操作增强
 * 1 分页参数显示
 * 2 枚举值用int表示
 * 3 通过是否要登陆限制
 *
 * @author chensenlai
 */
public class CustomOperationBuilderPlugin implements OperationBuilderPlugin {

    private final TypeNameExtractor nameExtractor;
    private final TypeResolver resolver;
    private final ResolvedType pageType;

    @Autowired
    public CustomOperationBuilderPlugin(TypeNameExtractor nameExtractor, TypeResolver resolver) {
        this.nameExtractor = nameExtractor;
        this.resolver = resolver;
        this.pageType = resolver.resolve(Page.class);
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }

    @Override
    public void apply(OperationContext context) {
        List<ResolvedMethodParameter> methodParameters = context.getParameters();
        List<Parameter> parameters = Lists.newArrayList();
        //分页参数处理
        for (ResolvedMethodParameter methodParameter : methodParameters) {
            ResolvedType resolvedType = methodParameter.getParameterType();
            if (pageType.getErasedType().equals(resolvedType.getErasedType())) {
                ParameterContext parameterContext = new ParameterContext(methodParameter,
                        new ParameterBuilder(),
                        context.getDocumentationContext(),
                        context.getGenericsNamingStrategy(),
                        context);
                Function<ResolvedType, ? extends ModelReference> factory = createModelRefFactory(parameterContext);
                ModelReference intModel = factory.apply(resolver.resolve(Integer.TYPE));
                ModelReference booleanModel = factory.apply(resolver.resolve(Boolean.TYPE));

                parameters.add(new ParameterBuilder()
                        .parameterType("query")
                        .name("current")
                        .modelRef(intModel)
                        .defaultValue("1")
                        .order(Ordered.LOWEST_PRECEDENCE)
                        .description("当前页").build());
                parameters.add(new ParameterBuilder()
                        .parameterType("query")
                        .name("size")
                        .modelRef(intModel)
                        .defaultValue("10")
                        .order(Ordered.LOWEST_PRECEDENCE)
                        .description("分页大小").build());
                parameters.add(new ParameterBuilder()
                        .parameterType("query")
                        .name("searchCount")
                        .modelRef(booleanModel)
                        .defaultValue("true")
                        .order(Ordered.LOWEST_PRECEDENCE)
                        .description("是否查询记录总数").build());
            }
        }

        if(CustomDocket.AUTH_PRESENT) {
            //根据是否需要登录, 约束token是否必传
            Parameter tokenParameter = new ParameterBuilder()
                    .parameterType("header")
                    .name(CoreConfig.SECURITY_HEADER_TOKEN)
                    .defaultValue("")
                    .description("用户token")
                    .modelRef(new ModelRef("string"))
                    .required(false)
                    .build();
            parameters.add(tokenParameter);
        }

        context.operationBuilder().parameters(parameters);
    }


    private Function<ResolvedType, ? extends ModelReference> createModelRefFactory(ParameterContext context) {
        ModelContext modelContext = ModelContext.inputParam(
                context.getGroupName(),
                context.resolvedMethodParameter().getParameterType(),
                context.getDocumentationType(),
                context.getAlternateTypeProvider(),
                context.getGenericNamingStrategy(),
                context.getIgnorableParameterTypes());
        return ResolvedTypes.modelRefFactory(modelContext, nameExtractor);
    }
}
