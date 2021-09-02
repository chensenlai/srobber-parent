package com.srobber.common.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * 集合工具类
 *
 * @author chensenlai
 */
public class CollectionUtil {

    public static boolean isEmpty(Collection collection) {
        if(collection==null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public <T, I> T getFromList(List<T> list, Function<T, I> mapper, I id) {
        if(CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.stream().filter(t-> Objects.equals(mapper.apply(t), id)).findAny().orElse(null);
    }

    public static <E> List<E> randomFetchList(List<E> list, int fetchNum) {
        if(list == null || list.isEmpty()) {
            return list;
        }
        Collections.shuffle(list);
        if(list.size() < fetchNum) {
            return list;
        }
        return list.subList(0, fetchNum);
    }

    public static <E> List<E> getNotNullList(List<E> list) {
        if(list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    public static <E> Set<E> getNotNullSet(Set<E> set) {
        if(set == null) {
            return Collections.emptySet();
        }
        return set;
    }

    private CollectionUtil(){}
}
