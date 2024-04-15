package com.zhongqin.commons.util;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集合分割工具类
 *
 * @author Kevin
 * @date 2020/11/20 13:23
 */
public class CollectionUtil {

    private static final Integer MAX_NUMBER = 500;

    /**
     * 按需要分割的数量分割
     *
     * @param originList 要分割的集合
     * @param maxNumber  按多少个一组分割
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> divide(List<T> originList, int maxNumber) {
        return getLists(originList, maxNumber);
    }

    /**
     * 按需要分割的数量分割
     *
     * @param originList 要分割的集合
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> divide(List<T> originList) {
        return getLists(originList, MAX_NUMBER);
    }

    /**
     * 按需要分割结果的份数分割
     *
     * @param originList 要分割的集合
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> divideParts(List<T> originList, int partsNumber) {
        if (CollectionUtils.isEmpty(originList) || partsNumber <= 0) {
            return Collections.emptyList();
        }
        int limit = (originList.size() / partsNumber) + 1;
        return getLists(originList, limit);
    }

    private static <T> List<List<T>> getLists(List<T> originList, int maxNumber) {
        if (CollectionUtils.isEmpty(originList)) {
            return Collections.emptyList();
        }
        int limit = countStep(originList.size(), maxNumber);
        List<List<T>> resultList = new ArrayList<>();
        Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
            resultList.add(originList.stream().skip(i * maxNumber).limit(maxNumber).collect(Collectors.toList()));
        });
        return resultList;
    }

    /**
     * 计算切分次数
     *
     * @param size      总集合大小
     * @param maxNumber 按多少个一组分割
     * @return
     */
    private static Integer countStep(int size, int maxNumber) {
        return (size + maxNumber - 1) / maxNumber;
    }
}
