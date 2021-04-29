package com.srobber.excel.config;

import com.srobber.excel.ExcelService;
import com.srobber.excel.ExcelServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Excel配置
 * @author chensenlai
 * 2021-04-28 下午12:34
 */
@Configuration
public class ExcelConfig {

    @Bean
    public ExcelService excelService() {
        ExcelServiceImpl excelService = new ExcelServiceImpl();
        return excelService;
    }
}
