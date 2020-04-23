package com.art1001.supply.service.project.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.organization.OrganizationMemberInfo;
import com.art1001.supply.entity.project.*;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.role.ProRoleUser;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.project.ProjectMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.organization.OrganizationMemberInfoService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.MyBeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * projectServiceImpl
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper,Project> implements ProjectService {

	/** projectMapper接口*/
	@Resource
	private ProjectMapper projectMapper;

	@Resource
	private TaskService taskService;

	@Resource
	private ShareService shareService;

	@Resource
	private ScheduleService scheduleService;

	@Resource
	private TagService tagService;


	@Resource
	private FileService fileService;

	@Resource
	private RelationService relationService;

	@Resource
	private ProRoleService proRoleService;

	@Resource
	private ProjectMemberService projectMemberService;

	@Resource
	private ProRoleUserService proRoleUserService;

	@Resource
	private OrganizationMemberInfoService organizationMemberInfoService;

	/**
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Project> findProjectPagerList(Pager pager){
		return projectMapper.findProjectPagerList(pager);
	}

	/**
	 * 通过projectId获取单条project数据
	 * 
	 * @param projectId
	 * @return
	 */
	@Override 
	public Project findProjectByProjectId(String projectId){
		return Optional.ofNullable(projectMapper.findProjectByProjectId(projectId))
				.orElseThrow(() -> new ServiceException("项目不存在"));
	}

	/**
	 * 通过projectId删除project数据
	 * 
	 * @param projectId
	 */
	@Override
	public void deleteProjectByProjectId(String projectId){
		projectMapper.deleteProjectByProjectId(projectId);
	}

	/**
	 * 修改project
	 */
	@Override
	public void updateProject(Project project) {
		projectMapper.updateById(project);
	}

	/**
	 * 修改project数据
	 * 
	 * @param project
	 */
/*
	@Override
	public void updateProject(Project project){
		projectMapper.updateProject(project);
	}
*/

	/**
	 * 获取所有project数据
	 *
	 * @return
	 */
	@Override
	public List<Project> findProjectAllList(){
		return projectMapper.findProjectAllList();
	}

	/**
	 * 保存project数据
	 * @param project 项目信息
	 */
	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	@Override
	public void saveProject(Project project){

		if(!StringUtils.isEmpty(project.getParentId())){
			if(this.checkIsSubProject(project.getParentId())){
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
		String[] funcs = new String[]{"任务","分享","文件","日程","群聊","统计"};
		JSONArray array = new JSONArray();

		Arrays.stream(funcs).forEach(item->{
			JSONObject object = new JSONObject();
			object.put("funcName",item);
			object.put("isOpen",true);
			array.add(object);
		});

		project.setFunc(array.toString());
		save(project);
		//初始化菜单
		String[] menus  = new String[]{"待处理","进行中","已完成","已审核","已拒绝"};
		relationService.saveRelationBatch(Arrays.asList(menus),project.getProjectId(),relation.getRelationId());

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
		proRoleUser.setRoleId(orgAdminRole.getRoleId());
		proRoleUser.setUId(ShiroAuthenticationManager.getUserId());
		proRoleUser.setProjectId(project.getProjectId());
		proRoleUser.setTCreateTime(LocalDateTime.now());
		proRoleUserService.save(proRoleUser);

		Integer userProjectCount = projectMemberService.getUserProjectCount();
		if(userProjectCount == 1){
			projectMemberService.updateTargetProjectCurrent(project.getProjectId(),ShiroAuthenticationManager.getUserId());
		}
	}

	/**
	 * 获取项目创建人的项目
	 *
	 * @return
	 */
	@Override
	public List<Project> findProjectByMemberId(String memberId,int projectDel) {
		return projectMapper.findProjectByMemberId(memberId,projectDel);
	}

	/**
	 * 查询出当前用户所执行的任务的 任务信息 和 项目信息
	 * @param id 当前用户id
	 * @return
	 */
	@Override
	public List<Project> findProjectAndTaskByExecutorId(String id) {
		return projectMapper.findProjectAndTaskByExecutorId(id);
	}

    /**
     * 查询出当前用户所参与的任务的 任务信息 和 项目信息
     * @param id 当前用户id
     * @return
     */
    @Override
    public List<Project> findProjectAndTaskByUserId(String id) {
        return projectMapper.findProjectAndTaskByUserId(id);
    }

    /**
     * 查询出当前用户所创建的任务的 任务信息 和 项目信息
     * @param id 当前用户id
     * @return
     */
    @Override
    public List<Project> findProjectAndTaskByCreateMember(String id) {
        return projectMapper.findProjectAndTaskByCreateMember(id);
    }

	/**
	 * 查询出用户参与的所有项目信息
	 * @param uId 用户id
	 * @return 项目实体集合
	 */
	@Override
	public List<Project> listProjectByUid(String uId) {
		return projectMapper.listProjectByUid(uId);
	}

	/**
	 * 数据:查询出当前用户收藏的所有项目
	 * 功能:添加关联页面展示 星标项目
	 * @param uId 用户id
	 * @return 用户收藏的所有项目信息
	 */
	@Override
	public List<Project> listProjectByUserCollect(String uId) {
		return projectMapper.listProjectByUserCollect(uId);
	}

	/**
	 *  根据用户id查询所有项目，包含我创建的，我参与的，星标项目，回收站的项目
	 * @param userId 用户id
	 * @return
	 */
	@Override
	public List<Project> findProjectByUserId(String userId) {
		return projectMapper.findProjectByUserId(userId);
	}

	/**
	 * 用于展示回收站的数据
	 * @param projectId 项目id
	 * @param type 选项卡的类型
	 */
	@Override
	public List<RecycleBinVO> recycleBinInfo(String projectId, String type) {

		List<RecycleBinVO> recycleBin = new ArrayList<RecycleBinVO>();
		if(BindingConstants.BINDING_TASK_NAME.equals(type)){
			recycleBin = taskService.findRecycleBin(projectId);
		}
		if(BindingConstants.BINDING_FILE_NAME.equals(type)){
			recycleBin = fileService.findRecycleBin(projectId,type);
		}
		if(BindingConstants.BINDING_SHARE_NAME.equals(type)){
			recycleBin = shareService.findRecycleBin(projectId);
		}
		if(BindingConstants.BINDING_SCHEDULE_NAME.equals(type)){
			recycleBin = scheduleService.findRecycleBin(projectId);
		}
		if(BindingConstants.BINDING_TAG_NAME.equals(type)){
			recycleBin = tagService.findRecycleBin(projectId);
		}
		if(BindingConstants.TASK_GROUP.equals(type)){
			recycleBin = relationService.findRecycleBin(projectId);
		}
		if(BindingConstants.FOLDER.equals(type)){
			recycleBin = fileService.findRecycleBin(projectId,type);
		}
		return recycleBin;
	}

	@Override
	public List<Project> findOrgProject(String userId,String orgId) {
		return projectMapper.findOrgProject(userId,orgId);
	}


	/**
	 * 查询出该项目的默认分组
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
		if(StringUtils.isEmpty(groupId)){
			//获取到默认分组id
			groupId = projectMemberService.findDefaultGroup(projectId, ShiroAuthenticationManager.getUserId());
		}

		//获取分组下的所有列表信息
        List<Relation> menuList = relationService.findAllMenuInfoByGroupId(groupId).stream()
                .sorted(Comparator.comparing(Relation::getOrder))
                .collect(Collectors.toList());
		if(CollectionUtils.isEmpty(menuList)){
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
        gants.addAll(taskService.buildFatherSon(tasks,menuList));

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
	 * @param projectName 项目名称
	 * @param condition 搜索条件(created,join,star)
	 * @return
	 */
	@Override
	public List<Project> seachByName(String projectName, String condition) {
		if(Constants.STAR.equals(condition)){
			return projectMapper.selectStarByName(projectName,ShiroAuthenticationManager.getUserId());
		}
		if(Constants.CREATED.equals(condition)){
			return projectMapper.selectCreatedByName(ShiroAuthenticationManager.getUserId(),projectName);
		}
		if(Constants.JOIN.equals(condition)){
			return projectMapper.selectJoin(ShiroAuthenticationManager.getUserId(),projectName);
		}
		return new ArrayList<>();
	}

	/**
	 * 获取项目下的所有任务id字符串 (逗号隔开)
	 * 包括子任务id
	 * 使用时需要自己分割
	 * @param projectId 项目id
	 * @return id字符串
	 */
	@Override
	public String findProjectAllTask(String projectId){
		return projectMapper.selectProjectAllTask(projectId);
	}

	/**
	 * 修改项目封面图片
	 * @param projectId 项目id
	 * @param fileUrl  文件路径
	 * @return int
	 */
	@Override
	public Integer updatePictureById(String projectId, String fileUrl) {
		return projectMapper.updatePictureById(projectId,fileUrl);
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
		if(StringUtils.isNotEmpty(projectId)){
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
	 * @param projects 原projects 集合信息
	 * @return 项目树信息集合
	 */
	private List<ProjectTreeVO> projectChangeToTreeData(List<Project> projects){
		if(CollectionUtils.isEmpty(projects)){
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

	@Override
	public void updateAllProject(String userId, String id) {
		projectMapper.updateAllProject(userId,id);
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
	public Integer updateMembersInfo(OrganizationMemberInfo organinfo) {
		//	 * String memberId,String userName, Date entryTime, String job, Integer memberLabel, String address, String email, String phone, Date birthday, String deptName, String deptId
		OrganizationMemberInfo memberInfo =new  OrganizationMemberInfo();
		if (organinfo.getUserName()!=null) {
			memberInfo.setUserName(organinfo.getUserName());
		}
		if (organinfo.getEntryTime()!=null) {
			memberInfo.setEntryTime(organinfo.getEntryTime());
		}
		if (organinfo.getJob()!=null) {
			memberInfo.setJob(organinfo.getJob());
		}
		if (organinfo.getMemberLabel()!=null) {
			memberInfo.setMemberLabel(organinfo.getMemberLabel());
		}
		if (organinfo.getAddress()!=null) {
			memberInfo.setAddress(organinfo.getAddress());
		}
		if (organinfo.getEmail()!=null) {
			memberInfo.setEmail(organinfo.getEmail());
		}
		if (organinfo.getPhone()!=null) {
			memberInfo.setPhone(organinfo.getPhone());
		}
		if (organinfo.getBirthday()!=null) {
			memberInfo.setBirthday(organinfo.getBirthday());
		}
		if (organinfo.getDeptName()!=null) {
			memberInfo.setDeptName(organinfo.getDeptName());
		}
		if (organinfo.getDeptId()!=null) {
			memberInfo.setDeptId(organinfo.getDeptId());
		}
		memberInfo.setUpdateTime(String.valueOf(System.currentTimeMillis()));
		return organizationMemberInfoService.update(memberInfo,new QueryWrapper<OrganizationMemberInfo>().eq("member_id",organinfo.getMemberId()).eq("project_id",organinfo.getProjectId()))?1:0;

	}


}