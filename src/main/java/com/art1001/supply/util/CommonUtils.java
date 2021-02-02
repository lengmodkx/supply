package com.art1001.supply.util;

import java.util.*;

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

    /**
     * 随机字符串生成
     * @param length
     * @return
     */
    public static String getRandomString(int length) { // length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }




    public static boolean listIsEmpty(Collection collection){
        return collection == null || collection.isEmpty();
    }

    public static String listToString(List<String> coverImages) {
        StringBuilder images = new StringBuilder();
        for (int i = 0; i < coverImages.size(); i++) {
            if (i != coverImages.size() - 1) {
                images.append(coverImages.get(i)).append(",");
            } else {
                images.append(coverImages.get(i));
            }
        }
        return images.toString();
    }

}
