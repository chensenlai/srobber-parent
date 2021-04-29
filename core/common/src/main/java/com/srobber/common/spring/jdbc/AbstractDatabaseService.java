package com.srobber.common.spring.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * JdbcTemplate API封装
 * 数据库连接服务抽象对象
 * 各项目需要继承并初始化jdbc
 *
 * @author chensenlai
 */
@Slf4j
public abstract class AbstractDatabaseService {

    private JdbcTemplate jdbc;

    /**
     * @param ds
     */
    public void setDataSource(DataSource dataSource) {
    	this.jdbc = new JdbcTemplate(dataSource);
    }

    /**
     * 查询数据库列表, 根据rowMapper构建对象
     * @param sql 查询sql
     * @param rowMapper 行映射器
     * @param <T> 对象类型
     * @return 对象列表
     */
    public <T> List<T> queryList(String sql, final RowMapper<T> rowMapper) {
        return jdbc.query(sql, rowMapper);
    }

    /**
     * 查询数据库列表, 根据rowMapper构建对象
     * @param sql 查询sql
     * @param rowMapper 行映射器
     * @param args 查询参数
     * @param <T> 对象类型
     * @return 对象列表
     */
    public <T> List<T> queryList(String sql, final RowMapper<T> rowMapper, Object... args) {
        final List<T> list = new ArrayList<T>();
        jdbc.query(sql, args, new ResultSetExtractor<List<T>>() {
            public List<T> extractData(ResultSet rs) {
                try {
                    int i = 0;
                    while (rs.next()) {
                        list.add(rowMapper.mapRow(rs, i));
                        i++;
                    }
                } catch (SQLException e) {
                	log.error("RowMapper-query error.", e);
                }
                return list;
            }
        });
        return list;
    }

    /**
     * 查询数据库列表, 返回Map
     * @param sql 查询sql
     * @return Map列表
     */
    public List<Map<String, Object>> queryMapList(String sql) {
        return jdbc.query(sql, new ColumnMapRowMapper());
    }

    /**
     * 查询数据库列表, 返回Map
     * @param sql 查询sql
     * @param args 查询参数
     * @return Map列表
     */
    public List<Map<String, Object>> queryMapList(String sql, Object... args) {
        return jdbc.query(sql, new ColumnMapRowMapper(), args);
    }

    /**
     * 查询数据库列表, 返回单列整型列表
     * @param sql 查询sql
     * @return 整型列表
     */
    public List<Integer> queryIntegerList(String sql) {
        return jdbc.query(sql, new SingleColumnRowMapper<Integer>());
    }

    /**
     * 查询数据库列表, 返回单列整型列表
     * @param sql 查询sql
     * @param args 查询参数
     * @return 整型列表
     */
    public List<Integer> queryIntegerList(String sql, Object... args) {
        return jdbc.query(sql, new SingleColumnRowMapper<Integer>(), args);
    }

    /**
     * 查询数据库列表, 返回单列长整型列表
     * @param sql 查询sql
     * @return 长整型列表
     */
    public List<Long> queryLongList(String sql) {
        return jdbc.query(sql, new SingleColumnRowMapper<Long>());
    }

    /**
     * 查询数据库列表, 返回单列长整型列表
     * @param sql 查询sql
     * @param args 查询参数
     * @return 长整型列表
     */
    public List<Long> queryLongList(String sql, Object... args) {
        return jdbc.query(sql, new SingleColumnRowMapper<Long>(), args);
    }

    /**
     * 查询数据库列表, 返回单列浮点数列表
     * @param sql 查询sql
     * @return 浮点数列表
     */
    public List<Double> queryDoubleList(String sql) {
        return jdbc.query(sql, new SingleColumnRowMapper<Double>());
    }

    /**
     * 查询数据库列表, 返回单列浮点数列表
     * @param sql 查询sql
     * @param args 查询参数
     * @return 浮点数列表
     */
    public List<Double> queryDoubleList(String sql, Object... args) {
        return jdbc.query(sql, new SingleColumnRowMapper<Double>(), args);
    }

