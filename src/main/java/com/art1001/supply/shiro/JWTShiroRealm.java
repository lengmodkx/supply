package com.art1001.supply.shiro;

import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.shiro.util.JWTToken;
import com.art1001.supply.shiro.util.JwtUtil;
import com.art1001.supply.shiro.util.MyByteSource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

public class JWTShiroRealm extends AuthorizingRealm {

    @Resource
    private UserMapper userMapper;

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
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {    
        JWTToken jwtToken = (JWTToken) authcToken;
        String token = jwtToken.getToken();
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("account_name",JwtUtil.getUsername(token)));

        if(userEntity == null)
            throw new AuthenticationException("token过期，请重新登录");

        return new SimpleAuthenticationInfo(userEntity, userEntity.getPassword(), // 密码
                new MyByteSource(userEntity.getAccountName() + userEntity.getCredentialsSalt()),
                this.getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo();
    }
}
