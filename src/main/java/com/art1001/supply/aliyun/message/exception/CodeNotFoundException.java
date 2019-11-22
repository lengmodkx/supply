package com.art1001.supply.aliyun.message.exception;

import com.art1001.supply.exception.BaseException;

/**
 * @author heshaohua
 * @date 2019/11/21 15:44
 **/
public class CodeNotFoundException extends BaseException {

    public CodeNotFoundException() {
        super();
    }

    public CodeNotFoundException(String message) {
        super(message);
    }

    public CodeNotFoundException(Throwable cause) {
        super(cause);
    }

    public CodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
