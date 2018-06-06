package com.art1001.supply.mapper.task;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.TaskFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * taskFilemapper接口
 */
@Mapper
public interface TaskFileMapper {

	/**
	 * 查询分页taskFile数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<TaskFile> findTaskFilePagerList(Pager pager);

	/**
	 * 通过id获取单条taskFile数据
	 * 
	 * @param id
	 * @return
	 */
	TaskFile findTaskFileById(String id);

	/**
	 * 通过id删除taskFile数据
	 * 
	 * @param id
	 */
	void deleteTaskFileById(String id);

	/**
	 * 修改taskFile数据
	 * 
	 * @param taskFile
	 */
	void updateTaskFile(TaskFile taskFile);

	/**
	 * 保存taskFile数据
	 * 
	 * @param taskFile
	 */
	void saveTaskFile(TaskFile taskFile);

	/**
	 * 获取所有taskFile数据
	 * 
	 * @return
	 */
	List<TaskFile> findTaskFileAllList();

}