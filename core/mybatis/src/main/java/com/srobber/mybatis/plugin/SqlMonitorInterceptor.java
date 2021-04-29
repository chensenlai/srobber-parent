package com.srobber.mybatis.plugin;

import com.srobber.common.enums.BaseEnum;
import com.srobber.common.spring.EnvironmentContext;
import com.srobber.common.util.DateFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.core.Ordered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Mybatis SQL拦截打印以及耗时
 * 参考: https://github.com/WangJi92/mybatis-sql-log/
 *
 * @author chensenlai
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
@Slf4j
public class SqlMonitorInterceptor implements Interceptor, Ordered {

    /**
     * 消耗耗时阀值
     */
    private final static int SPENT_MILLS = 500;

    private Configuration configuration = null;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        long startTime = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long sqlCost = endTime - startTime;

            StatementHandler statementHandler = (StatementHandler) target;
            BoundSql boundSql = statementHandler.getBoundSql();

            if(configuration == null){
                final DefaultParameterHandler parameterHandler = (DefaultParameterHandler) statementHandler.getParameterHandler();
                Field configurationField = ReflectionUtils.findField(parameterHandler.getClass(), "configuration");
                ReflectionUtils.makeAccessible(configurationField);
                this.configuration =  (Configuration) configurationField.get(parameterHandler);
            }

            //替换参数格式化Sql语句，去除换行符
            String sql = formatSql(boundSql, configuration);

            if(EnvironmentContext.isProdEnv()) {
                if(sqlCost > SPENT_MILLS) {
                    log.warn("sql: {} {}ms", sql, sqlCost);
                }
            } else {
                log.info("sql: {} {}ms", sql, sqlCost);
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 获取完整的sql实体的信息
     *
     * @param boundSql
     * @return
     */
    private String formatSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();
        // 输入sql字符串空判断
        if (sql == null || sql.length() == 0) {
            return "";
        }

        if (configuration == null) {
            return "";
        }

        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        // 美化sql
        sql = beautifySql(sql);
        /**
         * @see org.apache.ibatis.scripting.defaults.DefaultParameterHandler
         * 参考Mybatis 参数处理
         */
        if (parameterMappings != null) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    String paramValueStr = "";
                    if(value instanceof String){
                        paramValueStr = "'" + value + "'";
                    }else if (value instanceof Date) {
                        paramValueStr = "'" + DateFormatUtil.format((Date)value, DateFormatUtil.Pattern.YYYY_MM_DD_HH_MM_SS_SSS) + "'";
                    }else if(value instanceof BaseEnum) {
                        paramValueStr = ((BaseEnum)value).getNum() + "";
                    } else {
                        paramValueStr =  value + "";
                    }
                    // mybatis generator 中的参数不打印出来
                    if(!propertyName.contains("frch_criterion")){
                        paramValueStr = "/*" + propertyName + "*/" + paramValueStr;
                    }
                    sql = sql.replaceFirst("\\?", paramValueStr);
                }
            }
        }
        return sql;
    }

    /**
     * 美化Sql
     */
    private String beautifySql(String sql) {
        sql = sql.replaceAll("[\\s\n ]+", " ");
        return sql;
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}