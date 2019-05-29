package com.art1001.supply.exception;

/**
 * @Description
 * @Date:2019/5/29 13:40
 * @Author heshaohua
 **/
public class ApiParamsCheckException extends RuntimeException {


    public ApiParamsCheckException() {
        super();
    }

    public ApiParamsCheckException(String message) {
        super(message);
    }

    public ApiParamsCheckException(Throwable cause) {
        super(cause);
    }

    public ApiParamsCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiParamsCheckException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
