package com.art1001.supply.shiro;

import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.shiro.util.JWTToken;
import com.art1001.supply.shiro.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;

/**
 * 自定义身份认证
 * 基于HMAC（ 散列消息认证码）的控制域
 */
@Slf4j
public class JWTShiroRealm extends AuthorizingRealm {

    private UserMapper userMapper;

    @Resource
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public JWTShiroRealm(){
        this.setCredentialsMatcher(new JWTCredentialsMatcher());
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 认证信息.(身份验证) : Authentication 是用来验证用户身份
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        JWTToken jwtToken = (JWTToken) authToken;
        String token = jwtToken.getToken();
        String accountName = JwtUtil.getUsername(token);
        if(accountName == null){
            throw new AuthenticationException("token过期，请重新登录");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setAccountName(accountName);
        return new SimpleAuthenticationInfo(userEntity, accountName, "jwtRealm");
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo();
    }
}
