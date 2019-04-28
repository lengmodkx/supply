package com.art1001.supply.service.task;

import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.GantChartVO;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.statistics.Statistics;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskApiBean;
import com.art1001.supply.entity.task.TaskRemindRule;
import com.art1001.supply.entity.template.TemplateData;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;

import java.util.List;
import java.util.Map;


/**
 * taskService接口
 */
public interface TaskService extends IService<Task> {



	/**
	 * 通过taskId获取单条task数据
	 *
	 * @param taskId
	 * @return
	 */
	Task findTaskByTaskId(String taskId);

	List<GantChartVO> buildFatherSon(List<Task> tasks);

	/**
	 * 获取子任务的项目id
	 * @param taskId 子任务id
	 * @return 项目id
	 */
	String findChildTaskProject(String taskId);

	/**
	 * 保存task数据
	 * @param task 其他信息
	 * @param taskRemindRules 提醒规则
	 * @param tagIds 任务的标签
	 */
	//void saveTask(Task task, String taskRemindRules, String tagIds);

	/**
	 * 保存task数据
	 * @param task 其他信息
	 */
	void saveTask(Task task);

	void saveTaskBatch(String projectId, String menuId, List<TemplateData> templateDataList);

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
	 * 移动任务
	 * @param taskId 任务id
	 * @param projectId 项目id
	 * @param groupId 组id
	 * @param menuId 菜单id
	 * @return
	 */
	void mobileTask(String taskId, String projectId, String groupId,String menuId);

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
	 * 给当前任务点赞
	 * @param task 任务的实体信息
	 * @return
	 */
    int clickFabulous(Task task);

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
	 * @return 新生成的任务实体信息
	 * @param taskId 当前任务信息
	 * @param projectId 项目id
	 * @param groupId 组id
	 * @param menuId 菜单id
	 */
	Task copyTask(String taskId, String projectId, String groupId,String menuId);

	/**
	 * 收藏任务
	 * @param task 任务实体信息
	 * @return
	 */
	int collectTask(Task task);

	/**
	 * 取消收藏的任务
	 * @param task 任务的信息
	 * @return
	 */
	int cancelCollectTask(Task task);

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
	List<Task> findTaskByMenuId(String menuId);

	/**
	 * 查询菜单下的任务信息 不包括 执行者信息
	 * @return
	 */
	List<Task> simpleTaskMenu(String menuId);

	/**
	 * 根据任务的id 获取当前所属的项目id
	 * @param taskId 任务id
	 * @return 项目id
	 */
	String findProjectIdByTaskId(String taskId);

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
	 */
	void recoveryTask(String taskId, String menuId);

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
	 * 查询出和该用户有关的近三天的任务
	 * @return
	 */
	List<Task> findByUserIdAndByTreeDay();

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

	/**
	 * 根据任务的id 查询出该任务的名称
	 * @param taskId 任务id
	 * @return
	 */
	String findTaskNameById(String taskId);

	/**
	 * 取消收藏任务
	 * @param taskId 任务id
	 */
	void cancleCollectTask(String taskId);

	/**
	 * 查询出该项目下的所有任务 状态数量概览
	 * @param projectId 项目id
	 * @return
	 */
    List<Statistics> findTaskCountOverView(String projectId);

	/**
	 * 查询出在回收站中的任务
	 * @param projectId 项目id
	 * @return 该项目下所有在回收站的任务集合
	 */
	List<RecycleBinVO> findRecycleBin(String projectId);

	/**
	 * 删除一个任务的所有子任务
	 * @param taskId 任务id
	 */
	void deleteSubTaskByParentId(String taskId);


	/**
	 * 根据任务的id 查询出该任务的 所有参与者信息
	 * @param taskId 任务id
	 * @return
	 */
    String findUidsByTaskId(String taskId);

	/**
	 * 永久删除多个任务
	 * @param taskIds 任务id 的集合
	 */
	void deleteManyTask(List<String> taskIds);

	/**
	 * 查询饼图数据
	 * @param projectId 项目id
	 * @return list
	 */
	List<StringBuilder> findPieChartOverView(String projectId);

	/**
	 * 查询柱状图数据
	 * @param projectId 项目id
	 * @return list
	 */
	List<StringBuilder> findHistogramOverView(String projectId);

