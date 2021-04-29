package com.srobber.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 主从数据源切换(支持一主多从)
 * 应用场景: 读写分离
 *
 * @author chensenlai
 */
@Slf4j
public class MasterSlaveDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return MasterSlaveDataSourceContext.choose();
    }

}