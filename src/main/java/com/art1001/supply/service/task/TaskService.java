package com.art1001.supply.service.task;

import java.util.List;
import java.util.Map;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskLogVO;
import com.art1001.supply.entity.task.TaskMenuVO;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfoEntity;


/**
 * taskService接口
 */
public interface TaskService {

	/**
	 * 查询分页task数据
	 *
	 * @param pager 分页对象
	 * @return
	 */
	public List<Task> findTaskPagerList(Pager pager);

	/**
	 * 通过taskId获取单条task数据
	 *
	 * @param taskId
	 * @return
	 */
	public Task findTaskByTaskId(String taskId);

	/**
	 * 通过taskId删除task数据
	 *
     * @param taskId
     */
	public int deleteTaskByTaskId(String taskId);

	/**
	 * 修改task数据
	 *
	 * @param task
	 */
	public Log updateTask(Task task);

	/**
	 * 保存task数据
	 * @param task 其他信息
	 */
	public Log saveTask(Task task);

	/**
	 * 获取所有task数据
	 *
	 * @return
	 */
	public List<Task> findTaskAllList();

	/**
	 * 任务 移入回收站
	 * @param taskId 当前任务id
	 * @return
	 */
	Log moveToRecycleBin(String taskId);

	/**
	 * 修改当前任务状态（完成/重做）
	 * @param task 任务id
	 * @return
	 */
	Log resetAndCompleteTask(Task task);

	/**
	 * 设定任务的时间(开始 / 结束)
	 * @param task 任务的时间信息
	 * @return
	 */
	Log updateTaskStartAndEndTime(Task task);

	/**
	 * 根据菜单id 查询该菜单下有没有任务
	 * @param taskMenuId 菜单id
	 * @return
	 */
	int findTaskByMenuId(String taskMenuId);

	/**
	 * 移动任务
	 * @param task 任务的信息
	 * @param taskMenuVO 当前任务所在的 菜单,分组,项目 的信息
	 * @return
	 */
	Log mobileTask(Task task, TaskMenuVO taskMenuVO,TaskMenuVO newTaskMenuVO);

//	/**
//	 * 保存任务操作日志
//	 * @param task 任务实体信息
//	 * @param content 日志内容
//	 * @return
//	 */
//	Log saveTaskLog(Task task,String content);

	/**
	 * 根据任务id数组,查找出多个任务
	 * @param taskId 任务id数组
	 * @return
	 */
	List<Task> findManyTask(String[] taskId);

	/**
	 * 将任务转为顶级任务
	 * @param task 包含任务的id,名称
	 * @return
	 */
	Log turnToFatherLevel(Task task);

	/**
	 * 给任务添加标签
	 * @param tag 标签实体信息
	 * @param taskId 当前任务的id
	 * @param countByTagName 判断要绑定到任务上的标签是不是已经存在
	 * @return
	 */
	Log addTaskTags(Tag tag,String taskId,int countByTagName);

	/**
	 * 移除该任务上的标签
	 * @param taskId 当前任务uid
	 * @return
	 */
	void removeTaskTag(String tagId, String taskId);


	/**
	 * 更新任务的重复规则
	 * @param task 任务的实体信息
	 * @param object 时间重复周期的具体信息 (未设定)
	 * @return
	 */
	Log updateTaskRepeat(Task task, Object object);

	/**
	 * 更新任务提醒时间 和 指定提醒某个成员
	 * @param task 任务实体信息
	 * @param userEntity 用户实体信息
	 * @return
	 */
	Log updateTaskRemindTime(Task task, UserEntity userEntity);

	/**
	 * 清除任务的开始时间和结束时间
	 * @param task 任务的实体信息
	 * @return
	 */
	Log removeTaskStartTime(Task task);

	/**
	 * 清除任务的开始时间和结束时间
	 * @param task 任务的实体信息
	 * @return
	 */
	Log removeTaskEndTime(Task task);

	/**
	 * 添加参与者
	 * @return
	 */
	Log addAndRemoveTaskMember(String taskId,String memberIds);

