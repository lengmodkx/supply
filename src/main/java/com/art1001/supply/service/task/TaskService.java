package com.art1001.supply.service.task;

import java.util.List;
import java.util.Map;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.Project;
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
	public TaskLogVO updateTask(Task task);

	/**
	 * 保存task数据
	 * @param task 其他信息
	 */
	public TaskLogVO saveTask(UserEntity[] memberId,Project project,Task task);

	/**
	 * 获取所有task数据
	 * 
	 * @return
	 */
	public List<Task> findTaskAllList();

	/**
	 * 任务 (移入回收站/回复)
	 * @param taskId 当前任务id
	 * @param taskDel 当前任务是否已经在回收站
	 * @return
	 */
	TaskLogVO moveToRecycleBin(String taskId, String taskDel);

	/**
	 * 修改当前任务状态（完成/重做）
	 * @param task 任务id
	 * @return
	 */
	TaskLogVO resetAndCompleteTask(Task task);

	/**
	 * 设定任务的时间(开始 / 结束)
	 * @param task 任务的时间信息
	 * @return
	 */
	TaskLogVO updateTaskStartAndEndTime(Task task);

	/**
	 * 根据分组id 查询该菜单下有没有任务
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
	TaskLogVO mobileTask(Task task, TaskMenuVO taskMenuVO,TaskMenuVO newTaskMenuVO);

	/**
	 * 保存任务操作日志
	 * @param task 任务实体信息
	 * @param content 日志内容
	 * @return
	 */
	TaskLogVO saveTaskLog(Task task,String content);

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
	TaskLogVO turnToFatherLevel(Task task);

	/**
	 * 给任务添加标签
	 * @param tag 标签实体信息
	 * @param taskId 当前任务的id
	 * @param countByTagName 判断要绑定到任务上的标签是不是已经存在
	 * @return
	 */
	TaskLogVO addTaskTags(Tag tag,String taskId,int countByTagName);

	/**
	 * 移除该任务上的标签
	 * @param tags 当前任务上绑定的所有标签对象数组
	 * @param tag 当前要被的标签对象
	 * @param taskId 当前任务uid
	 * @return
	 */
	int removeTaskTag(Tag[] tags, Tag tag, String taskId);


	/**
	 * 更新任务的重复规则
	 * @param task 任务的实体信息
	 * @param object 时间重复周期的具体信息 (未设定)
	 * @return
	 */
	TaskLogVO updateTaskRepeat(Task task, Object object);

	/**
	 * 更新任务提醒时间 和 指定提醒某个成员
	 * @param task 任务实体信息
	 * @param userEntity 用户实体信息
	 * @return
	 */
	TaskLogVO updateTaskRemindTime(Task task, UserEntity userEntity);

	/**
	 * 清除任务的开始时间和结束时间
	 * @param task 任务的实体信息
	 * @return
	 */
	TaskLogVO removeTaskStartAndEndTime(Task task);

	/**
	 * 添加参与者
	 * @param task 任务实体信息
	 * @param userEntity 多个用户的信息
	 * @return
	 */
	TaskLogVO addTaskMember(Task task, UserEntity[] userEntity);

	/**
	 * 移除项目成员
	 * @param task 当前项目实体信息
	 * @param userEntity 被移除的用户的信息
	 * @return
	 */
	TaskLogVO removeTaskMember(Task task, UserEntity[] userEntity);

	/**
	 * 给当前任务点赞
	 * @param task 任务的实体信息
	 * @return
	 */
    int clickFabulous(Task task);

	boolean judgeFabulous(Task task);

	/**
	 * 用户取消对当前任务的赞
	 * @param task 当前任务信息
	 * @return
	 */
	int cancelFabulous(Task task);

	/**
	 * 给当前任务添加子级任务
	 * @param currentTask 当前任务 信息
	 * @param subLevel 子级任务信息
	 * @return
	 */
	TaskLogVO addSubLevelTasks(Task currentTask, Task subLevel);

	/**
	 * 完成子任务 和 重做子任务
	 * @param task 当前任务信息
	 * @return
	 */
	TaskLogVO resetAndCompleteSubLevelTask(Task task);

	/**
	 * 复制任务
	 * @return
	 * @param task 当前任务信息
	 */
	TaskLogVO copyTask(Task task);

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
	int SettingUpPrivacyPatterns(Task task);

	/**
	 * 查询当前项目下的所有成员信息
	 * @param projectId
	 * @return
	 */
	List<UserEntity> findProjectAllMember(String projectId);

}