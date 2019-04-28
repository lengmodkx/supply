package com.art1001.supply.service.relation.impl;

import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.fabulous.Fabulous;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.relation.GroupVO;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskMenuVO;
import com.art1001.supply.entity.template.TemplateData;
import com.art1001.supply.mapper.relation.RelationMapper;
import com.art1001.supply.mapper.task.TaskMapper;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.fabulous.FabulousService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * relationServiceImpl
 */
@Service
public class RelationServiceImpl extends ServiceImpl<RelationMapper,Relation> implements RelationService {

	/** relationMapper接口*/
	@Resource
	private RelationMapper relationMapper;

	/**taskService 逻辑层接口  */
	@Resource
	private TaskService taskService;

	/**taskMapper 接口 */
	@Resource
	private TaskMapper taskMapper;

	@Resource
	private LogService logService;

	@Resource
	private BindingService bindingService;

	@Resource
	private FabulousService fabulousService;

	@Resource
	private PublicCollectService publicCollectService;

	@Resource
	private TagRelationService tagRelationService;

	@Resource
	private UserNewsService userNewsService;

	@Resource
	private ProjectMemberService projectMemberService;

	/**
	 * 删除分组
	 * 
	 * @param relationId 分组id
	 */
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void deleteGroup(String relationId){

		//分组下所有菜单的id
		List<String> menuIds = relationMapper.findMenuIdByGroup(relationId);

		//分组下菜单的所有任务id
		List<String> taskIds = relationMapper.findTaskIdByMenus(menuIds);
		if(taskIds != null && taskIds.size() > 0){
			//删除该任务的绑定信息
			bindingService.deleteManyByPublicId(taskIds);
			//删除任务的日志 和 评论信息
			logService.deleteManyByPublicId(taskIds);
			//删除任务得赞信息
			fabulousService.deleteManyFabulousByInfoId(taskIds);
			//删除任务收藏信息
			publicCollectService.deleteManyCollectByItemId(taskIds);
			//删除任务的标签关联信息
			tagRelationService.deleteManyItemTagRelation(taskIds);
			//删除任务的消息通知信息
			userNewsService.deleteManyNewsByPublicId(taskIds);
			//删除任务
			taskService.deleteManyTask(taskIds);
		}

		relationMapper.deleteBatchIds(menuIds);
		relationMapper.deleteById(relationId);
	}

	/**
	 * 修改relation数据
	 * 
	 * @param relation
	 */
	@Override
	public void updateRelation(Relation relation){
		relationMapper.updateRelation(relation);
	}
	/**
	 * 保存relation数据
	 * 
	 * @param relation
	 */
	@Override
	public void saveRelation(Relation relation){
        relation.setRelationId(IdGen.uuid());
		relation.setCreator(ShiroAuthenticationManager.getUserId());
        relation.setOrder(relationMapper.findMaxOrder(relation.getProjectId(),0)+1);
		ProjectMember projectMember = new ProjectMember();
		projectMember.setDefaultGroup(relation.getRelationId());
		projectMemberService.update(projectMember,new QueryWrapper<ProjectMember>().eq("member_id",ShiroAuthenticationManager.getUserId()).eq("project_id",relation.getProjectId()));
		save(relation);
	}



	@Override
	public void saveRelationBatch(List<String> relationList, String projectId, String parentId) {
			relationMapper.saveRelationBatch(relationList,projectId,parentId);
	}

	@Override
	public void saveRelationBatch2(List<TemplateData> templateDataList, String projectId, String parentId) {
		relationMapper.saveRelationBatch2(templateDataList,projectId,parentId);
	}

	/**
	 * 获取所有relation数据
	 * 
	 * @return
	 */
	@Override
	public List<Relation> findRelationAllList(Relation relation){
		List<Relation> relationAllList = relationMapper.findRelationAllList(relation);
		relationAllList.forEach(r -> {
			r.getTaskList().forEach(t -> {
				t.setCompleteCount((int)t.getTaskList().stream().filter(Task::getTaskStatus).count());
			});
			Iterator<Task> iterator = r.getTaskList().iterator();
			while(iterator.hasNext()){
				Task task = iterator.next();
				if(task.getPrivacyPattern() == 0){
					if(!(Objects.equals(ShiroAuthenticationManager.getUserId(),task.getExecutor()))){
						if(!(task.getTaskUIds() != null && Arrays.asList(task.getTaskUIds().split(",")).contains(ShiroAuthenticationManager.getUserId()))){
							iterator.remove();
						}
					}
				}
			}

		});
		return relationAllList;
	}

