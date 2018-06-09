package com.art1001.supply.mapper.collect;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.TaskCollect;
import org.apache.ibatis.annotations.Mapper;

/**
 * collectmapper接口
 */
@Mapper
public interface TaskCollectMapper {

	/**
	 * 查询分页collect数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<TaskCollect> findTaskCollectPagerList(Pager pager);

	/**
	 * 通过id获取单条collect数据
	 * 
	 * @param id
	 * @return
	 */
	TaskCollect findTaskCollectById(String id);

	/**
	 * 通过id删除collect数据
	 * 
	 * @param id
	 */
	void deleteTaskCollectById(String id);

	/**
	 * 修改collect数据
	 * 
	 * @param taskCollect
	 */
	void updateTaskCollect(TaskCollect taskCollect);

	/**
	 * 保存collect数据
	 * 
	 * @param taskCollect
	 */
	void saveTaskCollect(TaskCollect taskCollect);

	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	List<TaskCollect> findTaskCollectAllList();

}