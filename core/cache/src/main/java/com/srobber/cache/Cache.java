package com.srobber.cache;

/**
 * 缓存操作
 *
 * @author chensenlai
 * 2020-09-17 上午11:06
 */
public interface Cache {

    /**
     * 获取缓存定义
     * @return 缓存配置
     */
    CacheDefinition getDefinition();

    /**
     * 获取缓存
     * @param key 缓存key
     * @return 缓存对象
     */
    <T> T get(String key);

    /**
     * 设置缓存
     * @param key 缓存key
     * @param value 缓存对象
     */
    <T> void set(String key, T value);


    /**
     * 删除缓存
     * @param key 缓存key
     */
    void delete(String key);
}