	/**
	 * 查询双容器柱状图数据
	 * @param projectId 项目id
	 * @return list
	 */
	List findDoubleHistogramOverView(String projectId);

	/**
	 * 查询出需要被关联的任务信息
	 * @param id 任务id集合
	 */
    TaskApiBean findTaskApiBean(String id);

	/**
	 * 生成任务提醒的规则
	 * @param taskId 任务id
	 * @param remindType 提醒类型
	 * @param num 数量
	 * @param timeType 时间类型
	 * @param customTime 自定义时间的字符串
	 * @return cron 表达式
	 */
	String remindCron(String taskId,String remindType, Integer num, String timeType, String customTime) throws ServiceException;

	/**
	 * 添加任务的提醒规则
	 * @param taskRemindRule 规则实体
	 * @param users 提醒的用户id字符串
	 */
    void addTaskRemind(TaskRemindRule taskRemindRule, String users) throws ServiceException;

	/**
	 * 更新任务的提醒规则
	 * @param taskRemindRule 提醒规则实体信息
	 */
	void updateTaskRemind(TaskRemindRule taskRemindRule) throws SchedulerException;

	/**
	 * 移除一条任务提醒规则
	 * @param id
	 */
	void removeRemind(String id) throws SchedulerException;

	/**
	 * 更新任务要提醒的成员信息
	 * @param taskId 任务id
	 * @param users 成员id 信息
	 */
	void updateRemindUsers(String taskId, String users) throws SchedulerException;

	/**
	 * 完成任务
	 * @param taskId 任务id
	 */
	Task completeTask(String taskId);

	/**
	 * 更新任务的开始时间
	 * @param taskId 任务id
	 * @param startTime 新的开始时间
	 */
    void updateStartTime(String taskId, Long startTime);

	/**
	 * 任务排序
	 * @param taskIds 排序后的菜单下所有任务id
	 * @param taskId 拖动的任务id
	 * @param newMenu 推动到的任务菜单id
	 */
	void orderTask(String taskIds, String taskId, String newMenu);

	/**
	 * 返回任务的视图信息
	 * @param taskId 任务id
	 * @return 任务视图信息
	 */
	Task taskInfoShow(String taskId);

	/**
	 * 获取一个任务的人员信息
	 * 人员信息 参与者+执行者 的id
	 * @param taskId 任务id
	 * @return 参与者 + 执行者的id数组
	 */
	String[] getTaskJoinAndExecutorId(String taskId);

	/**
	 * 获取绑定信息的子任务信息
	 * 注意子任务信息 只包括(任务id,任务执行者头像,任务名称)
	 * @param taskId 任务id
	 * @return 子任务信息集合
	 */
	List<Task> getBindChild(String taskId);

	/**
	 * 查询" 我的" 任务并且按照筛选条件进行筛选
	 * 我的任务就是(当前用户 参与,创建 项目中和当前用户有关的所有任务)
	 * @param isDone 是否完成
	 * @param order 根据 (最近创建时间,截止时间,优先级) 排序
	 * @param type 查询类型 (我执行的,我创建的,我参与的)
	 * @return 任务集合
	 */
	List<Task> findMeAndOrder(Boolean isDone, String order, String type);

	/**
	 * 查询出我执行的任务并且按照项目排序
	 * 每个项目对象下包括一个任务集合 项目:任务 一对多
	 * @param isDone 是否完成 (筛选条件)
	 * @return 项目集合
	 */
	List<Project> findExecuteOrderProject(Boolean isDone);

	/**
	 * 根据id集合 查询出对应的任务信息 以及执行者信息
	 * @param idList id集合
	 * @return
	 */
	List<Task> listById(List<String> idList);

	/**
	 * 补全任务的信息 (关联关系,附件,是否收藏,是否点赞,等等)
	 * @param task 基本任务信息
	 */
	void completionTaskInfo(Task task);

	/**
	 * 查询出一个任务基本信息
	 * 用户任务简便信息的显示
	 * @param taskId 任务id
	 * @return 任务信息
	 */
    Task findSimpleTaskById(String taskId);

	/**
	 * 把该子任务向上递归直到获取到顶级父任务的项目id
	 * @param id 子任务id
	 * @return 项目id
	 */
	String findChildProjectId(String id);
}