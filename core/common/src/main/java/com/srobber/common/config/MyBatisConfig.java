package com.srobber.common.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.srobber.common.mybatis.plugin.SqlMonitorInterceptor;
import com.srobber.common.mybatis.type.BaseEnumTypeHandler;
import com.srobber.common.enums.BaseEnum;
import com.srobber.common.util.BaseEnumUtil;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis配置管理
 * 1 使用BaseEnum枚举定义,增强BaseEnumTypeHandler
 * 2 增加sql打印插件日志, 方便审查
 * 3 增加分页插件
 *
 * @author chensenlai
 */
@Configuration
public class MyBatisConfig implements ConfigurationCustomizer {

    @Override
    public void customize(MybatisConfiguration configuration) {
        //注册枚举值类型转化处理器
        TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
        for(final Class<? extends BaseEnum> enumClass : BaseEnumUtil.all()) {
            registry.register(enumClass, new BaseEnumTypeHandler(enumClass));
        }
    }

    /**
     * sql打印耗时插件
     */
    @Bean
    public SqlMonitorInterceptor sqlMonitorInterceptor() {
        return new SqlMonitorInterceptor();
    }

    /**
     * Mybatis-Plus分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setLimit(500);
        return paginationInterceptor;
    }
}
