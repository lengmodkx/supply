package com.art1001.supply.aliyun.message.entity;

import lombok.Data;

/**
 * @author heshaohua
 * @date 2019/11/21 16:27
 **/
@Data
public class CodeQueryParam {

    private String code;

    public static CodeQueryParam buildQueryParam(String code){
        return new CodeQueryParam(code);
    }

    public CodeQueryParam(String code) {
        this.code = code;
    }

    public CodeQueryParam() {

    }
}
