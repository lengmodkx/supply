package com.art1001.supply.shiro;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserSessionEntity;
import com.art1001.supply.service.user.UserSessionService;
import com.art1001.supply.util.SpringContextUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
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

	/*
	 * 用户权限管理
	 */
	public static final MyDBRealm myDBRealm = SpringContextUtil.getBean("myDBRealm", MyDBRealm.class);
	/*
	 * 用户session管理
	 */
	public static final UserSessionService userSessionService = SpringContextUtil.getBean("userSessionService", UserSessionService.class);

	/**
	 * 获取shiro的session
	 * 
	 * @return
	 */
	public static Session getSession() {
		return SecurityUtils.getSubject().getSession();
	}

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
		UserEntity userEntity = new UserEntity();
		try {
			Object p = SecurityUtils.getSubject().getPrincipal();
			BeanUtils.copyProperties(userEntity,p);
		}catch (Exception e){
			e.printStackTrace();
		}
		return userEntity;
	}

	/**
	 * 获取用户id
	 * 
	 * @return
	 */
	public static String getUserId() {
		return getUserEntity() == null ? null : getUserEntity().getId();
	}
	
	/**
	 * 获取用户id
	 * 
	 * @return
	 */
	public static String getUserAccountName() {
		return getUserEntity() == null ? null : getUserEntity().getAccountName();
	}

	/**
	 * 把值放入到当前登录用户的Session里
	 * 
	 * @param key
	 * @param value
	 */
	public static void setSessionAttribute(Object key, Object value) {
		getSession().setAttribute(key, value);
	}

	/**
	 * 从当前登录用户的Session里取值
	 * 
	 * @param key
	 * @return
	 */
	public static Object getSessionAttribute(Object key) {
		return getSession().getAttribute(key);
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

	/**
	 * 获取验证码，获取后删除
	 * 
	 * @return
	 */
	public static String getKaptcha(String key) {
		String kaptcha = getSessionAttribute(key).toString();
		getSession().removeAttribute(key);
		return kaptcha;
	}
	
	
	/**
	 * 
	 * @Description	清空当前用户权限信息
	 * </br>目的：为了在判断权限的时候，系统会再次调用 <code>doGetAuthorizationInfo(...)  </code>方法加载权限信息。
	 *
	 * @author wjggwm
	 * @data 2017年1月5日 下午6:09:55
	 */
	public static void clearUserAuth(){
		myDBRealm.clearCachedAuthorizationInfo();
	}
	
	
	/**
	 * 
	 * @Description 根据UserIds清空权限信息
	 * @param userIds	用户ids
	 *
	 * @author wjggwm
	 * @data 2017年1月5日 下午6:07:45
	 */
	public static void clearUserAuthByUserId(String...userIds){
		
		if(null == userIds || userIds.length == 0)	{
			return ;
		}
		List<SimplePrincipalCollection> result = userSessionService.getSimplePrincipalCollectionByUserId(userIds);

		for (SimplePrincipalCollection simplePrincipalCollection : result) {
			myDBRealm.clearCachedAuthorizationInfo(simplePrincipalCollection);
		}
	}


	/**
	 * 
	 * @Description 根据UserIds清空权限信息
	 * @param userIds	用户ids
	 *
	 * @author wjggwm
	 * @data 2017年1月5日 下午6:08:01
	 */
	public static void clearUserAuthByUserId(List<Long> userIds) {
		if(null == userIds || userIds.size() == 0){
			return ;
		}
		clearUserAuthByUserId(userIds.toArray(new String[0]));
	}
	
	/**
	 * 
	 * @Description 清空所有用户权限信息
	 *
	 * @author wjggwm
	 * @data 2017年1月5日 下午6:08:01
	 */
	public static void clearAllUserAuth() {
		List<UserSessionEntity> list = userSessionService.getAllUser();
		List<String> userIds = new ArrayList<String>();
		list.forEach(user -> {
			userIds.add(user.getId());
		});
		clearUserAuthByUserId(userIds.toArray(new String[0]));
	}
	

}
