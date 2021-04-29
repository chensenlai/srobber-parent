package com.srobber.common.spring.jdbc.pagination;

import com.srobber.common.spring.jdbc.DatabaseException;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库分页实体
 * 
 * @author chensenlai
 */
@Data
public class Page<T> {
	
	/**
	 * 限制分页最大记录数
	 */
	public static final int MAX_PAGE_SIZE = 1000;

	/**
	 * 当前页码,默认第一页
	 */
	private int pageNo = 1;
	/**
	 * 分页大小,默认一页10条记录
	 */
	private int pageSize = 10;

	/**
	 * 记录总数
	 */
	private int totalRecord;
	/**
	 * 分页记录
	 */
	private List<T> recordList;

	/**
	 * @return 第一页页码, 没记录返回0
	 */
	public int getFirstPageNo() {
		int pageTotal = getPageTotal();
		if (getPageTotal() == 0) {
			return 0;
		}
		return 1;
	}

	/**
	 * @return 最后一页页码, 没记录返回0
	 */
	public int getLastPageNo() {
		int pageTotal = getPageTotal();
		if (getPageTotal() == 0) {
			return 0;
		}
		return pageTotal;
	}

	/**
	 * @return 上一页页码
	 */
	public int getPreviousPageNo() {
		if (this.pageNo <= 1) {
			return 1;
		}
		return this.pageNo - 1;
	}

	/**
	 * @return 下一页页码
	 */
	public int getNextPageNo() {
		if (this.pageNo >= getLastPageNo()) {
			return getLastPageNo();
		}
		return this.pageNo + 1;
	}

	/**
	 * @return 总页码数
	 */
	public int getPageTotal() {
		return (totalRecord + pageSize - 1) / pageSize;
	}

	/**
	 * @return 分页页码容器列表
	 */
	public List<Integer> getPageNoList(){
		List<Integer> pageNoList = new ArrayList<>(10);
		int curPageNo = getPageNo();
		int lastPageNo = getLastPageNo();
		//共显示5页(Tip: -1标识...)
		if(lastPageNo <= 5) {
			//总页数小于等于5页，全部显示
			for(int i=1; i<=lastPageNo; i++) {
				pageNoList.add(i);
			}
		} else {
			//总页数大于5页
			if(curPageNo <= 3) {
				//1) 当前页小于等于3，显示1 2 3 4 ... lastPage
				for(int i=1; i<=4; i++) {
					pageNoList.add(i);
				}
				pageNoList.add(-1);
				pageNoList.add(lastPageNo);
			} else if(curPageNo >= lastPageNo-2) {
				pageNoList.add(1);
				pageNoList.add(-1);
				//2) 当前页大于等于lastPage-2，显示  1 ... lastPage-3 lastPage-2 lastPage-1 lastPage
				for(int i=lastPageNo-3; i<=lastPageNo; i++) {
					pageNoList.add(i);
				}
			} else {
				//3) 当前页大于等于4小于等于lastPage-3，显示1 ... pageNo-1 pageNo pageNo+1 ... lastPage
				pageNoList.add(1);
				pageNoList.add(-1);
				for(int i=curPageNo-1; i<=curPageNo+1; i++) {
					pageNoList.add(i);
				}
				pageNoList.add(-1);
				pageNoList.add(lastPageNo);
			}
		}
		return pageNoList;
	}
	
	/**
	 * @param queryString 查询sql
	 * @return 分页记录总数sql
	 */
	public String getPageCountSql(String queryString) {
		return "SELECT COUNT(*) FROM ( " + queryString + " ) __c ";
		
	}

	/**
	 * @param queryString 查询sql
	 * @return 分页查询sql
	 */
	public String getPageSql(String queryString) {
		int curPageNo = getPageNo();
		int curPageSize = getPageSize();
		if(curPageNo < 1) {
			throw new DatabaseException("invalid pageNo "+curPageNo);
		}
		if(curPageSize < 1 || curPageSize > MAX_PAGE_SIZE) {
			throw new DatabaseException("invalid pageSize "+curPageSize);
		}
		int startIndex = (curPageNo-1)*curPageSize;
		return getMySqlPageSql(queryString, startIndex, curPageSize);
	}
	
	private String getMySqlPageSql(String queryString, Integer startIndex, Integer pageSize) {
		StringBuilder pageSql = new StringBuilder(queryString);
		if (startIndex != null) {
			pageSql.append(" LIMIT ").append(startIndex);
			if(pageSize != null) {
				pageSql.append(",").append(pageSize);
			}
		}
		return pageSql.toString();
	}
}
