package com.art1001.supply.service.project.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.partment.PartmentMember;
import com.art1001.supply.entity.project.*;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.role.ProRoleUser;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.vo.MenuVo;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.project.OrganizationMemberMapper;
import com.art1001.supply.mapper.project.ProjectMapper;
import com.art1001.supply.mapper.role.RoleMapper;
import com.art1001.supply.mapper.role.RoleUserMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.partment.PartmentMemberService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.project.ProjectSimpleInfoService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.resource.ProResourcesService;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * projectServiceImpl
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    /**
     * projectMapper接口
     */
    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private TaskService taskService;

    @Resource
    private ProResourcesService proResourcesService;

    @Resource
    private FileService fileService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RelationService relationService;

    @Resource
    private ProRoleService proRoleService;

    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private ProRoleUserService proRoleUserService;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private PartmentMemberService partmentMemberService;

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectSimpleInfoService projectSimpleInfoService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleService roleService;

    @Resource
    private RoleUserService roleUserService;

    @Resource
    private OrganizationMemberMapper organizationMemberMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleUserMapper roleUserMapper;
    /**
     * 通过projectId获取单条project数据
     *
     * @param projectId
     * @return
     */
    @Override
    public Project findProjectByProjectId(String projectId) {
        Project project = getOne(new QueryWrapper<Project>().eq("project_id",projectId).eq("project_del",0));
        Optional.ofNullable(project).ifPresent(p -> {
            UserEntity userEntity = userMapper.selectById(p.getMemberId());
            p.setImage(userEntity.getImage());
            p.setMemberName(userEntity.getUserName());
        });
        return project;
    }

    /**
     * 通过projectId删除project数据
     *
     * @param projectId
     */
    @Override
    public void deleteProjectByProjectId(String projectId) {
        projectMapper.deleteById(projectId);
    }

    /**
     * 修改project
     */
    @Override
    public void updateProject(Project project) {
//        projectMapper.updateById(project);
        projectMapper.updateProject(project);
    }

    /**
     * 保存project数据
     *
     * @param project 项目信息
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void saveProject(Project project) {

        if (!StringUtils.isEmpty(project.getParentId())) {
            if (this.checkIsSubProject(project.getParentId())) {
                throw new ServiceException("子项目不能再拥有子项目！");
            }
        }

        project.setProjectId(IdGen.uuid());
        project.setProjectCover("upload/project/bj.png");

        //初始化分组
        Relation relation = new Relation();
        relation.setRelationName("任务");
        relation.setProjectId(project.getProjectId());
        relation.setCreator(ShiroAuthenticationManager.getUserId());
        relationService.saveRelation(relation);

        //初始化项目文件夹
        fileService.initProjectFolder(project.getProjectId());

        //初始化项目功能菜单
        String[] funcs = new String[]{"任务", "分享", "文件", "日程", "群聊", "统计"};
        JSONArray array = new JSONArray();

        Arrays.stream(funcs).forEach(item -> {
            JSONObject object = new JSONObject();
            object.put("funcName", item);
            object.put("isOpen", true);
            array.add(object);
        });

        project.setFunc(array.toString());
        save(project);
        //初始化菜单
        String[] menus = new String[]{"待处理", "进行中", "已完成", "已审核", "已拒绝"};
        relationService.saveRelationBatch(Arrays.asList(menus), project.getProjectId(), relation.getRelationId());

        //往项目用户关联表插入数据
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(project.getProjectId());
        projectMember.setMemberId(ShiroAuthenticationManager.getUserId());
        projectMember.setCreateTime(System.currentTimeMillis());
        projectMember.setUpdateTime(System.currentTimeMillis());
        projectMember.setMemberLabel(1);
        projectMember.setDefaultGroup(relation.getRelationId());
        projectMember.setRoleKey("administrator");
        projectMemberService.save(projectMember);

        //企业中 项目拥有者的角色信息
        ProRole orgAdminRole = proRoleService.getOrgProjectRoleByKey(Constants.OWNER_KEY, project.getOrganizationId());

        ProRoleUser proRoleUser = new ProRoleUser();
        if (orgAdminRole!=null) {
            proRoleUser.setRoleId(orgAdminRole.getRoleId());
        }
        proRoleUser.setUId(ShiroAuthenticationManager.getUserId());
        proRoleUser.setProjectId(project.getProjectId());
        proRoleUser.setTCreateTime(LocalDateTime.now());
        proRoleUserService.save(proRoleUser);

        Integer userProjectCount = projectMemberService.getUserProjectCount();
        if (userProjectCount == 1) {
            projectMemberService.updateTargetProjectCurrent(project.getProjectId(), ShiroAuthenticationManager.getUserId());
        }
    }

    /**
     * 获取项目创建人的项目
     *
     * @return
     */
    @Override
    public List<Project> findProjectByMemberId(String memberId, int projectDel) {
        return projectMapper.findProjectByMemberId(memberId, projectDel);
    }

    /**
     * 查询出用户参与的所有项目信息
     *
     * @param uId 用户id
     * @return 项目实体集合
     */
    @Override
    public List<Project> listProjectByUid(String uId) {
        return projectMapper.listProjectByUid(uId);
    }

    /**
     * 根据用户id查询所有项目，包含我创建的，我参与的，星标项目，回收站的项目
     *
     * @param userId 用户id
     * @return
     */
    @Override
    public List<Project> findProjectByUserId(String userId) {
        return projectMapper.findProjectByUserId(userId);
    }

    @Override
    public List<Project> findOrgProject(String userId, String orgId) {
        return projectMapper.findOrgProject(userId, orgId);
    }


    /**
     * 查询出该项目的默认分组
     *
     * @param projectId 项目id
     * @return
     */
    @Override
    public String findDefaultGroup(String projectId) {
        return projectMapper.selectDefaultGroup(projectId);
    }

    @Override
    public List<GantChartVO> getGanttChart(String projectId, String groupId) {
        final List<GantChartVO> gants = new ArrayList<>();
        if (StringUtils.isEmpty(groupId)) {
            //获取到默认分组id
            groupId = projectMemberService.findDefaultGroup(projectId, ShiroAuthenticationManager.getUserId());
        }

        //获取分组下的所有列表信息
        List<Relation> menuList = relationService.findAllMenuInfoByGroupId(groupId).stream()
                .sorted(Comparator.comparing(Relation::getOrder))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(menuList)) {
            return gants;
        }

        //将分组下的所有列表任务存储到tasks中
        List<Task> tasks = new ArrayList<>();
        menuList.forEach(menu -> {
            List<Task> taskByMenuId = taskService.findTaskByMenuId(menu.getRelationId()).stream()
                    .sorted(Comparator.comparing(Task::getCreateTime).reversed())
                    .collect(Collectors.toList());
            tasks.addAll(taskByMenuId);
        });

        //构建任务为gants类型(更换id字符串为 数字类型)
        gants.addAll(taskService.buildFatherSon(tasks, menuList));

        Project projectGanttChart = projectMapper.getProjectGanttChart(projectId);
        GantChartVO pro = new GantChartVO();
        pro.setId(1);
        pro.setType("gantt.config.types.project");
        pro.setStart_date(projectGanttChart.getStartTime());
        pro.setEnd_date(projectGanttChart.getEndTime());
        pro.setText(projectGanttChart.getProjectName());
        pro.setPublicId(projectId);
        gants.add(0, pro);
        return gants;
    }

    /**
     * 模糊搜索项目
     *
     * @param projectName 项目名称
     * @param condition   搜索条件(created,join,star)
     * @return
     */
    @Override
    public List<Project> seachByName(String projectName, String condition, String orgId) {
        if (Constants.ALL.equals(condition)) {
            return projectMapper.selectAllByName(projectName, ShiroAuthenticationManager.getUserId(), orgId);
        }
        if (Constants.TRASH.equals(condition)) {
            return projectMapper.selectTrashByName(projectName, ShiroAuthenticationManager.getUserId(), orgId);
        }
        if (Constants.COMPLETE.equals(condition)) {
            return projectMapper.selectCompleteByName(projectName, ShiroAuthenticationManager.getUserId(), orgId);
        }
        if (Constants.STAR.equals(condition)) {
            return projectMapper.selectStarByName(projectName, ShiroAuthenticationManager.getUserId(), orgId);
        }
        if (Constants.CREATED.equals(condition)) {
            return projectMapper.selectCreatedByName(ShiroAuthenticationManager.getUserId(), projectName, orgId);
        }
        if (Constants.JOIN.equals(condition)) {
            return projectMapper.selectJoin(ShiroAuthenticationManager.getUserId(), projectName, orgId);
        }
        return new ArrayList<>();
    }

    /**
     * 获取项目下的所有任务id字符串 (逗号隔开)
     * 包括子任务id
     * 使用时需要自己分割
     *
     * @param projectId 项目id
     * @return id字符串
     */
    @Override
    public String findProjectAllTask(String projectId) {
        return projectMapper.selectProjectAllTask(projectId);
    }

    /**
     * 修改项目封面图片
     *
     * @param projectId 项目id
     * @param fileUrl   文件路径
     * @return int
     */
    @Override
    public Integer updatePictureById(String projectId, String fileUrl) {
        return projectMapper.updatePictureById(projectId, fileUrl);
    }

    @Override
    public Boolean checkIsExist(String projectId) {
        //构造出根据projectId查询某条信息的sql表达式
        LambdaQueryWrapper<Project> selectByProjectIdQw = new QueryWrapper<Project>().lambda()
                .eq(Project::getProjectId, projectId);

        Integer proCount = projectMapper.selectCount(selectByProjectIdQw);
        return proCount > 0;
    }

    @Override
    public Boolean notExist(String projectId) {
        return !this.checkIsExist(projectId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Project> getSubProject(String projectId) {
        //构造sql表达式
        LambdaQueryWrapper<Project> selectSubProjectByParentIdQw = new QueryWrapper<Project>().lambda()
                .eq(Project::getParentId, projectId)
                .orderByDesc(Project::getCreateTime);
        return this.list(selectSubProjectByParentIdQw);
        //把查询出来的项目信息 按照时间进项
    }

    @Override
    public List<ProjectTreeVO> getTreeData(String projectId) {
        String userId = ShiroAuthenticationManager.getUserId();

        List<Project> pros;

        //如果项目id不为空  则查询该项目的子项目,并且包装成目录树类型返回.
        if (StringUtils.isNotEmpty(projectId)) {
            pros = this.getSubProject(projectId);
        } else {
            pros = this.findProjectByUserId(userId);
            //过滤出根项目集合
            pros = pros.stream()
                    .filter(p -> Constants.ZERO.equals(p.getParentId()))
                    .collect(Collectors.toList());

        }
        return projectChangeToTreeData(pros);
    }

    /**
     * 改变项目信息集合到 项目树集合信息
     *
     * @param projects 原projects 集合信息
     * @return 项目树信息集合
     */
    private List<ProjectTreeVO> projectChangeToTreeData(List<Project> projects) {
        if (CollectionUtils.isEmpty(projects)) {
            return new ArrayList<>();
        }
        List<ProjectTreeVO> projectTreeVOS = new ArrayList<>();
        projects.forEach(pro -> {
            ProjectTreeVO tree = new ProjectTreeVO();
            tree.setTitle(pro.getProjectName());
            tree.setId(pro.getProjectId());
            tree.setExpand(false);
            tree.setSelected(false);
            tree.setChecked(false);
            projectTreeVOS.add(tree);
        });
        return projectTreeVOS;
    }

    @Override
    public Boolean checkIsSubProject(String projectId) {
        Project byId = this.getById(projectId);
        return !byId.getParentId().equals(Constants.ZERO);
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param:
     * @return:
     * @Description: 更新企业成员详细信息
     * @create: 18:48 2020/4/22
     */
    @Override
    public Integer updateMembersInfo(String memberId, String orgId, String userName, String entryTime, String job, String memberLabel, String address, String memberEmail, String phone, String birthday, String deptId) {
        OrganizationMember memberInfo = new OrganizationMember();
        memberInfo.setOrganizationId(orgId);
        if (StringUtils.isNotEmpty(userName)) {
            memberInfo.setUserName(userName);
        }
        if (StringUtils.isNotEmpty(birthday)) {
            memberInfo.setBirthday(birthday);
        }
        if (StringUtils.isNotEmpty(entryTime)) {
            memberInfo.setEntryTime(entryTime);
        }
        if (StringUtils.isNotEmpty(job)) {
            memberInfo.setJob(job);
        }
        if (StringUtils.isNotEmpty(memberLabel)) {
            //修改企业角色
            List<Role> roles = roleService.list(new QueryWrapper<Role>().eq("organization_id", orgId));
            Optional.ofNullable(roles).ifPresent(role -> {
                role.stream().forEach(r -> {
                    if (r.getRoleName().equals(memberLabel)) {
                        RoleUser roleUser = new RoleUser();
                        roleUser.setRoleId(r.getRoleId());
                        roleUser.setUId(memberId);
                        roleUser.setOrgId(orgId);
                        roleUserService.updateById(roleUser);
                        memberInfo.setMemberLabel(r.getRoleName());
                    }
                });
            });
        }
        if (StringUtils.isNotEmpty(address)) {
            memberInfo.setAddress(address);
        }
        if (StringUtils.isNotEmpty(memberEmail)) {
            memberInfo.setMemberEmail(memberEmail);
        }
        if (StringUtils.isNotEmpty(phone)) {
            memberInfo.setPhone(phone);
        }
        if (StringUtils.isNotEmpty(deptId)) {
            PartmentMember partmentMember = new PartmentMember();
            partmentMember.setUpdateTime(System.currentTimeMillis());
            partmentMember.setMemberId(memberId);
            partmentMember.setMemberType(memberInfo.getMemberLabel());
            partmentMember.setIsMaster(false);
            partmentMember.setPartmentId(deptId);
            partmentMember.setPartmentId(deptId);
            memberInfo.setPartmentId(deptId);

            partmentMemberService.save(partmentMember);
        }
        organizationMemberService.update(memberInfo, new QueryWrapper<OrganizationMember>().eq("member_id", memberId).eq("organization_id", orgId));
        return 1;
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param: orgId 企业id
     * @Param: memberId 用户id
     * @Param: dateSort 查询时间戳
     * @return:
     * @Description: 根据用户id和项目id获取任务列表
     * @create: 18:10 2020/4/26
     */
    @Override
    public List<Task> getTaskDynamicVOS(String orgId, String memberId, String dateSort) throws ParseException {
        //时间戳转换
        Date date = new Date(Long.valueOf(dateSort));
        Integer year = Integer.valueOf(new SimpleDateFormat("yyyy").format(date));
        Integer month = Integer.valueOf(new SimpleDateFormat("MM").format(date));
        //获取月份第一天和最后一天
        String startTime = DateUtils.getFisrtDayOfMonth(year, month);
        String endTime = DateUtils.getLastDayOfMonth(year, month);


        //根据指定的用户id和企业id查询项目信息
        List<Project> projects = projectMapper.selectList(new QueryWrapper<Project>().eq("organization_id", orgId).eq("member_id", memberId));
        List<Task> list = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(projects)) {
            for (Project project : projects) {
                List<Task> list1 = taskService.getTaskPanelByStartAndEndTime(project.getProjectId(), startTime, endTime);
                list.addAll(list1);
            }
        }
        return list;
    }

    /**
     * 根据企业id和用户id查询项目
     *
     * @param orgId
     * @param memberId
     * @return
     */
    @Override
    public List<Project> getProjectsByMemberIdAndOrgId(String orgId, String memberId) {
        return projectMapper.getProjectsByMemberIdAndOrgId(orgId, memberId);
    }

    /**
     * 查询企业下该成员所有的项目信息
     *
     * @param memberId
     * @param organizationId
     * @return
     */
    @Override
    public List<Project> getExperience(String memberId, String organizationId) {

        List<Project> projects = projectMapper.selectList(new QueryWrapper<Project>().eq("member_id", memberId).eq("organization_id", organizationId));
        List<String> projectSimpleInfoList = projectSimpleInfoService.isAdd(memberId, organizationId).stream().map(ProjectSimpleInfo::getProjectId).collect(Collectors.toList());

        //当项目表的id包含项目中间表的projectId时，设置是否添加
        if (!CollectionUtils.isEmpty(projectSimpleInfoList)) {
            Optional.ofNullable(projects).ifPresent(p -> projectSimpleInfoList.forEach(r -> {
                p.stream().filter(f -> f.getProjectId().equals(r)).forEach(c -> c.setIsAdd(1));
                p.stream().filter(f -> !f.getProjectId().equals(r)).forEach(c -> c.setIsAdd(0));
            }));
            //项目中间表没有值时表示从未添加过
        } else {
            Optional.ofNullable(projects).ifPresent(projectList -> projectList
                    .forEach(e -> e.setIsAdd(0)));
        }
        return projects;

    }

    /**
     * 项目角色判断 成员1 拥有者或管理员0
     *
     * @param projectId
     * @return
     */
    @Override
    public Integer judgmentRoles(String projectId) {
        Integer result = 1;
        ProRoleUser proRoleUser = proRoleUserService.findProRoleUser(projectId, ShiroAuthenticationManager.getUserId());
        if (!Constants.MEMBER_CN.equals(proRoleUser.getRoleName())) {
            result = 0;
        }
        return result;
    }

    @Override
    public String addMember(String projectId, String memberId) {
        Project project = projectService.getById(projectId);
        int orgExist = organizationMemberService.findOrgMemberIsExist(project.getOrganizationId(), ShiroAuthenticationManager.getUserId());
        if (orgExist == 0) {
            UserEntity byId = userMapper.selectById(memberId);
            organizationMemberService.saveOrganizationMember2(project.getOrganizationId(), byId);

        }
        projectMemberService.saveMember(projectId, memberId, project.getOrganizationId());

        return "1";
    }

    @Override
    public List<MenuVo> taskIndex(String projectId,String userId) {
        List<MenuVo>menuVos=Lists.newArrayList();
        List<String> keyList = proResourcesService.getMemberResourceKey(projectId, userId);
        redisUtil.remove("perms:" + userId);
        redisUtil.lset("perms:" + userId, keyList);
        redisUtil.set("userId:" + userId, projectId);
        List<Relation> list = relationService.list(new QueryWrapper<Relation>().eq("project_id", projectId).eq("parent_id", 0));
        List<String> relationIds=Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {
            relationIds = list.stream().filter(f->f.getParentId().equals(Constants.ZERO)).map(Relation::getRelationId).collect(Collectors.toList());
        }

        for (String relationId : relationIds) {
            Relation relation = new Relation();
            relation.setParentId(relationId);
            relation.setLable(1);
            Relation byId = relationService.getById(relationId);
            List<Relation> taskMenu = relationService.findRelationAllList(relation);
            MenuVo menuVo =  MenuVo.builder().name(byId.getRelationName()).taskMenu(taskMenu).build();
            menuVos.add(menuVo);
        }
        return menuVos;
    }

    @Override
    public void addProjectMember(String projectId, String memberId, String orgId) {
        //判断用户是否在企业
        if (organizationMemberService.findOrgMemberIsExist(orgId, memberId) != 0) {
            projectMemberService.saveMember(projectId, memberId, orgId);
        } else {
            UserEntity byId = userMapper.findById(memberId);
            OrganizationMember organizationMember = OrganizationMember.builder().job(byId.getJob())
                    .createTime(System.currentTimeMillis()).image(byId.getImage()).memberEmail(byId.getEmail())
                    .memberId(memberId).organizationId(orgId).phone(byId.getAccountName()).updateTime(System.currentTimeMillis())
                    .userName(byId.getUserName()).build();
            organizationMemberService.save(organizationMember);
            Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("role_name", "外部成员"));
            RoleUser roleUser = new RoleUser();
            roleUser.setUId(memberId);
            roleUser.setOrgId(orgId);
            roleUser.setRoleId(role.getRoleId());
            roleUser.setTCreateTime(LocalDateTime.now());
            roleUserMapper.insert(roleUser);
            projectMemberService.saveMember(projectId, memberId, orgId);
        }
    }
}