	/**
	 * 根据分组删除分组下的所有菜单
	 * @param relationId 分组id
	 */
	@Override
	public void deletenMenuByRelationId(String relationId) {
		relationMapper.deletenMenuByRelationId(relationId);
	}

	/**
	 * 在分组下创建菜单
	 * @param parentId 分组的id
	 * @param relation 菜单信息
	 */
	@Override
	public void addMenu(String parentId, Relation relation) {
		relation.setOrder(relationMapper.findMaxOrder(parentId,0)+1);
		relation.setParentId(parentId);
		relation.setLable(1);
		relation.setRelationDel(0);
		relation.setCreateTime(System.currentTimeMillis());
		relation.setUpdateTime(System.currentTimeMillis());
		relationMapper.saveRelation(relation);
	}

	/**
	 * 编辑菜单信息
	 * @param relation 菜单实体信息
	 * @return
	 */
	@Override
	public int editMenu(Relation relation) {
		return relationMapper.updateRelation(relation);
	}

	/**
	 * 根据菜单id 排序任务
	 * @param relationId 菜单id
	 * @return
	 */
	@Override
	public Relation taskSort(String relationId) {
		return relationMapper.taskSort(relationId);
	}

	/**
	 * 排序分组内的菜单
	 * @param relationId 分组id
	 * @return
	 */
	@Override
	public List<Relation> menuSort(String relationId) {
		return relationMapper.menuSort(relationId);
	}

	/**
	 * 将分组(移至回收站 或者 恢复)
	 * @param relationId 分组的id
	 * @param relationDel 当前分组的状态
	 */
	@Override
	public void moveRecycleBin(String relationId,String relationDel) {
		relationMapper.moveRecycleBin(relationId,relationDel,System.currentTimeMillis());
	}

	/**
	 * 执行此菜单所有的执行者
	 * 步骤
	 * 1.获取到菜单里的所有任务
	 * 2.给每个任务设置新的执行者
	 * 3.给任务成员关系表更新新的执行者信息
	 * 4.判断  如果新的执行者  已经在任务成员关系表以参与者的身份出现 则不给新的执行者在表中添加 参与者的数据
	 * @param relationId 列表id
	 * @param userId 新的执行者的id
	 */
	@Override
	public void setMenuAllTaskExecutor(String relationId,String userId,String uName) {
		//获取菜单下所有的任务信息
		List<Task> tasks = taskService.simpleTaskMenu(relationId);
		if(tasks != null && tasks.size() > 0){
			for (Task task: tasks) {
				//给任务设置新的执行者
				task.setExecutor(userId);
				//设置更新时间
				task.setUpdateTime(System.currentTimeMillis());
				//更新任务信息
				taskMapper.updateTask(task);

			}
		}
	}

	/**
	 * 设置此菜单下的所有的任务截止时间
	 * @param relationId 菜单id
	 * @param endTime 截止时间
	 */
	@Override
	public void setMenuAllTaskEndTime(String relationId, Long endTime) {
		List<Task> tasks = taskService.simpleTaskMenu(relationId);
		if (tasks != null && tasks.size() > 0) {
			for (Task task : tasks) {
				Task newTask = new Task();
				//设置新的截止时间
				newTask.setEndTime(endTime);
				newTask.setTaskId(task.getTaskId());
				newTask.setUpdateTime(System.currentTimeMillis());
				//保存到数据库
				taskService.updateById(newTask);
			}
		}
	}

