package com.art1001.supply.service.project.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.organization.OrganizationMemberInfo;
import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.partment.PartmentMember;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.project.ProjectMemberDTO;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.role.ProRoleUser;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.organization.OrganizationMemberInfoMapper;
import com.art1001.supply.mapper.project.OrganizationMemberMapper;
import com.art1001.supply.mapper.project.ProjectMemberMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.organization.OrganizationMemberInfoService;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.partment.PartmentMemberService;
import com.art1001.supply.service.partment.PartmentService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.MyBeanUtils;
import com.art1001.supply.util.ValidatedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * projectMemberServiceImpl
 */
@Service
public class ProjectMemberServiceImpl extends ServiceImpl<ProjectMemberMapper, ProjectMember> implements ProjectMemberService {

    private static final String POINTZERO = "-0.0";
    private static final String ZERO = "0";
    /**
     * projectMemberMapper接口
     */
    @Resource
    private ProjectMemberMapper projectMemberMapper;

    /**
     * 任务逻辑层Bean 注入
     */
    @Resource
    private TaskService taskService;

    /**
     * 项目逻辑层Bean 注入
     */
    @Resource
    private ProjectService projectService;

    /**
     * 文件层逻辑层Bean
     */
    @Resource
    private FileService fileService;

    @Resource
    private ProRoleService proRoleService;

    @Resource
    private RoleUserService roleUserService;

    /**
     * 分享逻辑层Bean
     */
    @Resource
    private ShareService shareService;

    /**
     * 日程逻辑层Bean
     */
    @Resource
    private ScheduleService scheduleService;

    @Resource
    private UserService userService;

    /**
     * 注入用户的逻辑层bean
     */
    @Resource
    private UserMapper userMapper;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private ProRoleUserService proRoleUserService;

    @Resource
    private PartmentService partmentService;

    @Resource
    private OrganizationMemberInfoService organizationMemberInfoService;

    @Resource
    private PartmentMemberService partmentMemberService;

    @Resource
    private OrganizationMemberInfoMapper organizationMemberInfoMapper;

    @Resource
    private OrganizationMemberMapper organizationMemberMapper;

    @Resource
    private RoleService roleService;

    @Resource
    private OrganizationService organizationService;

    private static final String EXTERNALMEMBER="外部成员";

    @Override
    public List<Project> findProjectByMemberId(String memberId, Integer projectDel) {
        return projectMemberMapper.findProjectByMemberId(memberId, projectDel);
    }

    @Override
    public List<ProjectMember> findByProjectId(String projectId) {
        List<ProjectMember> byProjectId = projectMemberMapper.findByProjectId(projectId);
        Optional.ofNullable(byProjectId).ifPresent(projectMembers->{
            projectMembers.forEach(r->{
                ProRoleUser proRoleUser = proRoleUserService.findProRoleUser(r.getProjectId(), r.getMemberId());
                if (Constants.MEMBER_CN.equals(proRoleUser.getRoleName())) {
                    r.setIsManager(1);
                }else{
                    r.setIsManager(0);
                }
            });
        });
        return byProjectId;
    }


    /**
     * @param orgId
     * @param memberId
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @return:
     * @Description: 根据项目id及用户id查询企业成员
     * @create: 15:49 2020/4/28
     */
    @Override
    public OrganizationMember findMemberByProjectIdAndMemberId(String orgId, String memberId) {

        try {
            OrganizationMember memberInfo = organizationMemberMapper.selectOne(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).eq("member_id", memberId));
            if (memberInfo!=null) {
                if (memberInfo.getEntryTime() != null) {
                    memberInfo.setEntryTime(memberInfo.getEntryTime());
                }
                if (!"".equals(memberInfo.getBirthday()) && memberInfo.getBirthday() != null) {
                    memberInfo.setBirthday(memberInfo.getBirthday());
                }
                PartmentMember partmentMember = partmentMemberService.getSimplePartmentMemberInfo(memberInfo.getPartmentId(), memberId);
                if (partmentMember != null) {
                    memberInfo.setDeptName(partmentMember.getPartmentName());
                    if (partmentMember.getIsMaster()) {
                        memberInfo.setParentName(partmentMember.getPartmentName());
                    } else {
                        memberInfo.setParentName(null);
                    }
                }
            }
            return memberInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new OrganizationMember();
    }

