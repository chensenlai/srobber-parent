package com.srobber.datasource;

import org.springframework.core.NamedThreadLocal;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 主从上下文
 *
 * @author chensenlai
 */
public class MasterSlaveDataSourceContext {

    /**
     * TODO 多线程环境下ThreadLocal无法传递
     */
    private static final ThreadLocal<String> CONTEXT =
            new NamedThreadLocal<String>("master-slave datasource context");

    /**
     * 主库
     */
    private static String MASTER;
    /**
     * 从库
     */
    private static List<String> SLAVES = new ArrayList<>();

    public static String getMaster() {
        return MASTER;
    }

    public static void putMaster(String master) {
        MASTER = master;
    }

    public static String getSlave() {
        if(SLAVES.size() == 0) {
            return null;
        }
        return SLAVES.get(new Random().nextInt(SLAVES.size()));
    }

    public static void putSlave(String slave) {
        SLAVES.add(slave);
    }

    /**
     * @return 选择数据源, 不存在则选master
     */
    public static String choose() {
        String key = CONTEXT.get();
        if(key == null) {
            key = MASTER;
        }
        return key;
    }

    /**
     * @return 获取上下文数据源
     */
    public static String get() {
        return CONTEXT.get();
    }

    /**
     * 设置上下文数据源
     * @param key 数据源
     */
    public static void set(String key) {
        if(key != null) {
            if(!MASTER.equals(key) && !SLAVES.contains(key)) {
                throw new InvalidParameterException("master-slave key "+key+" not exist");
            }
        }
        CONTEXT.set(key);
    }
}
