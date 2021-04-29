package com.srobber.datasource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * 主从数据源配置参数
 *
 * @author chensenlai
 */
@Data
@ConfigurationProperties(prefix = "srobber.datasource")
public class MasterSlaveDataSourceProperties {

    public static List<String> READONLY_METHOD_LIST = Arrays.asList("get", "query",
            "list", "page", "fetch", "read", "count", "sum", "total");

    private Class<? extends javax.sql.DataSource> type;
    private String driverClassName;
    private DataSourceConfig master;
    private List<DataSourceConfig> slaveList;
    /**
     * AspectJ表达式,指定那些切面需要增强
     * 如果没有配置,默认对注解@TargetDataSource或@Transactional类或方法切面启用增强
     */
    private String pointcutExpression;
    /**
     * 默认走从库的只读方法名前缀
     * 如果没有配置,默认${READONLY_METHOD_LIST}配置方法名前缀开头走从库
     */
    private List<String> readonlyMethodList;

    @Data
    public static class DataSourceConfig {
        private String key;
        private String url;
        private String username;
        private String password;
    }
}
