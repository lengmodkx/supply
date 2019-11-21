package com.art1001.supply.aliyun.message.exception;

/**
 * @author heshaohua
 * @date 2019/11/21 15:46
 **/
public class CodeMismatchException extends RuntimeException {

    public CodeMismatchException() {
        super();
    }

    public CodeMismatchException(String message) {
        super(message);
    }

    public CodeMismatchException(Throwable cause) {
        super(cause);
    }

    public CodeMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
