package com.art1001.supply.communication.service;

/**
 * This interface is created for RestAPI of User Integration, it should be
 * synchronized with the API list.
 * 
 * @author Eric23 2016-01-05
 */
public interface IMUserService {

	/**
	 * 注册IM用户[单个] <br>
	 * POST
	 *
	 * @param payload
	 *            <code>{"username":"${用户名}","password":"${密码}", "nickname":"${昵称值}"}</code>
	 * @return
	 */
	Object createNewIMUserSingle(Object payload);

	/**
	 * 注册IM用户[批量] <br>
	 * POST
	 *
	 * @param payload
	 *            <code>[{"username":"${用户名1}","password":"${密码}"},…,{"username":"${用户名2}","password":"${密码}"}]</code>
	 * @return
	 */
	Object createNewIMUserBatch(Object payload);


}