    /**
    * @Author: 邓凯欣
    * @Email：dengkaixin@art1001.com
    * @Param: memberName成员名称 memberPhone 成员电话
    * @return: 根据成员名称或成员电话模糊查询项目成员
    * @Description:
    * @create: 16:15 2020/5/15
    */
    @Override
    public List<ProjectMember> searchMemberByName(String condition,String projectId) {
        return projectMemberMapper.searchMemberByName(condition,projectId);
    }

    /**
     * 删除成员
     * @param memberId
     * @param projectId
     * @return
     */
    @Override
    public Integer deleteMember(String memberId, String projectId) {
        ProRole proRole = proRoleUserService.getRoleIdForProjectUser(projectId, memberId);
        if (!Constants.OWNER_CN.equals(proRole.getRoleName())) {
            return removeMemberAndRole(memberId, projectId);
        }
        Integer result=proRoleUserService.getManagersByProject(projectId);
        if (result>1) {
            return removeMemberAndRole(memberId, projectId);
        }else{
            return 0;
        }

    }



    /**
     * 移除项目成员
     * @param memberId
     * @param projectId
     * @return
     */
    @NotNull
    private Integer removeMemberAndRole(String memberId, String projectId) {
        this.remove(new QueryWrapper<ProjectMember>()
                .lambda().eq(ProjectMember::getMemberId, memberId)
                .eq(ProjectMember::getProjectId, projectId));

        proRoleUserService.remove(new QueryWrapper<ProRoleUser>().lambda()
                .eq(ProRoleUser::getUId, memberId).eq(ProRoleUser::getProjectId, projectId));
        return 1;
    }

    /**
     * 查询成员是否存在于项目中
     *
     * @param projectId 项目id
     * @param id        用户id
     * @return
     */
    @Override
    public int findMemberIsExist(String projectId, String id) {
        return projectMemberMapper.findMemberIsExist(projectId, id);
    }

    /**
     * 根据用户查询出 用户在该项目中的默认分组id
     *
     * @param projectId 项目id
     * @param userId    用户id
     * @return
     */
    @Override
    public String findDefaultGroup(String projectId, String userId) {
        ProjectMember projectMember = projectMemberMapper.selectOne(new QueryWrapper<ProjectMember>().select("default_group").eq("project_id", projectId).eq("member_id", userId).ne("default_group", "0"));
        if (projectMember == null) {
            throw new NullPointerException("该项目不存在!");
        }
        return projectMember.getDefaultGroup();
    }

    @Override
    public void updateDefaultGroup(String projectId, String userId, String groupId) {
        ProjectMember projectMember = new ProjectMember();
        projectMember.setDefaultGroup(groupId);
        update(projectMember, new UpdateWrapper<ProjectMember>().eq("project_id", projectId).eq("member_id", userId));
    }

    /**
     * 获取到模块在当前项目的的参与者信息与非参与者信息
     *
     * @param type      模块类型
     * @param id        信息id
     * @param projectId 所在项目id
     * @return 该项目成员在当前模块信息中的参与者信息与非参与者信息
     */
    @Override
    public List<UserEntity> getModelProjectMember(String type, String id, String projectId) {
        List<UserEntity> users = new ArrayList<>();
        if (Constants.TASK.equals(type)) {
            Task task = taskService.getOne(new QueryWrapper<Task>().eq("task_id", id).select("task_uids"));
            if (task != null) {
                users = userMapper.findManyUserById(task.getTaskUIds());
            }
        }
        if (Constants.FILE.equals(type)) {
            File file = fileService.getOne(new QueryWrapper<File>().eq("file_id", id).select("file_uids"));
            if (file != null) {
                users = userMapper.findManyUserById(file.getFileUids());
            }
        }
        if (Constants.SHARE.equals(type)) {
            Share share = shareService.getOne(new QueryWrapper<Share>().eq("share_id", id).select("uids"));
            if (share != null) {
                users = userMapper.findManyUserById(share.getUids());
            }
        }
        if (Constants.SCHEDULE.equals(type)) {
            Schedule schedule = scheduleService.getOne(new QueryWrapper<Schedule>().eq("schedule_id", id).select("member_ids"));
            if (schedule != null) {
                users = userMapper.findManyUserById(schedule.getMemberIds());
            }
        }
        List<UserEntity> newUserList = users;
        List<UserEntity> projectAllMember = userService.findProjectAllMember(projectId);
        return projectAllMember.stream().filter(item -> !newUserList.contains(item)).collect(Collectors.toList());
    }

