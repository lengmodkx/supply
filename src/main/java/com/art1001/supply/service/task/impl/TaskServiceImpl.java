package com.art1001.supply.service.task.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.task.Task;
import com.art1001.supply.mapper.task.TaskMapper;
import com.art1001.supply.service.task.TaskService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * taskServiceImpl
 */
@Service
public class TaskServiceImpl implements TaskService {

	/** taskMapper接口*/
	@Resource
	private TaskMapper taskMapper;
	
	/**
	 * 查询分页task数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Task> findTaskPagerList(Pager pager){
		return taskMapper.findTaskPagerList(pager);
	}

	/**
	 * 通过taskId获取单条task数据
	 * 
	 * @param taskId
	 * @return
	 */
	@Override 
	public Task findTaskByTaskId(String taskId){
		return taskMapper.findTaskByTaskId(taskId);
	}

	/**
	 * 通过taskId删除task数据
	 * 
	 * @param taskId
	 */
	@Override
	public void deleteTaskByTaskId(String taskId){
		taskMapper.deleteTaskByTaskId(taskId);
	}

	/**
	 * 修改task数据
	 * 
	 * @param task
	 */
	@Override
	public void updateTask(Task task){
		taskMapper.updateTask(task);
	}
	/**
	 * 保存task数据
	 * 
	 * @param task
	 */
	@Override
	public void saveTask(Task task){
		taskMapper.saveTask(task);
	}
	/**
	 * 获取所有task数据
	 * 
	 * @return
	 */
	@Override
	public List<Task> findTaskAllList(){
		return taskMapper.findTaskAllList();
	}
	
}