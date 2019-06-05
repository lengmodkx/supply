package com.art1001.supply.shiro;

import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.resource.ResourceMapper;
import com.art1001.supply.mapper.role.RoleMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.shiro.util.JWTToken;
import com.art1001.supply.shiro.util.JwtUtil;
import com.art1001.supply.shiro.util.MyByteSource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import org.apache.catalina.User;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义身份认证
 * 基于HMAC（ 散列消息认证码）的控制域
 */
public class JWTShiroRealm extends AuthorizingRealm {

    private UserMapper userMapper;

    @Resource
    private ResourceMapper resourceMapper;

    public void setResourceMapper(ResourceMapper resourceMapper) {
        this.resourceMapper = resourceMapper;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Lazy
    @Resource
    private RoleService roleService;

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
        String userName = JwtUtil.getUsername(token);
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("account_name",userName));

        if(userEntity == null)
            throw new AuthenticationException("token过期，请重新登录");

        return new SimpleAuthenticationInfo(userEntity, userEntity.getPassword(), // 密码
                new MyByteSource(userName + userEntity.getCredentialsSalt()),
                "jwtRealm");
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        UserEntity userEntity = (UserEntity)principals.oneByType(UserEntity.class);
        //查询出该用户的所有角色以及权限信息
        List<Role> roles = roleService.getUserRoles(userEntity.getUserId());
        //获取到用户角色key并且去重后的集合
        List<String> roleKeys = roles.stream().map(Role::getRoleKey).distinct().collect(Collectors.toList());
        simpleAuthorizationInfo.setRoles(new HashSet<>(roleKeys));
        List<String> resources = new ArrayList<>();
        roles.forEach(r -> {
            r.getResources().forEach(s -> {
                resources.add(s.getResourceKey());
            });
        });
        simpleAuthorizationInfo.setStringPermissions(new HashSet<>(resources));
        return simpleAuthorizationInfo;
    }

    /**
     * 清除当前用户权限信息
     */
    public void clearCachedAuthorizationInfo() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        SimplePrincipalCollection principals = new SimplePrincipalCollection(
                principal, getName());
        clearCachedAuthorizationInfo(principals);
    }
}
