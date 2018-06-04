package com.art1001.supply.shiro.util;

import com.art1001.supply.model.user.UserEntity;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class ShiroMD5Util {

    //添加user的密码加密方法
    public static String Md5(UserEntity userEntity) {
        String hashAlgorithmName = "MD5";//加密方式
        Object crdentials =userEntity.getPassword();//密码原值

        ByteSource salt = ByteSource.Util.bytes(userEntity.getAccountName());//以账号作为盐值

        int hashIterations = 1024;//加密1024次

        SimpleHash hash = new SimpleHash(hashAlgorithmName,crdentials,salt,hashIterations);

        return hash.toString();
    }
}
