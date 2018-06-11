package com.art1001.supply.service.task;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.task.Task;


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
	public int updateTask(Task task);

	/**
	 * 保存task数据
	 * @param task 其他信息
	 */
	public void saveTask(String[] memberId,Project project,Task task);

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
	int moveToRecycleBin(String taskId, String taskDel);

	/**
	 * 修改当前任务状态（完成/未完成）
	 * @param taskId 任务id
	 * @return
	 */
	int changeTaskStatus(String taskId,String taskStatus);

	/**
	 * 设定任务的时间(开始 / 结束)
	 * @param startTime 任务开始时间
	 * @param endTime 任务结束时间
	 * @param remindTime 任务提醒时间
	 * @return
	 */
	int updateTaskTime(String taskId, String startTime, String endTime, String remindTime);

	/**
	 * 根据分组id 查询该菜单下有没有任务
	 * @param taskMenuId 菜单id
	 * @return
	 */
	int findTaskByMenuId(String taskMenuId);

}