package com.srobber.common.util;

import com.srobber.common.enums.BaseEnum;
import com.srobber.common.config.CoreConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 枚举接口工具类
 *
 * 枚举支持定义在多个枚举类, 继承共同的接口父类,
 * 通过父类扫描找到实现枚举类,匹配枚举值
 *
 * @author chensenlai
 * 2020-12-08 下午5:19
 */
@Slf4j
public class BaseEnumInterfaceUtil {

    /**
     * 扫描包下所有定义的所有枚举接口类
     * properties或yml配置: srobber.core.enum.base-package
     */
    private static Map<Class<? extends BaseEnum>, List<Class<? extends BaseEnum>>> INTERFACE_CLASS_MAP = new HashMap<>();

    static {
        String basePackageStr = CoreConfig.ENUM_BASE_PACKAGE;
        String[] basePackageArr = basePackageStr.split(",|;| | ");
        Collection<Class<? extends BaseEnum>> allInterfaceClass = PackageScanner.scanPackages(basePackageArr, (clazz)->{
            if(BaseEnum.class == clazz) {
                return false;
            }
            if(!BaseEnum.class.isAssignableFrom(clazz)) {
                return false;
            }
            if(!clazz.isInterface()) {
                return false;
            }
            return true;
        }).stream().map(clazz->(Class<? extends BaseEnum>)clazz).collect(Collectors.toSet());

        //枚举接口和枚举类关系绑定
        Collection<Class<? extends BaseEnum>> allEnumClazz = BaseEnumConstantUtil.all();
        for(Class<? extends BaseEnum> interfaceClass : allInterfaceClass) {
            List<Class<? extends BaseEnum>> matchEnumClassList = new ArrayList<>();
            for(Class<? extends BaseEnum> enumClass : allEnumClazz) {
                if(interfaceClass.isAssignableFrom(enumClass)) {
                    matchEnumClassList.add(enumClass);
                }
            }
            INTERFACE_CLASS_MAP.put(interfaceClass, matchEnumClassList);
        }

        //检查枚举接口是否存在重复定义
        for(Class<? extends BaseEnum> interfaceClass : allInterfaceClass) {
            boolean ok = checkDuplicate(interfaceClass);
            if(!ok) {
                List<Class<? extends BaseEnum>> enumClassList = INTERFACE_CLASS_MAP.get(interfaceClass);
                log.error("Enum {}[{}] duplicate", interfaceClass, Arrays.toString(enumClassList.toArray()));
                System.exit(-1);
            }
        }
    }


    /**
     * 枚举数值转成枚举
     * @param interfaceClass 枚举接口类
     * @param num 枚举数值
     * @return 枚举
     */
    public static <E extends BaseEnum> E of(final Class<E> interfaceClass, final Integer num) {
        List<Class<? extends BaseEnum>> enumClassList = INTERFACE_CLASS_MAP.get(interfaceClass);
        for(Class<? extends BaseEnum> enumClass : enumClassList) {
            BaseEnum e = BaseEnumUtil.of(enumClass, num);
            if(e != null) {
                return (E)e;
            }
        }
        return null;
    }

    /**
     * 枚举值名称转枚举
     * @param interfaceClass 枚举接口类
     * @param name 枚举名称
     * @return 枚举
     */
    public static <E extends BaseEnum> E of(final Class<E> interfaceClass, final String name) {
        List<Class<? extends BaseEnum>> enumClassList = INTERFACE_CLASS_MAP.get(interfaceClass);
        for(Class<? extends BaseEnum> enumClass : enumClassList) {
            BaseEnum e = BaseEnumUtil.of(enumClass, name);
            if(e != null) {
                return (E)e;
            }
        }
        return null;
    }

    /**
     * 枚举列表
     * @param interfaceClass 枚举接口类
     * @param <E> 泛型
     * @return 枚举定义列表
     */
    public static <E extends BaseEnum> List<E> of(final Class<E> interfaceClass) {
        List<E> enumList = new ArrayList<>();
        List<Class<? extends BaseEnum>> enumClassList = INTERFACE_CLASS_MAP.get(interfaceClass);
        for(Class<? extends BaseEnum> enumClass : enumClassList) {
            BaseEnum[] es = enumClass.getEnumConstants();
            for(BaseEnum e : es) {
                enumList.add((E)e);
            }
        }
        return enumList;
    }

    /**
     * 获取系统配置所有枚举接口类
     * @return 所有枚举接口类
     */
    public static Collection<Class<? extends BaseEnum>> all() {
        return Collections.unmodifiableCollection(INTERFACE_CLASS_MAP.keySet());
    }

    /**
     * 检查枚举类是否数值定义重复
     * @param interfaceClass 枚举接口类型
     * @return  true-检查通过,没有重复值  false-检查不通过,存在重复定义值
     */
    private static boolean checkDuplicate(Class<? extends BaseEnum> interfaceClass) {
        List<Class<? extends BaseEnum>> enumClassList = INTERFACE_CLASS_MAP.get(interfaceClass);
        Set<Integer> numSet = new HashSet<>();
        for(Class<? extends BaseEnum> enumClass : enumClassList) {
            BaseEnum[] es = enumClass.getEnumConstants();
            for(BaseEnum e : es) {
                boolean notExistOldNum = numSet.add(e.getNum());
                if(!notExistOldNum) {
                    return false;
                }
            }
        }
        return true;
    }
}
