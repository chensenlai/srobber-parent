package com.srobber.common.util;

import com.srobber.common.enums.BaseEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 枚举工具类
 *
 * @author chensenlai
 */
public class BaseEnumUtil {

    /**
     * 枚举值转成枚举
     * @param clazz 枚举类(或枚举接口定义)
     * @param num 枚举数值
     * @return 枚举
     */
    public static <E extends BaseEnum> E of(final Class<E> clazz, final Integer num) {
        if(num == null) {
            return null;
        }
        if(clazz.isEnum()) {
            return BaseEnumConstantUtil.of(clazz, num);
        }
        if(clazz.isInterface()) {
            return BaseEnumInterfaceUtil.of(clazz, num);
        }
        return null;
    }

    /**
     * 枚举名称转枚举
     * @param clazz 枚举类(或枚举接口定义)
     * @param name 枚举名称
     * @return 枚举
     */
    public static <E extends BaseEnum> E of(final Class<E> clazz, final String name) {
        if(clazz.isEnum()) {
            return BaseEnumConstantUtil.of(clazz, name);
        }
        if(clazz.isInterface()) {
            return BaseEnumInterfaceUtil.of(clazz, name);
        }
        return null;
    }

    /**
     * 枚举列表
     * @param clazz 枚举类(或枚举接口定义)
     * @param <E> 泛型
     * @return 枚举定义列表
     */
    public static <E extends BaseEnum> List<E> of(final Class<E> clazz) {
        if(clazz.isEnum()) {
            return BaseEnumConstantUtil.of(clazz);
        }
        if(clazz.isInterface()) {
            return BaseEnumInterfaceUtil.of(clazz);
        }
        return null;
    }

    /**
     * 获取系统配置所有枚举类
     * @return 所有枚举类
     */
    public static Collection<Class<? extends BaseEnum>> all() {
        List<Class<? extends BaseEnum>> list = new ArrayList<>();
        list.addAll(BaseEnumConstantUtil.all());
        list.addAll(BaseEnumInterfaceUtil.all());
        return Collections.unmodifiableCollection(list);
    }
}
