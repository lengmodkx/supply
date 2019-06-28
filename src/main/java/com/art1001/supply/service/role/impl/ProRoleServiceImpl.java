package com.art1001.supply.service.role.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.mapper.role.ProRoleMapper;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.resource.ProResourcesRoleService;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@Service
public class ProRoleServiceImpl extends ServiceImpl<ProRoleMapper, ProRole> implements ProRoleService {

    /**
     * 注入项目 业务层Bean
     */
    @Resource
    private ProjectService projectService;

    /**
     * 注入自己解决 事物this调用失效问题
     */
    @Resource
    private ProRoleService proRoleService;

    /**
     * 注入项目成员关系业务层Bean
     */
    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private ProRoleMapper proRoleMapper;

    /**
     * 注入角色资源关系业务层Bean
     */
    @Resource
    private ProResourcesRoleService proResourcesRoleService;

    @Override
    public Integer initProRole(String projectId) {
        if(Stringer.isNullOrEmpty(projectId)){
            return -1;
        }
        if(!projectService.checkIsExist(projectId)){
            return -1;
        }
        //这里手动添加默认角色信息,后期可以读取配置文件进行添加
        String[] initRoles = {"拥有者","管理员","成员"};
        for (String roleName : initRoles) {
            ProRole role = new ProRole();
            role.setRoleName(roleName);
            switch (roleName){
                case Constants.OWNER_CN:
                    role.setRoleKey("administrator");
                    role.setRoleDes("项目的拥有者");
                    break;
                case Constants.ADMIN_CN:
                    role.setRoleKey("admin");
                    role.setRoleDes("项目的管理者");
                    break;
                default:
                    role.setRoleKey("member");
                    role.setIsDefault(true);
                    role.setRoleDes("项目的普通成员");
                    break;
            }
            role.setProjectId(projectId);
            role.setCreateTime(LocalDateTime.now());
            role.setUpdateTime(LocalDateTime.now());
            role.setIsSystemInit(true);
            proRoleMapper.insert(role);
        }
        return proResourcesRoleService.saveBatchBind(projectId);
    }

    @Override
    public List<ProRole> getProjectInitRoleId(String projectId) {
        if(Stringer.isNullOrEmpty(projectId)){
            return null;
        }

        //构造出查询项目初始化角色id集合的sql表达式
        LambdaQueryWrapper<ProRole> selectProjectInitRoleId = new QueryWrapper<ProRole>().lambda()
                .eq(ProRole::getProjectId, projectId)
                .eq(ProRole::getIsSystemInit, true)
                .select(ProRole::getRoleId,ProRole::getRoleKey);

        List<ProRole> proRoleIds = proRoleMapper.selectList(selectProjectInitRoleId);
        if(CollectionUtils.isEmpty(proRoleIds)){
            return new ArrayList<>();
        }
        return proRoleIds;
    }

    @Override
    public Integer getDefaultProRoleId(String projectId) {
        if(Stringer.isNullOrEmpty(projectId)){
            return null;
        }

        //生成根据projectId查询该项目下的默认角色id的sql表达式
        LambdaQueryWrapper<ProRole> selectDefaultRoleByProjectId = new QueryWrapper<ProRole>().lambda()
                .eq(ProRole::getProjectId, projectId)
                .eq(ProRole::getIsDefault, true)
                .select(ProRole::getRoleId);

        return proRoleMapper.selectOne(selectDefaultRoleByProjectId).getRoleId();
    }

    @Override
    public Integer getRoleIdByRoleKey(String key, String projectId) {
        if(Stringer.isNullOrEmpty(key) || Stringer.isNullOrEmpty(projectId)){
            return -1;
        }

        //构造出查询项目中为key的角色id的sql表达式
        LambdaQueryWrapper<ProRole> selectRoleIdByKeyAndProjectIdQw = new QueryWrapper<ProRole>().lambda()
                .eq(ProRole::getProjectId, projectId)
                .eq(ProRole::getRoleKey, key)
                .select(ProRole::getRoleId);
        ProRole proRole = proRoleMapper.selectOne(selectRoleIdByKeyAndProjectIdQw);

        if(proRole != null && proRole.getRoleId() != null){
            return proRole.getRoleId();
        }
        return -1;
    }

    @Override
    public Integer addProRole(ProRole proRole) {
        proRole.setCreateTime(LocalDateTime.now());
        //查询该角色的key在项目中是否存在
        boolean roleNotExist = this.checkIsExist(proRole.getProjectId(),proRole.getRoleKey()) == 0;
        if(roleNotExist){
            return proRoleMapper.insert(proRole);
        } else {
            return 0;
        }
    }

    @Override
    public Integer checkIsExist(String projectId, String roleKey) {
        if(Stringer.isNullOrEmpty(projectId) || Stringer.isNullOrEmpty(roleKey)){
            return -1;
        }

        //构造出查询当前项目中有没有存在roleKey记录的sql表达式
        LambdaQueryWrapper<ProRole> selectRoleCountByPidAndKeyQw = new QueryWrapper<ProRole>().lambda()
                .eq(ProRole::getProjectId, projectId)
                .eq(ProRole::getRoleKey, roleKey);
        return proRoleMapper.selectCount(selectRoleCountByPidAndKeyQw);
    }

