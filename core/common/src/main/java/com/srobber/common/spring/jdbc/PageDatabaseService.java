package com.srobber.common.spring.jdbc;

import java.util.List;

import com.srobber.common.spring.jdbc.pagination.Page;
import org.springframework.jdbc.core.RowMapper;

/**
 * JdbcTemplate API封装
 * 带分页功能DB服务
 *
 * @author chensenlai
 */
public abstract class PageDatabaseService extends AbstractDatabaseService {

	/**
	 * 分页查询列表
	 * @param page 分页参数
	 * @param sql	查询sql
	 * @param rowMapper 行映射器
	 * @param <T> 对象类型
	 * @return 分页列表
	 */
	public <T> Page<T> queryList(Page<T> page, String sql, final RowMapper<T> rowMapper) {
    	String countSql = page.getPageCountSql(sql);
    	return queryList(page, sql, countSql, rowMapper);
    }

	/**
	 * 分页查询列表
	 * @param page 分页参数
	 * @param sql 查询sql
	 * @param countSql 查询总行数sql
	 * @param rowMapper 行映射器
	 * @param <T> 对象类型
	 * @return 分页列表
	 */
    public <T> Page<T> queryList(Page<T> page, String sql, String countSql, final RowMapper<T> rowMapper) {
    	int totalRecord = queryInteger(countSql);
		page.setTotalRecord(totalRecord);
    	if(totalRecord >= 1) {
    		List<T> recordList = queryList(page.getPageSql(sql), rowMapper);
			page.setRecordList(recordList);
    	}
        return page;
    }

	/**
	 * 分页查询列表
	 * @param page 分页参数
	 * @param sql 查询sql
	 * @param rowMapper 行映射器
	 * @param args 查询参数
	 * @param <T> 对象类型
	 * @return 分页列表
	 */
    public <T> Page<T> queryList(Page<T> page, String sql, final RowMapper<T> rowMapper, Object... args) {
    	String countSql = page.getPageCountSql(sql);
    	return queryList(page, sql, countSql, rowMapper, args);
    }

	/**
	 * 分页查询列表
	 * @param page 分页参数
	 * @param sql 查询sql
	 * @param countSql 查询总行数sql
	 * @param rowMapper 行映射器
	 * @param args 查询参数
	 * @param <T> 对象类型
	 * @return 分页列表
	 */
    public <T> Page<T> queryList(Page<T> page, String sql, String countSql, final RowMapper<T> rowMapper, Object... args) {
    	int totalRecord = queryInteger(countSql, args);
		page.setTotalRecord(totalRecord);
    	if(totalRecord >= 1) {
    		List<T> recordList = queryList(page.getPageSql(sql), rowMapper, args);
			page.setRecordList(recordList);
    	}
        return page;
    }
}
