package com.art1001.supply.wechat.message.exception;

import com.art1001.supply.exception.BaseException;

/**
 * @author heshaohua
 * @date 2019/12/5 15:47
 **/
public class UserIdIsEmpty extends BaseException {

    public UserIdIsEmpty() {
    }

    public UserIdIsEmpty(String message) {
        super(message);
    }

    public UserIdIsEmpty(String message, Throwable cause) {
        super(message, cause);
    }

    public UserIdIsEmpty(Throwable cause) {
        super(cause);
    }

    public UserIdIsEmpty(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