    /**
     * 如果有用户为该角色那么该角色被删除后对应的用户都会被移动到项目默认角色下
     * 如果删除的自定义角色正好是默认角色则删除后项目的默认角色应设置为key为member的角色, 并且被删除的角色会转移到key为member的角色下
     * @param roleId 角色id
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer removeProRole(Integer roleId, String projectId) {
        List<String> proRoleUsers = projectMemberService.getProRoleUsers(roleId);
        Boolean isDefault = this.checkRoleIdIsDefault(roleId);
        if(isDefault){
            proRoleService.setProDefaultRole(projectId, Constants.MEMBER_KEY);
        }
        proRoleMapper.deleteById(roleId);
        Integer defaultProRoleId = this.getDefaultProRoleId(projectId);
        projectMemberService.updateUserToNewDefaultRole(proRoleUsers, defaultProRoleId, projectId);
        return 1;
    }

    @Override
    public Boolean checkRoleIdIsDefault(Integer roleId) {
        if(Stringer.isNullOrEmpty(roleId)){
            return false;
        }
        //构造出sql表达式
        LambdaQueryWrapper<ProRole> selectIsDefault = new QueryWrapper<ProRole>().lambda()
                .eq(ProRole::getRoleId, roleId)
                .select(ProRole::getIsDefault);

        ProRole proRole = proRoleMapper.selectOne(selectIsDefault);
        if(proRole != null && proRole.getIsDefault() != null){
            return proRole.getIsDefault();
        }
        return false;
    }

    @Override
    public Integer setProDefaultRole(String projectId, String roleKey) {
        if(Stringer.isNullOrEmpty(projectId) || Stringer.isNullOrEmpty(roleKey)){
            return -1;
        }

        boolean notExist = this.checkIsExist(projectId, roleKey) == 0;
        if(notExist){
            return -1;
        }

        Integer proDefaultRoleId = this.getProDefaultRoleId(projectId);
        if(proDefaultRoleId == -1){
            return proDefaultRoleId;
        }

        ProRole proRole = new ProRole();
        proRole.setUpdateTime(LocalDateTime.now());
        proRole.setIsDefault(true);

        //生成sql表达式
        LambdaUpdateWrapper<ProRole> cacelDefaultUw = new UpdateWrapper<ProRole>().lambda()
                .eq(ProRole::getRoleId, proDefaultRoleId);
        proRole.setIsDefault(false);
        proRoleMapper.update(proRole, cacelDefaultUw);

        //生成sql表达式
        LambdaUpdateWrapper<ProRole> upProDefaultRoleUw = new UpdateWrapper<ProRole>().lambda()
                .eq(ProRole::getProjectId, projectId)
                .eq(ProRole::getRoleKey, roleKey);
        proRole.setIsDefault(true);

        return proRoleMapper.update(proRole, upProDefaultRoleUw);
    }

    @Override
    public Integer getProDefaultRoleId(String projectId) {
        if(Stringer.isNullOrEmpty(projectId)){
            return -1;
        }

        //构造出sql表达式
        LambdaQueryWrapper<ProRole> selectProDefaultRoleIdQw = new QueryWrapper<ProRole>().lambda()
                .eq(ProRole::getProjectId, projectId)
                .eq(ProRole::getIsDefault, true)
                .select(ProRole::getRoleId);

        ProRole proRole = proRoleMapper.selectOne(selectProDefaultRoleIdQw);
        if(proRole != null && Stringer.isNotNullOrEmpty(proRole.getRoleId())){
            return proRole.getRoleId();
        }
        return -1;
    }

    @Override
    public Boolean checkRoleIsExistByRoleId(Integer roleId) {
        if(Stringer.isNullOrEmpty(roleId)){
            return false;
        }
        return proRoleMapper.selectById(roleId) != null;
    }

    @Override
    public Boolean checkRoleIsSystemInit(Integer roleId) {
        if(Stringer.isNullOrEmpty(roleId)){
            return false;
        }

        ProRole proRole = proRoleMapper.selectById(roleId);
        if(proRole != null){
            return proRole.getIsSystemInit();
        }
        return false;
    }

    @Override
    public Integer updateRoleName(Integer roleId, String roleName) {
        ProRole proRole = new ProRole();
        proRole.setRoleId(roleId);
        proRole.setRoleName(roleName);
        proRole.setUpdateTime(LocalDateTime.now());
        return proRoleMapper.updateById(proRole);
    }

    @Override
    public Boolean checkRoleIsNotExistByRoleId(Integer roleId) {
        return !this.checkRoleIsExistByRoleId(roleId);
    }

    @Override
    public List<ProRole> getProRoles(String projectId) {
        return proRoleMapper.selectProRoles(projectId);
    }
}
