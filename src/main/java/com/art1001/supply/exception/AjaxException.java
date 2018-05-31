package com.art1001.supply.exception;

/**
 * 
 * ajax异常,针对ajax请求处理的Exception
 * @author wangyafeng
 * 2016年7月12日 下午3:19:14
 *
 */
public class AjaxException extends RuntimeException{
	/**
	 * serialVersionUID 
	 */
	private static final long serialVersionUID = 1L;

	AjaxException() {
		super();
	}

	AjaxException(String message) {
		super(message);
	}

	AjaxException(Throwable cause) {
		super(cause);
	}

	AjaxException(String message, Throwable cause) {
		super(message, cause);
	}

	AjaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    	super(message, cause, enableSuppression, writableStackTrace);
    }
}