    /**
     * 查询数据库列表, 返回单列字符串列表
     * @param sql 查询sql
     * @return 字符串列表
     */
    public List<String> queryStringList(String sql) {
        return jdbc.query(sql, new SingleColumnRowMapper<String>());
    }

    /**
     * 查询数据库列表, 返回单列字符串列表
     * @param sql 查询sql
     * @param args 查询参数
     * @return 字符串列表
     */
    public List<String> queryStringList(String sql, Object... args) {
        return jdbc.query(sql, new SingleColumnRowMapper<String>(), args);
    }

    /**
     * 查询数据库单个对象
     * @param sql 查询sql
     * @param rowMapper 行映射器
     * @param args 查询参数
     * @param <T>
     * @return
     */
    public <T> T queryObject(String sql, final RowMapper<T> rowMapper, Object... args) {
        try {
            return jdbc.queryForObject(sql, rowMapper, args);
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询数据库, 返回Integer
     * @param sql 查询sql
     * @param args 查询参数
     * @return
     */
    public Integer queryInteger(String sql, Object... args) {
        try {
            return jdbc.queryForObject(sql, args, Integer.class);
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询数据库, 返回Long
     * @param sql 查询sql
     * @param args 查询参数
     * @return
     */
    public Long queryLong(String sql, Object... args) {
        try {
            return jdbc.queryForObject(sql, args, Long.class);
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询数据库, 返回Double
     * @param sql 查询sql
     * @param args 查询参数
     * @return
     */
    public Double queryDouble(String sql, Object... args) {
        try {
        	return jdbc.queryForObject(sql, args, Double.class);
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询数据库, 返回String
     * @param sql 查询sql
     * @param args 查询参数
     * @return
     */
    public String queryString(String sql, Object... args) {
        try {
            return jdbc.queryForObject(sql, args, String.class);
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 更新/插入/删除数据记录
     * @param sql 更新sql
     * @param args 更新参数
     * @return 更新记录数
     */
    public int update(String sql, Object... args) {
        return jdbc.update(sql, args);
    }

    /**
     * 批量更新/插入/删除记录(提高性能)
     * @param sql 更新sql
     * @param args 更新参数
     * @return 每组参数对应更新sql更新记录数
     */
    public int[] updateBatch(String sql, List<Object[]> args){
    	 return jdbc.batchUpdate(sql, args);
    }

    /**
     * 插入记录 (可以返回主键, 非返回主键也可用update完成插入)
     * @param sql 插入sql
     * @param keys 数据库主键column (可以多个, 联合组建)
     * @param args 插入参数
     * @return 插入主键, GeneratedKeyHolder原理从resultSet提取对应列值出来
     */
    public List<Map<String, Object>> insert(final String sql, final String[] keys , final Object... args ){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator psc  = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, keys);
                if (args != null) {
                    for(int i=0; i<args.length;i++ ){
                        ps.setObject(i+1, args[i]);
                    }
                }
                return ps;
            }
        };
        int result = jdbc.update(psc, keyHolder);
        if(result <= 0) {
        	return null;
        }
        return keyHolder.getKeyList();
    }

    /**
     * 插入记录, 适用于自增主键插入返回主键指
     * @param sql 插入sql
     * @param key 插入主键名称
     * @param args 插入参数
     * @return 新增主键值 (应用层可以通过 Number.intValue或Number.longValue取出实际主键)
     */
    public Number insert(final String sql, final String key , final Object... args ){
        List<Map<String, Object>> keyList = insert(sql, new String[]{key}, args);
        if(keyList==null || keyList.size()!=1) {
            throw new DatabaseException("DB insert error, assert size=1");
        }
        Map<String, Object> keyMap = keyList.get(0);
        Object value = keyMap.get(key);
        if(value == null) {
            throw new DatabaseException("DB insert error, key null");
        }
        if(!(value instanceof Number)) {
            throw new DatabaseException("DB insert error, key not return number");
        }
        return (Number)value;
    }
}
