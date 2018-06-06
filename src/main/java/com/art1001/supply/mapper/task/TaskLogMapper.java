package com.art1001.supply.mapper.task;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.TaskLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 111mapper接口
 */
@Mapper
public interface TaskLogMapper {

	/**
	 * 查询分页taskLog数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<TaskLog> findTaskLogPagerList(Pager pager);

	/**
	 * 通过id获取单条taskLog数据
	 * 
	 * @param id
	 * @return
	 */
	TaskLog findTaskLogById(String id);

	/**
	 * 通过id删除taskLog数据
	 * 
	 * @param id
	 */
	void deleteTaskLogById(String id);

	/**
	 * 修改taskLog数据
	 * 
	 * @param taskLog
	 */
	void updateTaskLog(TaskLog taskLog);

	/**
	 * 保存taskLog数据
	 * 
	 * @param taskLog
	 */
	void saveTaskLog(TaskLog taskLog);

	/**
	 * 获取所有taskLog数据
	 * 
	 * @return
	 */
	List<TaskLog> findTaskLogAllList();

}