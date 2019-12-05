package com.art1001.supply.wechat.message.exception;

import com.art1001.supply.exception.BaseException;

/**
 * @author heshaohua
 * @date 2019/12/5 15:49
 **/
public class WeChatAppMessageTemplateInvalid extends BaseException {
    public WeChatAppMessageTemplateInvalid() {
    }

    public WeChatAppMessageTemplateInvalid(String message) {
        super(message);
    }

    public WeChatAppMessageTemplateInvalid(String message, Throwable cause) {
        super(message, cause);
    }

    public WeChatAppMessageTemplateInvalid(Throwable cause) {
        super(cause);
    }

    public WeChatAppMessageTemplateInvalid(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
