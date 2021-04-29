package com.srobber.common.util;

import com.srobber.common.enums.BaseEnum;
import com.srobber.common.config.CoreConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 枚举实例工具类
 *
 * @author chensenlai
 * 2020-12-10 下午3:13
 */
@Slf4j
public class BaseEnumConstantUtil {

    /**
     * 扫描包下所有定义的所有枚举类
     * properties或yml配置: srobber.core.enum.base-package
     */
    private static Collection<Class<? extends BaseEnum>> ENUM_CLASS_SET = null;

    static {
        String basePackageStr = CoreConfig.ENUM_BASE_PACKAGE;
        String[] basePackageArr = basePackageStr.split(",|;| | ");
        Collection<Class<? extends BaseEnum>> all = PackageScanner.scanPackages(basePackageArr, (clazz)->{
            if(!BaseEnum.class.isAssignableFrom(clazz)) {
                return false;
            }
            if(!clazz.isEnum()) {
                return false;
            }
            return true;
        }).stream().map(clazz->(Class<? extends BaseEnum>)clazz).collect(Collectors.toSet());

        //检查枚举值定义数值存在重复
        for(Class<? extends BaseEnum> enumClass : all) {
            boolean ok = checkDuplicate(enumClass);
            if(!ok) {
                log.error("Enum {} duplicate", enumClass);
                System.exit(-1);
            }
        }

        ENUM_CLASS_SET = all;
    }

    /**
     * 枚举值转成枚举
     * @param enumClass 枚举类
     * @param num 枚举数值
     * @return 枚举
     */
    public static <E extends BaseEnum> E of(final Class<E> enumClass, final Integer num) {
        if(num == null) {
            return null;
        }
        E[] es = enumClass.getEnumConstants();
        for(E e : es) {
            if(e.getNum() == num) {
                return e;
            }
        }
        return null;
    }

    /**
     * 枚举名称转枚举
     * @param enumClass 枚举类
     * @param name 枚举名称
     * @return 枚举
     */
    public static <E extends BaseEnum> E of(final Class<E> enumClass, final String name) {
        E[] es = enumClass.getEnumConstants();
        for(E e : es) {
            if(Objects.equals(e.getName(), name)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 枚举列表
     * @param enumClass 枚举类
     * @param <E> 泛型
     * @return 枚举定义列表
     */
    public static <E extends BaseEnum> List<E> of(final Class<E> enumClass) {
        E[] es = enumClass.getEnumConstants();
        return Arrays.asList(es);
    }

    /**
     * 获取系统配置所有枚举类
     * @return 所有枚举类
     */
    public static Collection<Class<? extends BaseEnum>> all() {
        return Collections.unmodifiableCollection(ENUM_CLASS_SET);
    }

    /**
     * 检查枚举类是否数值定义重复
     * @param enumClass 枚举类型
     * @return  true-检查通过,没有重复值  false-检查不通过,存在重复定义值
     */
    private static boolean checkDuplicate(Class<? extends BaseEnum> enumClass) {
        BaseEnum[] es = enumClass.getEnumConstants();
        Set<Integer> numSet = new HashSet<>((int)Math.ceil(es.length/0.75));
        for(BaseEnum e : es) {
            boolean notExistOldNum = numSet.add(e.getNum());
            if(!notExistOldNum) {
                return false;
            }
        }
        return true;
    }
}
