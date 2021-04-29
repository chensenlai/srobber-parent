package com.srobber.datasource.config;

import com.srobber.datasource.MasterSlaveDataSource;
import com.srobber.datasource.MasterSlaveDataSourceContext;
import com.srobber.datasource.TargetDataSourceAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态数据源注册
 *
 * @author chensenlai
 */
@Slf4j
@EnableConfigurationProperties(MasterSlaveDataSourceProperties.class)
@Configuration
public class MasterSlaveDataSourceConfig {

    @Bean
    public DataSource masterSlaveDataSource(MasterSlaveDataSourceProperties dataSourceProperties) {
        Class<? extends javax.sql.DataSource> type = dataSourceProperties.getType();
        String driverClassName = dataSourceProperties.getDriverClassName();
        //解析主库数据源配置
        MasterSlaveDataSourceProperties.DataSourceConfig master = dataSourceProperties.getMaster();
        if(master == null) {
            throw new NullPointerException("master-slave master null");
        }
        DataSource masterDataSource = createDataSource(type, driverClassName,
                master.getUrl(), master.getUsername(), master.getPassword());
        MasterSlaveDataSourceContext.putMaster(master.getKey());

        //解析从库数据源配置
        Map<Object, Object> slaveDataSources = new HashMap<>(16);
        List<MasterSlaveDataSourceProperties.DataSourceConfig> slaveList = dataSourceProperties.getSlaveList();
        if(slaveList != null && !slaveList.isEmpty()) {
            for(MasterSlaveDataSourceProperties.DataSourceConfig slave : slaveList) {
                String key = slave.getKey();
                DataSource slaveDataSource = createDataSource(type, driverClassName,
                        slave.getUrl(), slave.getUsername(), slave.getPassword());
                Object old = slaveDataSources.put(key, slaveDataSource);
                if(old != null) {
                    throw new RuntimeException("master-slave slave "+key+" duplicate");
                }
                MasterSlaveDataSourceContext.putSlave(slave.getKey());
            }
        }


        MasterSlaveDataSource masterSlaveDataSource = new MasterSlaveDataSource();
        masterSlaveDataSource.setDefaultTargetDataSource(masterDataSource);
        masterSlaveDataSource.setTargetDataSources(slaveDataSources);
        return masterSlaveDataSource;
    }

    @Bean
    public TargetDataSourceAdvisor targetDataSourceAdvisor(BeanFactory beanFactory, MasterSlaveDataSourceProperties dataSourceProperties) {
        TargetDataSourceAdvisor advisor = new TargetDataSourceAdvisor();
        advisor.setDataSourceProperties(dataSourceProperties);
        advisor.setBeanFactory(beanFactory);
        advisor.init();
        return advisor;
    }

    /**
     * 创建数据源
     * @param type 具体实现数据源(如:Druid)
     * @param driverClassName jdbc驱动名
     * @param url   jdbc连接地址
     * @param username jdbc用户名
     * @param password jdbc密码
     * @return 数据源
     */
    private DataSource createDataSource(Class<? extends DataSource> type, String driverClassName,
                                       String url, String username, String password) {
        return DataSourceBuilder.create()
                .type(type)
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }
}