	/**
	 * 移动菜单下的所有任务
	 * @param oldTaskMenuVO 旧的任务位置信息
	 * @param newTaskMenuVO 新的任务位置信息
	 */
	@Override
	public void moveMenuAllTask(TaskMenuVO oldTaskMenuVO, TaskMenuVO newTaskMenuVO) {
		List<Task> tasks = taskService.simpleTaskMenu(oldTaskMenuVO.getTaskMenuId());
		if(tasks != null && tasks.size() > 0){
			for (Task task : tasks) {
				Task newTask = new Task();
				newTask.setTaskId(task.getTaskId());
				//taskService.mobileTask(newTask,oldTaskMenuVO,newTaskMenuVO);
			}
		}
	}

	/**
	 * 复制了列表下所有任务
	 * @param oldTaskMenuVO 复制前的任务位置信息
	 * @param newTaskMenuVO 复制到的任务位置信息
	 */
	@Override
	public void copyMenuAllTask(TaskMenuVO oldTaskMenuVO,TaskMenuVO newTaskMenuVO) {
		List<Task> tasks = taskService.simpleTaskMenu(oldTaskMenuVO.getTaskMenuId());
		if(tasks != null && tasks.size() > 0){
			for (Task task : tasks) {
				//taskService.copyTask(task.getTaskId(),oldTaskMenuVO.getProjectId() ,newTaskMenuVO.getTaskMenuId());
			}
		}
	}

	/**
	 * 菜单下的所有任务移动到回收站
	 * @param relationId 菜单id
	 */
	@Override
	public void menuAllTaskToRecycleBin(String relationId) {
		List<Task> tasks = taskService.simpleTaskMenu(relationId);
		if(tasks != null && tasks.size() > 0){
			for (Task task : tasks) {
				Task newTask = new Task();
				newTask.setTaskId(task.getTaskId());
				newTask.setTaskDel(task.getTaskDel());
				taskService.moveToRecycleBin(newTask.getTaskId());
			}
		}
	}

	/**
	 * 根据菜单id 查询出该菜单所在的 分组信息以及项目信息
	 * @param relationId 菜单id
	 * @return
	 */
	@Override
	public TaskMenuVO findProjectAndGroupInfoByMenuId(String relationId) {
		return relationMapper.findProjectAndGroupInfoByMenuId(relationId);
	}

	/**
	 * 查询菜单下的任务的最大序号
	 * @param taskMenuId 菜单id
	 * @return
	 */
	@Override
	public int findMenuTaskMaxOrder(String taskMenuId) {
		return relationMapper.findMenuTaskMaxOrder(taskMenuId);
	}

	/**
	 * 查询出项目下的所有分组
	 * @param projectId 项目id
	 * @return
	 */
	@Override
	public List<Relation> findAllGroupInfoByProjectId(String projectId) {
		return relationMapper.findAllGroupInfoByProjectId(projectId);
	}

	/**
	 * 查询出某个分组下的所有菜单信息
	 * @param groupId 分组id
	 * @return
	 */
	@Override
	public List<Relation> findAllMenuInfoByGroupId(String groupId) {
		return relationMapper.findAllMenuInfoByGroupId(groupId);
	}

	/**
	 * 实现方法 根据分组id 查询出该分组下的所有任务信息
	 * @param id 分组id
	 * @return 任务实体信息集合
	 */
	@Override
	public List<Relation> findGroupAllTask(String id) {
		List<Relation> allMenuInfoByGroupId = findAllMenuInfoByGroupId(id);
		List<Relation> totalTask = new ArrayList<Relation>();
		if(!allMenuInfoByGroupId.isEmpty()){
			for (Relation relation : allMenuInfoByGroupId){
				Relation relations = getRelationAndAllTaskInfo(relation.getRelationId());
				//按照任务的创建时间排序任务
				Collections.sort(relations.getTaskList(), new Comparator<Task>() {
					@Override
					public int compare(Task o1, Task o2) {
						Date date1 = new Date(o1.getCreateTime());
						Date date2 = new Date(o2.getCreateTime());
						if(date1.after(date2)){
							return 1;
						}
						return -1;
					}
				});
				totalTask.add(relations);
			}
		}
		return totalTask;
	}

	@Override
	public Relation getRelationAndAllTaskInfo(String relationId) {
		return relationMapper.getRelationAndAllTaskInfo(relationId);
	}