    /**
     * 查询出一个项目中的所有成员id
     *
     * @param projectId 项目id
     * @return 成员id集合
     */
    @Override
    public List<String> getProjectAllMemberId(String projectId) {
        LambdaQueryWrapper<ProjectMember> eq = new QueryWrapper<ProjectMember>().lambda().select(ProjectMember::getMemberId).eq(ProjectMember::getProjectId, projectId);
        return projectMemberMapper.selectList(eq).stream().map(ProjectMember::getMemberId).collect(Collectors.toList());
    }

    /**
     * 获取当前用户的星标项目
     *
     * @param userId 用户id
     * @return 星标项目
     */
    @Override
    public List<Project> getStarProject(String userId) {
        return projectMemberMapper.getStarProject(userId);
    }

    /**
     * @Author: 邓凯欣
     * @Email： dengkaixin@art1001.com
     * @Param: [projectId, memberId, orgId]
     * @return: java.lang.Integer
     * @Description: 新加的修改，将数据添加到企业用户详情表
     * @create: 15:41 2020/4/22
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer saveMember(String projectId, String memberId, String orgId) {
        Integer roleId = proRoleService.getProDefaultRoleId(orgId);
        ProRoleUser proRoleUser = new ProRoleUser();
        proRoleUser.setProjectId(projectId);
        proRoleUser.setRoleId(roleId);
        proRoleUser.setUId(memberId);
        proRoleUser.setTCreateTime(LocalDateTime.now());
        proRoleUserService.save(proRoleUser);

        //查询出当前项目的默认分组
        String groupId = projectService.findDefaultGroup(projectId);
        ProjectMember member = new ProjectMember();
        member.setDefaultGroup(groupId);
        member.setProjectId(projectId);
        member.setMemberId(memberId);
        member.setCreateTime(System.currentTimeMillis());
        member.setUpdateTime(System.currentTimeMillis());
        member.setMemberLabel(0);

        ProRole byId = proRoleService.getById(roleId);
        member.setRoleKey(byId.getRoleKey());
        projectMemberMapper.insert(member);

        //如果是企业外部成员，权限变为外部成员权限
        if (organizationMemberService.findOrgOtherByMemberId(memberId, orgId)==0) {
            List<Role> roles = roleService.list(new QueryWrapper<Role>().eq("organization_id", orgId));
            Optional.ofNullable(roles).ifPresent(r->r.stream().forEach(e->{
                if (e.getRoleName().equals(EXTERNALMEMBER)) {
                    RoleUser roleUser=new RoleUser();
                    roleUser.setUId(memberId);
                    roleUser.setTCreateTime(LocalDateTime.now());
                    roleUser.setRoleId(e.getRoleId());
                    roleUser.setOrgId(orgId);
                    roleUserService.save(roleUser);
                }
            }));
        }
        return 1;
    }

    @Override
    public String getUserCurrentProjectId() {
        //构造出查询用户所在项目id的sql表达式
        LambdaQueryWrapper<ProjectMember> selectUserCurrentProjectIdQw = new QueryWrapper<ProjectMember>().lambda()
                .eq(ProjectMember::getMemberId, ShiroAuthenticationManager.getUserId())
                .eq(ProjectMember::getCurrent, true)
                .select(ProjectMember::getProjectId);

        ProjectMember projectMember = projectMemberMapper.selectOne(selectUserCurrentProjectIdQw);
        if (projectMember != null && StringUtils.isNotEmpty(projectMember.getProjectId())) {
            return projectMember.getProjectId();
        }
        return null;
    }

    /**
     * 1.查询原来的所在项目和要修改的项目id是否一致,如果一致则不需要修改
     * 2.如果不一致,则先把原先的所在项目记录标记去除,然后在给新的项目标记为所在项目
     *
     * @param projectId 要更新为所在项目的项目id
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateUserCurrentProject(String projectId) {
        if (projectService.notExist(projectId)) {
            return -1;
        }

        boolean userProjectExist = this.checkUserProjectBindIsExist(projectId, ShiroAuthenticationManager.getUserId());
        if (userProjectExist) {
            //获取到修改前用户所在的项目id
            String userCurrentProjectId = this.getUserCurrentProjectId();
            boolean newPidEqualOldPid = StringUtils.isNotEmpty(userCurrentProjectId) && projectId.equals(userCurrentProjectId);
            if (newPidEqualOldPid) {
                return 1;
            }

            String proIdCondition = userCurrentProjectId;

            //构造出查询用户和要更新的项目对应关系的sql表达式
            LambdaUpdateWrapper<ProjectMember> updateUserProjectCurrentUw = new UpdateWrapper<ProjectMember>().lambda()
                    .eq(ProjectMember::getProjectId, proIdCondition)
                    .eq(ProjectMember::getMemberId, ShiroAuthenticationManager.getUserId());

            //要更新的字段信息,current首先设置为false等同于 取消原先用户所在项目的标记
            ProjectMember projectMember = new ProjectMember();
            projectMember.setCurrent(false);
            projectMember.setUpdateTime(System.currentTimeMillis());
            projectMemberMapper.update(projectMember, updateUserProjectCurrentUw);

            //更新projectId条件,标记新的用户所在项目
            proIdCondition = projectId;
            projectMember.setCurrent(true);
            projectMember.setUpdateTime(System.currentTimeMillis());
            projectMemberMapper.update(projectMember, updateUserProjectCurrentUw);
            return 1;
        }
        return -1;

    }

    @Override
    public Integer updateTargetProjectCurrent(String projectId, String userId) {
        if (projectService.notExist(projectId)) {
            return -1;
        }

        //构造出更新记录的sql表达式
        LambdaUpdateWrapper<ProjectMember> upUserProjectUw = new UpdateWrapper<ProjectMember>().lambda()
                .eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getMemberId, userId);

        //给要更新的字段信息赋值
        ProjectMember projectMember = new ProjectMember();
        projectMember.setCurrent(true);
        projectMember.setUpdateTime(System.currentTimeMillis());
        return projectMemberMapper.update(projectMember, upUserProjectUw);
    }

    @Override
    public Boolean checkUserProjectBindIsExist(String projectId, String userId) {
        //构造出查询用户和projectId的关系是否存在的条件表达式
        LambdaQueryWrapper<ProjectMember> selectUserProjectIsExistQw = new QueryWrapper<ProjectMember>().lambda()
                .eq(ProjectMember::getMemberId, userId);

        return projectMemberMapper.selectCount(selectUserProjectIsExistQw) > 0;
    }

    @Override
    public Integer getUserProjectCount() {
        String userId = ShiroAuthenticationManager.getUserId();
        //构造出查询用户相关项目数的sql表达式
        LambdaQueryWrapper<ProjectMember> selectUserProjectCountQw = new QueryWrapper<ProjectMember>().lambda()
                .eq(ProjectMember::getMemberId, userId);
        return projectMemberMapper.selectCount(selectUserProjectCountQw);
    }

    @Override
    public List<String> getProRoleUsers(Integer proRoleId) {
        //构造出sql表达式
        LambdaQueryWrapper<ProjectMember> selectMemberByRoleQw = new QueryWrapper<ProjectMember>().lambda()
                .eq(ProjectMember::getRoleId, proRoleId)
                .select(ProjectMember::getMemberId);

        return projectMemberMapper.selectList(selectMemberByRoleQw).stream()
                .map(ProjectMember::getMemberId)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateUserToNewDefaultRole(List<String> userIds, Integer roleId, String orgId) {
        if (CollectionUtils.isEmpty(userIds)) {
            return -1;
        }

        userIds.forEach(userId -> {
            RoleUser roleUser = new RoleUser();
            roleUser.setRoleId(roleId);

            //生成sql表达式
            LambdaUpdateWrapper<RoleUser> upToNewDefaultRoleUw = new UpdateWrapper<RoleUser>().lambda()
                    .eq(RoleUser::getRoleId, roleId)
                    .eq(RoleUser::getOrgId, orgId);
            roleUserService.update(roleUser, upToNewDefaultRoleUw);
        });
        return 1;
    }

    /**
     * 获取当前用户的非星标项目
     *
     * @param userId 用户id
     * @return 非星标项目
     */
    @Override
    public List<Project> getNotStarProject(String userId) {
        return projectMemberMapper.getNotStarProject(userId);
    }

