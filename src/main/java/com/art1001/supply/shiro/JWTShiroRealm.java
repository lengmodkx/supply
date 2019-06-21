package com.art1001.supply.shiro;

import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.project.OrganizationMemberMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.shiro.util.JWTToken;
import com.art1001.supply.shiro.util.JwtUtil;
import com.art1001.supply.shiro.util.MyByteSource;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义身份认证
 * 基于HMAC（ 散列消息认证码）的控制域
 */
@Slf4j
public class JWTShiroRealm extends AuthorizingRealm {

    private UserMapper userMapper;


    @Resource
    private OrganizationMemberMapper organizationMemberMapper;

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

        if(Stringer.isNotNullOrEmpty(userEntity)){
            //生成查询用户默认企业的条件表达式
            LambdaQueryWrapper<OrganizationMember> eq = new QueryWrapper<OrganizationMember>().lambda()
                    .eq(OrganizationMember::getMemberId, userEntity.getUserId())
                    .eq(OrganizationMember::getUserDefault, 1);
            OrganizationMember organizationMember = organizationMemberMapper.selectOne(eq);
            if(Stringer.isNotNullOrEmpty(organizationMember)){
                userEntity.setDefaultOrgId(organizationMember.getOrganizationId());
            }
        }

        if(userEntity == null){
            throw new AuthenticationException("token过期，请重新登录");
        }

        return new SimpleAuthenticationInfo(userEntity, userEntity.getPassword(),
                new MyByteSource(userName + userEntity.getCredentialsSalt()),
                "jwtRealm");
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        UserEntity userEntity = principals.oneByType(UserEntity.class);

        //查询出该用户的所有角色以及权限信息
        List<Role> roles = roleService.getUserOrgRoles(userEntity.getUserId(),userEntity.getDefaultOrgId());
        //获取到用户角色key并且去重后的集合
        List<String> roleKeys = roles.stream().map(Role::getRoleKey).distinct().collect(Collectors.toList());
        simpleAuthorizationInfo.setRoles(new HashSet<>(roleKeys));
        List<String> resources = new ArrayList<>();
        roles.forEach(r -> r.getResources().forEach(s -> resources.add(s.getResourceKey())));
        simpleAuthorizationInfo.setStringPermissions(new HashSet<>(resources));
        return simpleAuthorizationInfo;
    }

    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        UserEntity userEntity = principals.oneByType(UserEntity.class);
        if(Stringer.isNullOrEmpty(userEntity.getDefaultOrgId())){
            return true;
        } else {
            AuthorizationInfo authorizationInfo = getAuthorizationInfo(principals);
            Permission p = getPermissionResolver().resolvePermission(permission);
            return isPermitted(p, authorizationInfo);
        }
    }

    /**
     * 清除当前用户权限信息
     */
    @SuppressWarnings("all")
    public void clearCachedAuthorizationInfo() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        SimplePrincipalCollection principals = new SimplePrincipalCollection(
                principal, getName());
        clearCachedAuthorizationInfo(principals);
    }
}
