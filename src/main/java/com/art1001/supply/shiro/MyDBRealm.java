package com.art1001.supply.shiro;

import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.shiro.util.MyByteSource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 
 * @ClassName: MyRealm
 * @Description: 自定义jdbcRealm,认证&授权
 * @author wangyafeng
 * @date 2016年7月12日 下午4:30:16
 *
 */
public class MyDBRealm extends AuthorizingRealm {

	private UserMapper userMapper;

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	/**
	 * 授权信息
	 * 只有需要验证权限时才会调用, 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.在配有缓存的情况下，只加载一次.
	 */

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		return new SimpleAuthorizationInfo();
	}

	/**
	 * 认证信息,认证回调函数,登录时调用
	 * </br>首先根据传入的用户名获取User信息；然后如果user为空，那么抛出没找到帐号异常UnknownAccountException；
	 * </br>如果user找到但锁定了抛出锁定异常LockedAccountException；最后生成AuthenticationInfo信息，
	 * </br>交给间接父类AuthenticatingRealm使用CredentialsMatcher进行判断密码是否匹配，
	 * </br>如果不匹配将抛出密码错误异常IncorrectCredentialsException；
	 * </br>另外如果密码重试次数太多将抛出超出重试次数异常ExcessiveAttemptsException；
	 * </br>在组装SimpleAuthenticationInfo信息时， 需要传入：身份信息（用户名）、凭据（密文密码）、加密盐（username+salt），
	 * </br>CredentialsMatcher使用盐加密传入的明文密码和此处的密文密码进行匹配。
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) {
		UsernamePasswordToken token = (UsernamePasswordToken)auth;
		String username = token.getUsername();
		UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getAccountName,username));
		if(userEntity == null)
			throw new AuthenticationException("用户名或者密码错误");
		return new SimpleAuthenticationInfo(userEntity, userEntity.getPassword(), // 密码
				new MyByteSource(username + userEntity.getCredentialsSalt()),
				"dbRealm");
	}

	/**
     * 清除当前用户认证信息
     */
	public void clearCachedAuthenticationInfo() {
		PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
		SimplePrincipalCollection principals = new SimplePrincipalCollection(
				principalCollection, getName());
		super.clearCachedAuthenticationInfo(principals);
	}

	/**
     * 清除用户认证信息
     */
	@Override
	public void clearCachedAuthenticationInfo(PrincipalCollection principalCollection) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(
				principalCollection, getName());
		super.clearCachedAuthenticationInfo(principals);
	}
}