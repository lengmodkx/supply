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
	 * @param memberId 当前用户id
	 * @param taskId 任务id
	 */
	@Override
	public int deleteTaskCollectById(String memberId,String taskId){
		return taskCollectMapper.deleteTaskCollectById(memberId,taskId);
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
	public int saveTaskCollect(TaskCollect taskCollect){
		return taskCollectMapper.saveTaskCollect(taskCollect);
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

	/**
	 * 判断当前用户有没有收藏该任务
	 * @param memberId 当前登录用户id
	 * @param taskId 当前任务id
	 * @return
	 */
	@Override
	public int judgeCollectTask(String memberId, String taskId) {
		return taskCollectMapper.judgeCollectTask(memberId,taskId);
	}

}