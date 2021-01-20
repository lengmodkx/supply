package com.art1001.supply.util;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName PatternUtils
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/12 10:55
 * @Discription 正则解析工具类
 */
public class PatternUtils {

    private static Pattern patternTags = Pattern.compile("#(.*?)#");
    private static Pattern patternNickName = Pattern.compile("@(.*?)\\s");

    /**
     * 解析#话题标签
     * @param str
     * @return
     */
    public static List<String> parsingTags(String str) {
        List<String> list = Lists.newArrayList();
        Matcher matcher = patternTags.matcher(str);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    /**
     * 解析昵称
     * @param str
     * @return
     */
    public static List<String> parsingNickName(String str){
        List<String> list = Lists.newArrayList();
        Matcher matcher = patternNickName.matcher(str);
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }
}
