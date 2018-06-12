package com.art1001.supply.mapper.task;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * taskmapper接口
 */
@Mapper
public interface TaskMapper {

	/**
	 * 根据任务id数组查找多个任务
	 * @param taskId 任务id数组
	 * @return
	 */
	List<Task> findManyTask(String[] taskId);

    /**
	 * 查询分页task数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Task> findTaskPagerList(Pager pager);

	/**
	 * 通过taskId获取单条task数据
	 * 
	 * @param taskId
	 * @return
	 */
	Task findTaskByTaskId(String taskId);

	/**
	 * 删除任务
	 * 通过taskId删除task数据
	 * @param taskId 任务id
	 */
	int deleteTaskByTaskId(String taskId);

	/**
	 * 修改task数据
	 * 
	 * @param task
	 */
	int updateTask(Task task);

	/**
	 * 保存task数据
	 * 
	 * @param task
	 */
	void saveTask(Task task);

	/**
	 * 获取所有task数据
	 * 
	 * @return
	 */
	List<Task> findTaskAllList();

	/**
	 * 移入回收站/恢复任务
	 * @param taskId 任务id
	 * @param taskDel 任务是否在回收站
	 * @param updateTime 更新时间
	 * @return
	 */
    int moveToRecycleBin(@Param("taskId") String taskId, @Param("taskDel") String taskDel, @Param("updateTime") long updateTime);

	/**
	 * 修改当前任务状态
	 * @param taskId 当前任务id
	 * @param updateTime 当前时间毫秒数
     * @return
	 */
	int changeTaskStatus(@Param("taskId") String taskId, @Param("taskStatus") String taskStatus, @Param("updateTime") long updateTime);

	/**
	 * 判断当前菜单有没有任务
	 * @param taskMenuId 菜单id
	 * @return
	 */
    int findTaskByMenuId(String taskMenuId);
}