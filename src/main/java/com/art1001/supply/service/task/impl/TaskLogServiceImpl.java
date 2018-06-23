package com.art1001.supply.service.task.impl;

import java.util.List;

import javax.annotation.Resource;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.TaskLog;
import com.art1001.supply.entity.task.TaskLogVO;
import com.art1001.supply.mapper.task.TaskLogMapper;
import com.art1001.supply.service.task.TaskLogService;
import org.springframework.stereotype.Service;

/**
 * taskLogServiceImpl
 */
@Service
public class TaskLogServiceImpl implements TaskLogService {

	/** 111DAO接口*/
	@Resource
	private TaskLogMapper taskLogMapper;
	
	/**
	 * 查询分页taskLog数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<TaskLog> findTaskLogPagerList(Pager pager){
		return taskLogMapper.findTaskLogPagerList(pager);
	}

	/**
	 * 通过id获取单条taskLog数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public TaskLog findTaskLogById(String id){
		return taskLogMapper.findTaskLogById(id);
	}

	/**
	 * 通过id删除taskLog数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteTaskLogById(String id){
		taskLogMapper.deleteTaskLogById(id);
	}

	/**
	 * 修改taskLog数据
	 * 
	 * @param taskLog
	 */
	@Override
	public void updateTaskLog(TaskLog taskLog){
		taskLogMapper.updateTaskLog(taskLog);
	}
	/**
	 * 保存taskLog数据
	 * 
	 * @param taskLog
	 */
	@Override
	public void saveTaskLog(TaskLog taskLog){
		taskLogMapper.saveTaskLog(taskLog);
	}
	/**
	 * 获取所有taskLog数据
	 * 
	 * @return
	 */
	@Override
	public List<TaskLog> findTaskLogAllList(){
		return taskLogMapper.findTaskLogAllList();
	}

	@Override
	public TaskLogVO findTaskLogContentById(String id) {
		return taskLogMapper.findTaskLogContentById(id);
	}

	/**
	 * 初始化任务的日志
	 * @param taskId 任务的id
	 * @return
	 */
	@Override
	public List<TaskLog> initTaskLog(String taskId) {
		return taskLogMapper.initTaskLog(taskId);
	}
}