package com.art1001.supply.shiro;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserSessionEntity;
import com.art1001.supply.service.user.UserSessionService;
import com.art1001.supply.util.SpringContextUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @ClassName: ShiroManager
 * @Description: shiro 认证信息操作工具类
 * @author wangyafeng
 * @date 2016年7月12日 下午4:31:10
 *
 */
public class ShiroAuthenticationManager {

	/**
	 * 获取shiro Subject
	 * 
	 * @return
	 */
	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	/**
	 * 获取用户
	 * 
	 * @return
	 */
	public static UserEntity getUserEntity() {
		UserEntity principal = (UserEntity) SecurityUtils.getSubject().getPrincipal();
		principal.setPassword(null);
		return principal;
	}

	/**
	 * 获取用户id
	 * 
	 * @return
	 */
	public static String getUserId() {
		return getUserEntity().getUserId();
	}


	/**
	 * 判断是否登录
	 * 
	 * @return
	 */
	public static boolean isLogin() {
		return null != SecurityUtils.getSubject().getPrincipal();
	}

	/**
	 * 退出登录
	 */
	public static void logout() {
		SecurityUtils.getSubject().logout();
	}

	

	

}