    @Override
    public List<UserEntity> getMembers(String userId) {
        //获取到和当前用户有关的项目信息
        List<Project> userProjects = projectService.listProjectByUid(userId);
        if (CollectionUtils.isEmpty(userProjects)) {
            return new ArrayList<>();
        }

        //userProjects中包含了项目的所有的信息，因为这里需要根据项目id集合查询项目成员，所以只需要提取出项目id集合。
        List<String> projectIds = userProjects.stream().map(Project::getProjectId).collect(Collectors.toList());
        List<String> memberIdListByProIdList = this.getMemberIdListByProjectIdList(projectIds);

        //根据多个项目中成员id的集合，获取到成员信息返回
        return userService.getUserListByIdList(memberIdListByProIdList);
    }

    @Override
    public List<String> getMemberIdListByProjectIdList(Collection<String> projectIdList) {
        if (CollectionUtils.isEmpty(projectIdList)) {
            return new ArrayList<>();
        }
        return projectMemberMapper.selectMemberIdListByProjectIdList(projectIdList);
    }

    @Override
    public List<String> getUserProjectIdList(String userId) {
        ValidatedUtil.filterNullParam(userId);

        LambdaQueryWrapper<ProjectMember> selectUserProjectIdListQw = new QueryWrapper<ProjectMember>().lambda()
                .eq(ProjectMember::getMemberId, userId)
                .select(ProjectMember::getProjectId);

        List<ProjectMember> projectMemberList = this.list(selectUserProjectIdListQw);

        //提取出项目id列表
        List<String> userProjectIdList = projectMemberList.stream()
                .map(ProjectMember::getProjectId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(userProjectIdList)) {
            return new ArrayList();
        }

        return userProjectIdList;
    }

