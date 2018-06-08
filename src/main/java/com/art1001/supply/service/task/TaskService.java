package com.art1001.supply.service.task;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
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
	public void deleteTaskByTaskId(String taskId);

	/**
	 * 修改task数据
	 *
	 * @param task
	 */
	public int updateTask(Task task);

	/**
	 * 保存task数据
	 * 
	 * @param task
	 */
	public void saveTask(Task task);

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
}