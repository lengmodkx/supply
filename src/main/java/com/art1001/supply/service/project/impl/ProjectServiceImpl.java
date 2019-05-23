package com.art1001.supply.service.project.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.project.GantChartVO;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.project.ProjectMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
	private RoleService roleService;

	@Resource
	private ProjectMemberService projectMemberService;

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
		return projectMapper.findProjectByProjectId(projectId);
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
	 * 修改project数据
	 * 
	 * @param project
	 */
	@Override
	public void updateProject(Project project){
		projectMapper.updateProject(project);
	}

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
		String[] menus  = new String[]{"待处理","进行中","已完成"};
		relationService.saveRelationBatch(Arrays.asList(menus),project.getProjectId(),relation.getRelationId());

		//往项目用户关联表插入数据
		Role roleEntity = roleService.getOne(new QueryWrapper<Role>().eq("role_name","拥有者"));
		ProjectMember projectMember = new ProjectMember();
		projectMember.setProjectId(project.getProjectId());
		projectMember.setMemberId(ShiroAuthenticationManager.getUserId());
		projectMember.setCreateTime(System.currentTimeMillis());
		projectMember.setUpdateTime(System.currentTimeMillis());
		projectMember.setMemberLabel(1);
		projectMember.setDefaultGroup(relation.getRelationId());
		projectMember.setRoleId(roleEntity.getRoleId());
		projectMemberService.save(projectMember);
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

	/**
	 * 获取项目的甘特图数据
	 * @param projectId 项目id
	 * @return
	 */
	@Override
	public List<GantChartVO> getGanttChart(String projectId) {
		if(Stringer.isNullOrEmpty(projectId)){
			throw new ServiceException("项目id不能为空!");
		}
		if(projectMapper.selectById(projectId) == null){
			throw new ServiceException("该项目不存在!");
		}
		List<GantChartVO> gants = new ArrayList<GantChartVO>();
		//获取到该项目的所有任务id字符串(逗号隔开)
		String taskIds = projectMapper.selectProjectAllTask(projectId);
		if(!Stringer.isNullOrEmpty(taskIds)){
			List<String> idList = Arrays.asList(taskIds.split(","));
			List<Task> tasks = taskService.listById(idList).stream().sorted(Comparator.comparing(Task::getLevel)).collect(Collectors.toList());
			//构建任务和子任务的parent 和 id  (更换id字符串为 数字类型)
			gants = taskService.buildFatherSon(tasks);
		}
		//查询出项目的部分信息并且映射进 GantChartVO
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
}