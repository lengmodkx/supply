package com.art1001.supply.service.task;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskLogVO;
import com.art1001.supply.entity.task.TaskMenuVO;


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
	public TaskLogVO saveTask(String[] memberId,Project project,Task task);

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
	 * 修改当前任务状态（完成/未完成）
	 * @param taskId 任务id
	 * @return
	 */
	TaskLogVO changeTaskStatus(String taskId,String taskStatus);

	/**
	 * 设定任务的时间(开始 / 结束)
	 * @param task 任务的时间信息
	 * @return
	 */
	TaskLogVO updateTaskTime(Task task);

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
}