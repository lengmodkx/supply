package com.art1001.supply.aliyun.message.exception;

import com.art1001.supply.exception.BaseException;

/**
 * @author heshaohua
 * @date 2019/11/21 14:43
 **/
public class MessageSendException extends BaseException {

    public MessageSendException() {
        super();
    }

    public MessageSendException(String message) {
        super(message);
    }

    public MessageSendException(Throwable cause) {
        super(cause);
    }

    public MessageSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageSendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
