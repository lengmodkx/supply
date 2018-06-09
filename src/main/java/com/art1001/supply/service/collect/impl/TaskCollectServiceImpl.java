package com.art1001.supply.service.collect.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.collect.TaskCollect;
import com.art1001.supply.mapper.collect.TaskCollectMapper;
import com.art1001.supply.service.collect.TaskCollectService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * collectServiceImpl
 */
@Service
public class TaskCollectServiceImpl implements TaskCollectService {

	/** collectMapper接口*/
	@Resource
	private TaskCollectMapper taskCollectMapper;
	
	/**
	 * 查询分页collect数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<TaskCollect> findTaskCollectPagerList(Pager pager){
		return taskCollectMapper.findTaskCollectPagerList(pager);
	}

	/**
	 * 通过id获取单条collect数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public TaskCollect findTaskCollectById(String id){
		return taskCollectMapper.findTaskCollectById(id);
	}

	/**
	 * 通过id删除collect数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteTaskCollectById(String id){
		taskCollectMapper.deleteTaskCollectById(id);
	}

	/**
	 * 修改collect数据
	 * 
	 * @param taskCollect
	 */
	@Override
	public void updateTaskCollect(TaskCollect taskCollect){
		taskCollectMapper.updateTaskCollect(taskCollect);
	}
	/**
	 * 保存collect数据
	 * 
	 * @param taskCollect
	 */
	@Override
	public void saveTaskCollect(TaskCollect taskCollect){
		taskCollectMapper.saveTaskCollect(taskCollect);
	}
	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	@Override
	public List<TaskCollect> findTaskCollectAllList(){
		return taskCollectMapper.findTaskCollectAllList();
	}
	
}