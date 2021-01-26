package com.art1001.supply.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName OrderUtils
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/26 13:46
 * @Discription 订单工具类
 */
public class OrderUtils {

    public static String createOrderSn(){
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String strDate2 = dtf2.format(ldt);
        int result=0;
        for(int j = 0; j< 100; j++){
            result=(int)((Math.random()*9+1)*100000);
        }
        return "SN"+strDate2+result;
    }
}