	/**
	 * 移除任务参与者
	 * @param task 当前任务实体信息
	 * @param userEntity 被移除的用户的信息
	 * @return
	 */
	Log removeTaskMember(Task task, UserEntity userEntity);

	/**
	 * 给当前任务点赞
	 * @param task 任务的实体信息
	 * @return
	 */
    int clickFabulous(Task task);

	/**
	 * 判断用户有没有该任务点赞
	 * @param task 任务的实体信息
	 * @return
	 */
	boolean judgeFabulous(Task task);

	/**
	 * 用户取消对当前任务的赞
	 * @param task 当前任务信息
	 * @return
	 */
	int cancelFabulous(Task task);

	/**
	 * 给当前任务添加子级任务
	 * @param parentTaskId 父任务的id
	 * @param subLevel 子级任务信息
	 * @return
	 */
	Log addSubLevelTasks(String parentTaskId, Task subLevel);

	/**
	 * 完成子任务 和 重做子任务
	 * @param task 当前任务信息
	 * @return
	 */
	Log resetAndCompleteSubLevelTask(Task task);

	/**
	 * 复制任务
	 * @return
	 * @param task 当前任务信息
	 * @param projectId 当前任务所在的项目id
	 * @param newTaskMenuVO 复制到的位置的信息
	 */
	Log copyTask(Task task, String projectId, TaskMenuVO newTaskMenuVO);

	/**
	 * 收藏任务
	 * @param task 任务实体信息
	 * @return
	 */
	int collectTask(Task task);

	/**
	 * 判断当前登录用户有没有收藏该任务
	 * @param task 当前用户的信息
	 * @return
	 */
	boolean judgeCollectTask(Task task);

	/**
	 * 取消收藏的任务
	 * @param task 任务的信息
	 * @return
	 */
	int cancelCollectTask(Task task);

	/**
	 * 更改当前任务的隐私模式
	 * @param task 任务的实体信息
	 * @return
	 */
	int settingUpPrivacyPatterns(Task task);

	/**
	 * 查询当前项目下的所有成员信息
	 * @param projectId 项目id
	 * @param executor 任务的执行者id
	 * @return
	 */
	List<UserEntity> findProjectAllMember(String projectId,String executor);

	/**
	 * 智能分组 分别为  查询 今天的任务 , 已完成任务, 未完成任务
	 * @param status 任务状态条件
	 * @param projectId 项目id
	 * @return
	 */
	List<Task> intelligenceGroup(String status,String projectId);

	/**
	 * 查询某个菜单下的所有任务的信息及任务的执行者信息
	 * @param menuId 菜单id
	 * @return
	 */
	List<Task> taskMenu(String menuId);

	/**
	 * 查询菜单下的任务信息 不包括 执行者信息
	 * @return
	 */
	List<Task> simpleTaskMenu(String menuId);

	/**
	 * 查询某个人执行的所有任务
	 * @param uId 当前用户id
	 * @param orderType 任务的排序类型
	 * @return
	 */
	List<Task> findTaskByExecutor(String uId,String orderType);

	/**
	 * 查询等待认领的任务
	 * @return
	 */
	List<Task> waitClaimTask(String projectId);

	/**
	 * 移除该任务的执行者 改为待认领状态
	 * @param taskId 任务的id
	 */
	Log removeExecutor(String taskId);

	/**
	 * 更新任务执行者
	 * @param taskId 该任务的id
	 * @param uName 新的任务执行者的名字
	 * @param executor 新的执行者的id
	 * @return
	 */
	Log updateTaskExecutor(String taskId,String executor, String uName);

	/**
	 * 查询该项目下的所有任务
	 * @param projectId 项目id
	 * @return
	 */
	List<Task> findTaskByProject(String projectId);

	/**
	 * 查询项目下的指定的优先级的任务
	 * @param projectId 项目id
	 * @param priority 优先级别
	 * @return
	 */
	List<Task> findTaskByPriority(String projectId,String priority);

