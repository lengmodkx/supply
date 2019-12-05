package com.art1001.supply.wechat.message.exception;

import com.art1001.supply.exception.BaseException;

/**
 * @author heshaohua
 * @date 2019/12/5 15:05
 **/
public class WeChatDataBuildIsEmpty extends BaseException {

    public WeChatDataBuildIsEmpty() {
    }

    public WeChatDataBuildIsEmpty(String message) {
        super(message);
    }

    public WeChatDataBuildIsEmpty(String message, Throwable cause) {
        super(message, cause);
    }

    public WeChatDataBuildIsEmpty(Throwable cause) {
        super(cause);
    }

    public WeChatDataBuildIsEmpty(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
