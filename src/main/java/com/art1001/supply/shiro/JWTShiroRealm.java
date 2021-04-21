package com.art1001.supply.shiro;

import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.shiro.util.JWTToken;
import com.art1001.supply.shiro.util.JwtUtil;
import com.art1001.supply.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * 自定义身份认证
 * 基于HMAC（ 散列消息认证码）的控制域
 */
@Slf4j
public class JWTShiroRealm extends AuthorizingRealm {

    public JWTShiroRealm(RedisUtil redisUtil){
        this.setCredentialsMatcher(new JWTCredentialsMatcher(redisUtil));
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 认证信息.(身份验证) : Authentication 是用来验证用户身份
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        JWTToken jwtToken = (JWTToken) authToken;
        String token = jwtToken.getToken();
        String userId = JwtUtil.getUserId(token);
        if(userId == null){
            throw new AuthenticationException("token过期，请重新登录");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        return new SimpleAuthenticationInfo(userEntity, userId, "jwtRealm");
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        return new SimpleAuthorizationInfo();
    }
}
