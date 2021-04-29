package com.srobber.cache;

/**
 * 缓存前缀, 方便缓存的管理
 *
 * @author chensenlai
 * 2020-09-17 上午11:27
 */
@FunctionalInterface
public interface CachePrefix {

    /**
     * 缓存前缀
     * @return 缓存前缀
     */
    String name();
}