	/**
	 * 根据项目的id 查询出该项目下的所有菜单信息 (不保括分组信息)
	 * @param projectId 项目id
	 * @return
	 */
	@Override
	public List<Relation> findMenusByProjectId(String projectId) {
		return relationMapper.findMenusByProjectId(projectId);
	}

	/**
	 * 加载所有分组信息
	 * @param projectId 项目Id
	 * @return
	 */
	@Override
	public List<Relation> loadGroupInfo(String projectId) {
		return relationMapper.loadGroupInfo(projectId);
	}

	/**
	 * 查询出某个项目下 回收站中的所有任务分组
	 * @param projectId 项目id
	 * @return
	 */
	@Override
	public List<RecycleBinVO> findRecycleBin(String projectId) {
		return relationMapper.findRecycleBin(projectId);
	}

	/**
	 * 查询一个菜单的名称 和  该菜单所属项目的名称
	 * @param menuId 菜单id
	 */
	@Override
	public TaskMenuVO findRelationNameAndProjectName(String menuId) {
		return relationMapper.findRelationNameAndProjectName(menuId);
	}

	/**
	 * 添加任务菜单
	 * @param relation 菜单信息 (名称,所在项目id,所在分组id)
	 */
	@Override
	public void saveMenu(Relation relation) {
		relation.setRelationId(IdGen.uuid());
		relation.setCreator(ShiroAuthenticationManager.getUserId());
		relation.setLable(1);
		//设置菜单排序编号
		relation.setOrder(relationMapper.findMaxOrder(relation.getParentId(),1) + 1);
		relation.setCreateTime(System.currentTimeMillis());
		relation.setUpdateTime(System.currentTimeMillis());
		relationMapper.saveRelation(relation);
	}

	/**
	 * 添加任务分组
	 * @param relation 分组信息(名称,所在项目)
	 */
	@Override
	public void saveGroup(Relation relation) {
		relation.setRelationId(IdGen.uuid());
		relation.setCreator(ShiroAuthenticationManager.getUserId());
		relation.setCreateTime(System.currentTimeMillis());
		relation.setUpdateTime(System.currentTimeMillis());
		relation.setOrder(relationMapper.findMaxOrder(relation.getProjectId(),0) + 1);
		relationMapper.saveRelation(relation);
	}

	/**
	 * 排序菜单
	 * @param menuIds 菜单id
	 */
	@Override
	public void orderMenu(String[] menuIds) {
		int index = 1;
		for (String id : menuIds) {
			Relation relation = new Relation();
			relation.setRelationId(id);
			relation.setOrder(index);
			relationMapper.updateById(relation);
			index++;
		}
	}

	/**
	 * 获取分组下所有菜单以及任务信息数据
	 * @param groupId 分组id
	 * @return 菜单和任务数据集合
	 */
	@Override
	public List<Relation> initMainPage(String groupId) {
		return relationMapper.selectMenuTask(groupId);

	}

	/**
	 * 获取一个分组下的菜单信息以及任务信息
	 * @param groupId 分组id
	 * @return 菜单和任务的集合
	 */
	@Override
	public List<Relation> bindMenuInfo(String groupId) {
		return relationMapper.bindMenuInfo(groupId);
	}


	/**
	 * 获取项目下的全部分组信息
	 * 分组信息中包括了 任务总数  完成数 优先级信息等
	 * @param  projectId 项目id
	 * @return 分组详情信息
	 */
	@Override
	public List<GroupVO> getGroupsInfo(String projectId){
		List<GroupVO> groupsInfo = relationMapper.getGroupsInfo(projectId);
		groupsInfo.forEach(g -> {
			g.setCompleteCount((int)g.getTasks().stream().filter(Task::getTaskStatus).count());
			g.setOrdinary(g.getTasks().stream().filter(t -> "普通".equals(t.getPriority())).count() / (long)g.getTasks().size());
			g.setUrgent(g.getTasks().stream().filter(t -> "紧急".equals(t.getPriority())).count() / (long)g.getTasks().size());
			g.setVeryUrgent(g.getTasks().stream().filter(t -> "非常紧急".equals(t.getPriority())).count() / (long)g.getTasks().size());
		});
		return groupsInfo;
	}
}


