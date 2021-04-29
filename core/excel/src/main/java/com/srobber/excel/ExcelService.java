package com.srobber.excel;

import com.srobber.common.result.BatchResult;
import com.srobber.common.result.ServiceResult;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * excel工具类
 * @author chensenlai
 * 2021-04-28 下午12:28
 */
public interface ExcelService {

    /**
     * excel转化成javabean列表
     * @param upload 上传的excel文件
     * @param clazz 转化成javabean类
     * @param contextData 上下文数据,可以用于值转化和javabean校验
     * @param <T> 泛型
     * @return 批处理结果(总数,成功数,转化列表等)
     */
    <T> BatchResult<T> excelToBeanList(File upload, Class<T> clazz,
                                                     Map<String, Object> contextData);

    /**
     * 实体列表转化成excel(默认格式)
     * @param beanList 实体列表
     * @param contextData 上下文数据,用于值转化和数据校验
     * @return excel文件字节
     */
    <T> ServiceResult<byte[]> beanListToExcel(List<T> beanList, Map<String, Object> contextData);

    /**
     * 根据模板和实体生成excel
     * @param template 模板文件
     * @param bean 实体
     * @param contextData 上下文数据,用于值转化和数据校验
     * @return excel文件字节
     */
    <T>ServiceResult<byte[]> beanToExcelTemplate(File template, T bean, Map<String, Object> contextData);
}
