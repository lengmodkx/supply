package com.art1001.supply.wechat.message.exception;

import com.art1001.supply.exception.BaseException;

/**
 * @author heshaohua
 * @date 2019/12/5 15:02
 **/
public class UserIdListIsEmptyException extends BaseException {

    public UserIdListIsEmptyException() {
    }

    public UserIdListIsEmptyException(String message) {
        super(message);
    }

    public UserIdListIsEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserIdListIsEmptyException(Throwable cause) {
        super(cause);
    }

    public UserIdListIsEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
