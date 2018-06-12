package com.art1001.supply.service.task;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.TaskLog;
import com.art1001.supply.entity.task.TaskLogVO;

import java.util.List;

/**
 * taskLogService接口
 */
public interface TaskLogService {

	/**
	 * 查询分页taskLog数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<TaskLog> findTaskLogPagerList(Pager pager);

	/**
	 * 通过id获取单条taskLog数据
	 * 
	 * @param id
	 * @return
	 */
	public TaskLog findTaskLogById(String id);

	/**
	 * 通过id删除taskLog数据
	 * 
	 * @param id
	 */
	public void deleteTaskLogById(String id);

	/**
	 * 修改taskLog数据
	 * 
	 * @param taskLog
	 */
	public void updateTaskLog(TaskLog taskLog);

	/**
	 * 保存taskLog数据
	 * 
	 * @param taskLog
	 */
	public void saveTaskLog(TaskLog taskLog);

	/**
	 * 获取所有taskLog数据
	 * 
	 * @return
	 */
	public List<TaskLog> findTaskLogAllList();

	/**
	 * 根据id查询日志的内容
	 * @param id 日志的id
	 * @return
	 */
	TaskLogVO findTaskLogContentById(String id);
}