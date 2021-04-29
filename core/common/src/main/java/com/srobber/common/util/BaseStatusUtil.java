package com.srobber.common.util;

import com.srobber.common.status.BaseStatus;
import com.srobber.common.config.CoreConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 响应状态工具类
 *
 * @author chensenlai
 * 2020-09-19 下午3:01
 */
public class BaseStatusUtil {

    /**
     * 扫描包下所有定义的所有枚举类
     * properties或yml配置: srobber.core.status.base-package
     */
    private static Collection<Class<? extends BaseStatus>> STATUS_CLASS_SET = null;
    static {
        String basePackageStr = CoreConfig.STATUS_BASE_PACKAGE;
        String[] basePackageArr = basePackageStr.split(",|;| | ");
        Collection<Class<? extends BaseStatus>> all = PackageScanner.scanPackages(basePackageArr, (clazz)->{
            if(!(clazz.isEnum() && BaseStatus.class.isAssignableFrom(clazz))) {
                return false;
            }
            return true;
        }).stream().map(clazz->(Class<? extends BaseStatus>)clazz).collect(Collectors.toSet());

        STATUS_CLASS_SET = Collections.unmodifiableCollection(all);
    }

    /**
     * 获取系统配置所有状态类
     * @return 系统所有状态类定义
     */
    public static Collection<Class<? extends BaseStatus>> all() {
        return STATUS_CLASS_SET;
    }

}