	/**
	 * 查询某个任务下的所有子任务
	 * @param taskId 父级任务id
	 * @return
	 */
	List<Task> findTaskByFatherTask(String taskId);

	/**
	 * 恢复任务的功能
	 * @param taskId 任务的id
	 * @param menuId 恢复后放到哪个菜单
	 * @param projectId 项目id
	 */
	void recoveryTask(String taskId, String menuId,String projectId);

	/**
	 * 查询此任务的关联
	 * @param taskId 任务id
	 */
	Map<String,List> findTaskRelation(String taskId);

	/**
	 * 根据任务id查询任务的创建者id
	 * @param taskId 任务的id
	 * @return
	 */
	String findTaskMemberIdByTaskId(String taskId);


	/**
	 * 重新排序一个任务菜单的任务顺序
	 * @param oldMenuTaskId 旧任务菜单的所有任务id
	 * @param oldMenuId  旧任务菜单的id
	 * @param newMenuId 新的任务菜单的id
	 * @param newMenuTaskId 新的任务菜单的所有任务id
	 * @param taskId 任务的id
	 */
	void orderOneTaskMenu(String[]oldMenuTaskId,String[] newMenuTaskId,String oldMenuId,String newMenuId,String taskId);

	/**
	 * 根据任务的id 查询出该任务的所有标签
	 * @param taskId 任务id
	 * @return
	 */
	List<Tag> findTaskTag(String taskId);

	/**
	 * 查询出该用户参与的近三天的任务
	 * @param userId 用户id
	 * @return
	 */
	List<Task> findTaskByUserIdAndByTreeDay(String userId);

	/**
	 * 查询出我创建的任务信息
	 * @param memberId 创建者的id
	 * @return
	 */
	List<Task> findTaskByMemberId(String memberId);

	/**
	 * 查询出我参与的任务
	 * @param id 当前用户id
	 *
	 * @return
	 */
	List<Task> findTaskByUserId(String id,String orderType);

	/**
	 * 查询出当前用户执行的所有任务信息 并且按照创建时间或者截止时间排序
	 * @param id 用户id
	 * @param orderType 按照时间排序的类型  (创建时间,截止时间)
	 * @return
	 */
	List<Task> findTaskByExecutorIdAndTime(String id, String orderType);

	/**
	 * 查询出该用户执行的 已经完成的任务
	 * @param id 用户id
	 * @return
	 */
	List<Task> findTaskByExecutorAndStatus(String id);

	/**
	 * 查询出该用户所参与的任务(按照任务的状态)
	 * @param id 用户id
	 * @param status 任务的状态
	 * @return
	 */
	List<Task> findTaskByUserIdByStatus(String id, String status);

	/**
	 * 查询出该用户所参与的任务 按照时间排序
	 * @param id 用户id
	 * @param orderType 比较类型
	 * @return
	 */
	List<Task> findTaskByUserAndTime(String id, String orderType);

	/**
	 * 查询出该用户创建的任务 (根据任务状态查询)
	 * @param id 用户id
	 * @param status 任务的状态
	 * @return
	 */
	List<Task> findTaskByCreateMemberByStatus(String id, String status);

	/**
	 * 查询出我创建的任务 只要未完成
	 * @param id 当前用户id
	 * @param orderType 排序类型
	 * @return
	 */
	List<Task> findTaskByCreateMember(String id,String orderType);

	/**
	 * 查询出用户创建的所有任务并且按照时间排序
	 * @param id 用户id
	 * @param orderType 排序类型
	 * @return
	 */
	List<Task> findTaskByCreateMemberAndTime(String id, String orderType);

	/**
	 * 查询该用户在日历上创建的所有任务
	 * @param uId 用户id
	 * @return
	 */
	List<Task> findTaskByCalendar(String uId);

	/**
	 * 根据任务的id 查询出任务的名称
	 * @param taskId 任务id
	 * @return 任务名称
	 */
	String getTaskNameById(String taskId);

	/**
	 * 清空任务的标签
	 * @param publicId 任务id
	 */
    void clearTaskTag(String publicId);
}