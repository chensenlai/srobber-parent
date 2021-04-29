package com.srobber.excel;

import java.util.Map;

/**
 * excel的一行转化成javabean后,校验bean数据的合法性
 * 校验后的结果会记录在对应行的resultCol和msgCol列. {@link Excel}
 *
 * @author chensenlai
 */
public interface BeanValidator<T> {

	/**
	 * excel一行转化成javabean后校验bean参数合法性
	 * @param bean excel一行转化成javabean
	 * @param contextData 上下文数据,excel导入时校验
	 * @return 校验失败错误提示信息, 成功返回null或""
	 */
	String validate(T bean, Map<String, Object> contextData);
}
