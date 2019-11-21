package com.art1001.supply.aliyun.message.util;

import java.util.Random;

/**
 * @author heshaohua
 * @date 2019/11/21 15:31
 **/
public class CodeGen {

    public static String getCode(){
        Random random = new Random();
        int randomNum = random.nextInt(1000000);
        return String.format("%06d", randomNum);

    }
}
