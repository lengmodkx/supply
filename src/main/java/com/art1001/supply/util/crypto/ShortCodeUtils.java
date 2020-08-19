package com.art1001.supply.util.crypto;

import com.art1001.supply.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.Operation;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

@Component
public class ShortCodeUtils {

    @Resource
    private RedisUtil redisUtil;

//    public static HashMap<String, String> map = new HashMap<String, String>();

    public  String getString(String alphabet) {
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
    public String encode(String longUrl) {
        String key = getString(longUrl);
        redisUtil.set("shortUrl:"+key,key,LocalDateTime.now().withNano(0).plusHours(48).toEpochSecond(ZoneOffset.of("+8")));

        return key;
    }

    /**
     * 解密
     */
    public String decode(String shortUrl) {
        String[] split = shortUrl.split("in/");
        return redisUtil.get("shortUrl:"+split[1]);
    }
}