package com.srobber.common.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 集合流式处理工具类
 *
 * @author chensenlai
 * 2020-09-23 下午4:31
 */
public class StreamUtil {

    public static <T> List<T> filterToList(Collection<T> sourceList, Predicate<T> test) {
        if(sourceList == null) {
            return null;
        }
        return sourceList.stream()
                .filter(test)
                .collect(Collectors.toList());
    }

    public static <T> T filterToOne(Collection<T> sourceList, Predicate<T> test) {
        if(sourceList == null) {
            return null;
        }
        Optional<T> opt = sourceList.stream()
                .filter(test)
                .findFirst();
        return opt.orElse(null);
    }

    public static <S, T> List<T> mapToList(Collection<S> sourceList, Function<S, T> fun) {
        if(sourceList == null) {
            return null;
        }
        return sourceList.stream()
                .map(fun)
                .collect(Collectors.toList());
    }

    public static <S, T> List<T> flatMapToList(Collection<S> sourceList, Function<S, List<T>> fun) {
        if(sourceList == null) {
            return null;
        }
        return sourceList.stream()
                .flatMap(s->CollectionUtil.getNotNullList(fun.apply(s)).stream())
                .collect(Collectors.toList());
    }

    public static <S, T> Page<T> mapToPage(Page<S> sourcePage, Function<S, T> fun) {
        if(sourcePage == null) {
            return null;
        }
        List<S> sourceList = sourcePage.getRecords();
        List<T> targetList = mapToList(sourceList, fun);

        Page<T> targetPage = new Page<>();
        targetPage.setCurrent(sourcePage.getCurrent());
        targetPage.setSize(sourcePage.getSize());
        targetPage.setTotal(sourcePage.getTotal());
        targetPage.setRecords(targetList);
        return targetPage;
    }
}
