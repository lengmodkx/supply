package com.art1001.supply.aliyun.message.util;

import com.art1001.supply.aliyun.message.exception.MessageSendException;
import com.art1001.supply.util.ObjectsUtil;

import java.util.regex.Pattern;

/**
 * @author heshaohua
 * @date 2019/11/21 14:41
 **/
public class PhoneTest {

    private static final String PHONE_TEST = "^1[3|4|5|7|8][0-9]{9}$";

    private static final int PHONE_LENGTH = 11;


    /**
     * 验证手机号合法性
     * @param phone 手机号
     */
    public static void testPhone(String phone){

        if(ObjectsUtil.isEmpty(phone)){
            throw new MessageSendException("手机号为空");
        }

        if(phone.length() != PHONE_LENGTH){
            throw new MessageSendException("手机号应为11位数");

        } else {
            Pattern p = Pattern.compile(PHONE_TEST);
            if(!p.matcher(phone).matches()){
                throw new MessageSendException("手机号格式不正确");
            }
        }
    }
}
