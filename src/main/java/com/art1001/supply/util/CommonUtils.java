package com.art1001.supply.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CommonUtils {

    /**
     * 判断数组中是否存在某元素
     *
     * 存在返回 true
     */
    public static boolean useList(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    /**
     * 移除数组的某个元素
     * @param arr 数组
     * @param targetValue 被移除的元素
     */
    public static void removeValue(String[] arr, String targetValue) {
        List<String> strings = new ArrayList<String>();
        strings = Arrays.asList(arr);
        strings.remove(targetValue);
    }

    public static boolean listIsEmpty(Collection collection){
        return collection == null || collection.isEmpty();
    }

}
