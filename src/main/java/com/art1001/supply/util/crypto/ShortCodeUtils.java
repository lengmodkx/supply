package com.art1001.supply.util.crypto;

import java.util.HashMap;
import java.util.Random;

public class ShortCodeUtils {

    public static HashMap<String, String> map = new HashMap<String, String>();


    public static String getString(String alphabet) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            char c = alphabet.charAt(random.nextInt(20));
            sb.append(c);
        }
        return sb.toString();
    }


    /**
     * 加密
     */
    public static String encode(String longUrl) {
        String key = getString(longUrl);
        if (map.containsKey(key)) {
            key = getString(longUrl);
        }
        map.put(key, longUrl);

        return "https://www.aldbim.com/" + key;
    }

    /**
     * 解密
     */
    public static String decode(String shortUrl) {
        String[] split = shortUrl.split("com/");

        return map.get(split[1]);
    }
}