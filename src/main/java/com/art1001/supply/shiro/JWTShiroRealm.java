package com.art1001.supply.shiro;

import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.resource.ResourceMapper;
import com.art1001.supply.mapper.role.RoleMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.shiro.util.JWTToken;
import com.art1001.supply.shiro.util.JwtUtil;
import com.art1001.supply.shiro.util.MyByteSource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Collection;
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

    public void setRoleMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Resource
    private RoleMapper roleMapper;

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
        String username = JwtUtil.getUsername(principals.toString());
        UserEntity user = userMapper.selectOne(new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getAccountName, username));
        List<ResourceEntity> resourceList = resourceMapper.findResourcesByUserId(user.getUserId());
        Collection<String> roles = roleMapper.selectList(new QueryWrapper<>()).stream().map(Role::getRoleKey).collect(Collectors.toList());
        // 权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roles);
//        info.getRoles().forEach(r - > {
//
//        });
        //根据用户ID查询角色（role），放入到Authorization里。
        // 单角色用户情况
        //info.addRole(user.getRole().getKey());
        // 多角色用户情况
        // info.setRoles(user.getRolesName());
        // 用户的角色对应的所有权限

        //或者直接查询出所有权限set集合
        //info.setStringPermissions(permissions);
        //return info;
        return new SimpleAuthorizationInfo();
    }
}
