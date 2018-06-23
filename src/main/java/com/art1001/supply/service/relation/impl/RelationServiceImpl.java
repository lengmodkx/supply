package com.art1001.supply.service.relation.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.entity.task.TaskMenuVO;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.mapper.relation.RelationMapper;
import com.art1001.supply.mapper.task.TaskMapper;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.task.TaskMemberService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * relationServiceImpl
 */
@Service
public class RelationServiceImpl implements RelationService {

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
	/**taskMember 接口  */
	private TaskMemberService taskMemberService;

	@Resource
	/**userService接口  */
	private UserService userService;

	/**
	 * 查询分页relation数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Relation> findRelationPagerList(Pager pager){
		return relationMapper.findRelationPagerList(pager);
	}

	/**
	 * 通过relationId获取单条relation数据
	 * 
	 * @param relationId
	 * @return
	 */
	@Override 
	public Relation findRelationByRelationId(String relationId){
		return relationMapper.findRelationByRelationId(relationId);
	}

	/**
	 * 通过relationId删除relation数据
	 * 
	 * @param relationId
	 */
	@Override
	public void deleteRelationByRelationId(String relationId){
		relationMapper.deleteRelationByRelationId(relationId);
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
		relationMapper.saveRelation(relation);
	}
	/**
	 * 获取所有relation数据
	 * 
	 * @return
	 */
	@Override
	public List<Relation> findRelationAllList(Relation relation){
		return relationMapper.findRelationAllList(relation);
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
		relation.setRelationId(IdGen.uuid());
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
	 * @param userInfoEntity 新的执行者的id
	 */
	@Override
	public void setMenuAllTaskExecutor(String relationId,UserInfoEntity userInfoEntity,String uName) {
		//获取菜单下所有的任务信息
		List<Task> tasks = taskService.simpleTaskMenu(relationId);
		if(tasks != null && tasks.size() > 0){
			for (Task task: tasks) {
				//给任务设置新的执行者
				task.setExecutor(userInfoEntity.getId());
				//设置更新时间
				task.setUpdateTime(System.currentTimeMillis());
				//更新任务信息
				taskMapper.updateTask(task);
				//删除此任务原来的执行者和任务的关联信息
				taskMemberService.removeExecutor(task.getTaskId());
				//添加任务成员关系
				TaskMember taskMember = new TaskMember();
				taskMember.setId(IdGen.uuid());
				taskMember.setMemberId(userInfoEntity.getId());
				taskMember.setCurrentTaskId(task.getTaskId());
				taskMember.setMemberName(uName);
				taskMember.setMemberImg(userInfoEntity.getImage());
				taskMember.setType("执行者");
				taskMember.setCreateTime(System.currentTimeMillis());
				taskMember.setUpdateTime(System.currentTimeMillis());
				//把执行者和任务的关联信息添加到库中
				taskMemberService.saveTaskMember(taskMember);
				//查询新的任务执行者之前是不是此任务的参与者
				int isTaskMember = taskMemberService.findTaskMemberExecutorIsMember(userInfoEntity.getId(), task.getTaskId());
				//如果新的任务执行者以前已经是该任务的参与者  就不在添加该执行者的参与者信息
				if(isTaskMember == 0){
					taskMember.setId(IdGen.uuid());
					taskMember.setType("参与者");
					taskMemberService.saveTaskMember(taskMember);
				}
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
				taskService.updateTaskStartAndEndTime(newTask);
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
				taskService.mobileTask(newTask,oldTaskMenuVO,newTaskMenuVO);
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
				taskService.copyTask(task,oldTaskMenuVO.getProjectId() ,newTaskMenuVO);
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
	 * 根据任务id 查询该任务的菜单信息.
	 * @param taskId 任务id
	 * @return
	 */
	@Override
	public Relation findMenuInfoByTaskId(String taskId) {
		return relationMapper.findMenuInfoByTaskId(taskId);
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
}