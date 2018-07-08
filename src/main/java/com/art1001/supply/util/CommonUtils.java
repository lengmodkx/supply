package com.art1001.supply.util;

import java.util.Arrays;

public class CommonUtils {

    /**
     * 判断数组中是否存在某元素
     *
     * 存在返回 true
     */
    public static boolean useList(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    public static void main(String[] args) {
        String[] strings = {"1", "2"};
        System.out.println(useList(strings, "1"));
    }

}