    @Override
    public List<UserEntity> getProjectUserInfo(String projectId, String keyWord) {
        return projectMemberMapper.selectProjectUserInfo(projectId, keyWord);
    }

    @Override
    public void updateAll(String userId, String id) {
        projectMemberMapper.updateAll(userId, id);
    }

    @Override
    public List<Project> getUserProjectsInOrg(String userId, String orgId) {
        ValidatedUtil.filterNullParam(userId, orgId);

        if (userService.checkUserIsExist(userId) == 0) {
            throw new ServiceException("用户不存在!");
        }

        Optional.ofNullable(organizationMemberService.findOrgByMemberId(userId, orgId))
                .orElseThrow(() -> new ServiceException("用户不在该企业中"));

        LambdaQueryWrapper<ProjectMember> getAllProMember = new QueryWrapper<ProjectMember>()
                .lambda().eq(ProjectMember::getMemberId, userId);

        List<ProjectMember> members = this.list(getAllProMember);

        if (CollectionUtils.isEmpty(members)) {
            new LinkedList<>();
        }

        List<String> projectIdList = members.stream().map(ProjectMember::getProjectId).collect(Collectors.toList());

        LambdaQueryWrapper<Project> getProjectListQW = new QueryWrapper<Project>()
                .lambda().in(Project::getProjectId, projectIdList).eq(Project::getOrganizationId, orgId);

        List<Project> list = projectService.list(getProjectListQW);
        Optional.ofNullable(list).ifPresent(projects->{
            projects.stream().forEach(r->{
                r.setProjectSchedule(organizationService.automaticUpdateProjectSchedule(r));
            });
        });
        return list;
    }
}