package com.art1001.supply.service.relation.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.relation.GroupVO;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskMenuVO;
import com.art1001.supply.entity.template.TemplateData;
import com.art1001.supply.exception.ServiceException;
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
import com.art1001.supply.util.ValidatedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

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
	 * @return
	 */
	@Override
	public List<Relation> findRelationAllList(Relation relation){
		List<Relation> relationAllList = relationMapper.findRelationAllList(relation);
		relationAllList.forEach(r -> {
			r.getTaskList().forEach(t -> t.setCompleteCount((int)t.getTaskList().stream().filter(Task::getTaskStatus).count()));
			r.getTaskList().forEach(t -> {
				t.setIsExistSub(t.getTaskList().size() > 0);
				t.setChildCount(t.getTaskList().size());
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
		return relationMapper.updateById(relation);
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
	 * 执行此菜单所有的执行者 (version 1.0)
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
     * 设置此菜单下的所有的任务的截止时间
     * @param relationId 菜单id
     * @param endTime 截止时间
     * @return 结果
     */
    @Override
    public int setAllTaskEndTime(String relationId, Long endTime) {
        List<Task> tasks = taskService.list(new QueryWrapper<Task>().lambda().eq(Task::getTaskMenuId, relationId).select(Task::getTaskId));
        if(CollectionUtils.isEmpty(tasks)){
            throw new ServiceException("此列表任务数为0,无法设置截止时间");
        }
        //循环设置任务的截止时间
        tasks.forEach(task -> {
            task.setEndTime(endTime);
            task.setUpdateTime(System.currentTimeMillis());
        });
        return taskService.updateBatchById(tasks) ? 1:0;

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
		if(relation.getOrder() == null){
			relation.setOrder(relationMapper.findMaxOrder(relation.getParentId(),1) + 1);
		} else {
			relation.setOrder(relation.getOrder() + 1);
			//改分组下的每个列表都要为信列表让出位置
			List<Relation> relations = relationMapper.selectList(new QueryWrapper<Relation>().lambda().select(Relation::getOrder, Relation::getRelationId).eq(Relation::getParentId, relation.getParentId()).ge(Relation::getOrder, relation.getOrder()));
			if(!CollectionUtils.isEmpty(relations)){
				relations.forEach(r ->{
					if(r.getOrder() >= relation.getOrder()){
						r.setOrder(r.getOrder()+1);
					}
				});
                this.updateBatchById(relations);
            }
		}
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
		//初始化菜单
		String[] menus  = new String[]{"待处理","进行中","已完成","已审核","已拒绝"};
		relationMapper.saveRelationBatch(Arrays.asList(menus),relation.getProjectId(),relation.getRelationId());
		projectMemberService.updateDefaultGroup(relation.getProjectId(),ShiroAuthenticationManager.getUserId(),relation.getRelationId());
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
		DecimalFormat df   = new DecimalFormat("######0.00");
		List<GroupVO> groupsInfo = relationMapper.getGroupsInfo(projectId);
		groupsInfo.forEach(g -> {
			g.setTaskTotal(g.getTasks().size());
			if(!CollectionUtils.isEmpty(g.getTasks())){
				g.setCompleteCount((int)g.getTasks().stream().filter(Task::getTaskStatus).count());
				g.setNotCompleteCount(g.getTasks().size() - g.getCompleteCount());
				g.setCompletePercentage(df.format((double)g.getCompleteCount() / (double)g.getTasks().size() * 100));
				g.setNoCompletePercentage(df.format((double)(g.getTasks().size() - g.getCompleteCount()) / (double)g.getTasks().size() * 100));


				int beOverdue = (int)g.getTasks().stream().filter(t -> t.getEndTime() != null && t.getEndTime() < System.currentTimeMillis()).count();
				g.setBeOverdue(beOverdue);
				g.setBeOverduePercentage(df.format((double)beOverdue / (double)g.getTasks().size() * 100));
			}
			//任务信息置空,让gc进行回收
			g.setTasks(null);
		});
		return groupsInfo;
	}

    /**
	 * (version 2.0)
     * 移动列表下的所有任务
     * @param menuId    列表id
     * @param projectId 项目id
     * @param groupId   分组id
     * @param toMenuId  移动到的列表id
     * @return 结果
     */
    @Override
    public boolean moveAllTask(String menuId, String projectId, String groupId, String toMenuId) {
        List<Task> tasks = taskService.getMoveData(menuId);
        if(CollectionUtils.isEmpty(tasks)){
            throw new ServiceException("此列表下的任务为0,无法移动!");
        }
        List<String> taskIds = new ArrayList<>();
		int menuTaskMaxOrder = this.findMenuTaskMaxOrder(menuId);
		tasks.forEach(task -> {
			//生成父任务更新条件
			LambdaUpdateWrapper<Task> taskSet = new UpdateWrapper<Task>().lambda()
					.set(Task::getUpdateTime,System.currentTimeMillis())
					.eq(Task::getTaskId, task.getTaskId());

			//生成子任务更新条件
			LambdaUpdateWrapper<Task> subTaskSet = new UpdateWrapper<Task>().lambda()
					.set(Task::getUpdateTime,System.currentTimeMillis());

			//设置任务序号
			task.setOrder(menuTaskMaxOrder + 1);
			task.getTaskList().forEach(sub -> {
				if(!task.getProjectId().equals(projectId)){
					subTaskSet.set(Task::getTaskUIds, "");
					subTaskSet.set(Task::getMemberId,ShiroAuthenticationManager.getUserId());
					subTaskSet.set(Task::getExecutor,"");
				}
				subTaskSet.eq(Task::getTaskId, sub.getTaskId());
				//更新子任务
				taskService.update(sub, subTaskSet);

			});
			//如果移动到其他项目中则清除任务的执行者和参与者并且更新任务创建者id为当前操作用户
			if(!task.getProjectId().equals(projectId)){
				taskSet.set(Task::getTaskUIds, "");
				taskSet.set(Task::getExecutor,"");
				taskSet.set(Task::getMemberId, ShiroAuthenticationManager.getUserId());
				//获取到父任务和子任务的id
				taskIds.add(task.getTaskId());
				taskIds.addAll(task.getTaskList().stream().map(Task::getTaskId).collect(Collectors.toList()));
			}
			task.setTaskGroupId(groupId);
			task.setTaskMenuId(toMenuId);
			task.setProjectId(projectId);
			//删除绑定关系
			if(!CollectionUtils.isEmpty(taskIds)){
				tagRelationService.removeBatchByType(taskIds,Constants.TASK);
			}
			//更新父任务
			taskService.update(task, taskSet);
		});
        return true;
    }

	/**
	 * (version 2.0)
	 * 复制列表下的所有任务
	 * @param menuId    要复制的列表id
	 * @param projectId 项目id
	 * @param groupId   分组id
	 * @param toMenuId  复制到的列表id
	 * @return 是否成功
	 */
	@Override
	public boolean copyAllTask(String menuId, String projectId, String groupId, String toMenuId) {
		List<Task> taskByMenuId = taskService.findTaskByMenuId(menuId);
		if(CollectionUtils.isEmpty(taskByMenuId)){
			throw new ServiceException("此列表下的任务为0,无法复制!");
		}
		List<Task> subTasks = new ArrayList<>();
		int menuTaskMaxOrder = this.findMenuTaskMaxOrder(menuId);
		taskByMenuId.forEach(task -> {
			int i = 1;
			task.setTaskId(IdGen.uuid());
			task.setCreateTime(System.currentTimeMillis());
			task.setUpdateTime(System.currentTimeMillis());
			task.setMemberId(ShiroAuthenticationManager.getUserId());
			task.setOrder(menuTaskMaxOrder + i);
			if(!task.getProjectId().equals(projectId)){
				task.setTaskUIds("");
				task.setExecutor("");
			}
			task.setTaskMenuId(toMenuId);
			task.setProjectId(projectId);
			task.setTaskGroupId(groupId);
			task.setPrivacyPattern(1);
			task.setFabulousCount(0);
			task.setTaskStatus(false);
			task.getTaskList().forEach(s -> {
				s.setTaskId(IdGen.uuid());
				s.setCreateTime(System.currentTimeMillis());
				s.setUpdateTime(System.currentTimeMillis());
				s.setPrivacyPattern(1);
				s.setFabulousCount(0);
				s.setTaskStatus(false);
				s.setParentId(task.getTaskId());
				if(!task.getProjectId().equals(projectId)){
					s.setTaskUIds("");
					s.setExecutor("");
				}
				subTasks.add(s);
			});
			i++;
		});
		if(!CollectionUtils.isEmpty(subTasks)){
			taskByMenuId.addAll(subTasks);
		}
		return taskService.saveBatch(taskByMenuId);
	}

	/**
	 * (version 2.0)
	 * 列表下所有任务移动到回收站
	 * @param menuId 列表id
	 * @return 是否成功
	 */
	@Override
	public boolean allTaskMoveRecycleBin(String menuId) {
		List<Task> list = taskService.list(new QueryWrapper<Task>().lambda().eq(Task::getTaskMenuId, menuId).select(Task::getTaskId));
		if(CollectionUtils.isEmpty(list)){
			throw new ServiceException("此列表下的任务为0,无法移动到回收站!");
		}
		list.forEach(t -> {
			t.setTaskDel(1);
			t.setUpdateTime(System.currentTimeMillis());
		});
		return taskService.updateBatchById(list);
	}

	/**
	 * (version 2.0)
	 * 设置该列表下的所有执行者
	 * @param menuId   列表id
	 * @param executor 执行者id
	 * @return 是否成功
	 */
	@Override
	public boolean setAllTaskExecutor(String menuId, String executor) {
		List<Task> list = taskService.list(new QueryWrapper<Task>().lambda().eq(Task::getTaskMenuId, menuId).select(Task::getTaskId));
		if(CollectionUtils.isEmpty(list)){
			throw new ServiceException("此列表下的任务为0,无法设置执行者!");
		}
		list.forEach(t -> {
			t.setExecutor(executor);
			t.setUpdateTime(System.currentTimeMillis());
		});
		return taskService.updateBatchById(list);
	}

	/**
	 * 删除此列表 (version2.0)
	 * @param menuId 列表id
	 * @return 是否成功
	 */
	@Override
	public boolean removeMenu(String menuId) {
		int count = taskService.count(new QueryWrapper<Task>().lambda().eq(Task::getTaskMenuId,menuId).eq(Task::getTaskDel, 0));
		if(count > 0){
			throw new ServiceException("此列表中存在任务,不能删除!");
		}
		return relationMapper.deleteById(menuId) > 0;

	}

	@Override
	public int checkUserIsExistGroup(String groupId) {
		return taskMapper.checkUserIsExistGroup(groupId,ShiroAuthenticationManager.getUserId());
	}

	/**
	 * 通过relationId获取项目id
	 * @param relationId 列表id
	 * @return String
	 */
	@Override
	public String getObject(String relationId) {
		return relationMapper.relationMapper(relationId);
	}

	@Override
	public List<String> getGroupTaskId(String groupId) {
		//sql表达式
		LambdaQueryWrapper<Task> selectTaskIdByGroupQw = new QueryWrapper<Task>().lambda()
				.eq(Task::getTaskGroupId, groupId)
				.select(Task::getTaskId);

		List<Task> groupTask = taskService.list(selectTaskIdByGroupQw);

		if(org.apache.commons.collections.CollectionUtils.isEmpty(groupTask)){
			return new ArrayList<>();
		}

		return groupTask.stream().map(Task::getTaskId).collect(Collectors.toList());
	}

	@Override
	public String getProjectId(String relationId) {
		ValidatedUtil.filterNullParam(relationId);

		LambdaQueryWrapper<Relation> selectProjectIdQw = new QueryWrapper<Relation>().lambda()
				.eq(Relation::getRelationId, relationId)
				.select(Relation::getProjectId);

		Relation one = this.getOne(selectProjectIdQw);

		if(one.getProjectId() == null){
			throw new ServiceException("该记录不属于任何项目！");
		}

		return one.getProjectId();
	}
}


