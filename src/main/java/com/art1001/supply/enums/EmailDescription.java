package com.art1001.supply.enums;

/**
 * 
 * 邮件提示信息
 * @author wangyafeng
 * 2016年7月12日 下午3:10:06
 *
 */
public enum EmailDescription {

	ADD_EMAIL("新建账户通知","您好,您的账户已创建,账户名: %s ,密码: %s ,请尽快登录系统修改密码,谢谢."),
	UPDATE_EMAIL("密码重置通知","您好，您的密码已重置，新密码是: %s ");
	
	private String subject;
	
	private String message;
	
	/**
	 * 
	 * @param subject	主题
	 * @param message	提示信息
	 */
	private EmailDescription(String subject, String message)
	{
		this.subject = subject;
		this.message = message;
	}
	
	public String getSubject()
	{
		return this.subject;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	
	
}
