package com.art1001.supply.service.task;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.TaskFile;


/**
 * taskFileService接口
 */
public interface TaskFileService {

	/**
	 * 查询分页taskFile数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<TaskFile> findTaskFilePagerList(Pager pager);

	/**
	 * 通过id获取单条taskFile数据
	 * 
	 * @param id
	 * @return
	 */
	public TaskFile findTaskFileById(String id);

	/**
	 * 通过id删除taskFile数据
	 * 
	 * @param id
	 */
	public void deleteTaskFileById(String id);

	/**
	 * 修改taskFile数据
	 * 
	 * @param taskFile
	 */
	public void updateTaskFile(TaskFile taskFile);

	/**
	 * 保存taskFile数据
	 * 
	 * @param taskFile
	 */
	public void saveTaskFile(TaskFile taskFile);

	/**
	 * 获取所有taskFile数据
	 * 
	 * @return
	 */
	public List<TaskFile> findTaskFileAllList();
	
}