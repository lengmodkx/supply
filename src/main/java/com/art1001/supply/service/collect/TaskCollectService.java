package com.art1001.supply.service.collect;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.TaskCollect;
import org.apache.ibatis.annotations.Param;


/**
 * collectService接口
 */
public interface TaskCollectService {

	/**
	 * 查询分页collect数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<TaskCollect> findTaskCollectPagerList(Pager pager);

	/**
	 * 通过id获取单条collect数据
	 * 
	 * @param id
	 * @return
	 */
	public TaskCollect findTaskCollectById(String id);

	/**
	 * 通过id删除collect数据
	 * 
	 * @param memberId 当前用户id
	 * @param taskId 任务id
	 */
	public int deleteTaskCollectById(String memberId,String taskId);

	/**
	 * 修改collect数据
	 * 
	 * @param taskCollect
	 */
	public void updateTaskCollect(TaskCollect taskCollect);

	/**
	 * 保存collect数据
	 *
     * @param taskCollect
     */
	public int saveTaskCollect(TaskCollect taskCollect);

	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	public List<TaskCollect> findTaskCollectAllList();

	/**
	 * 查询当前用户有没有收藏任务
	 * @param memberId 当前登录用户id
	 * @param taskId 当前任务id
	 * @return
	 */
    int judgeCollectTask(String memberId,String taskId);
}