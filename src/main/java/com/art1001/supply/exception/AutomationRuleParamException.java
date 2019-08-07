package com.art1001.supply.exception;

/**
 * @Description
 * @Date:2019/8/7 11:34
 * @Author heshaohua
 **/
public class AutomationRuleParamException extends  Exception{


    public AutomationRuleParamException() {
        super();
    }

    public AutomationRuleParamException(String message) {
        super(message);
    }

    public AutomationRuleParamException(Throwable cause) {
        super(cause);
    }

    public AutomationRuleParamException(String message, Throwable cause) {
        super(message, cause);
    }
}
