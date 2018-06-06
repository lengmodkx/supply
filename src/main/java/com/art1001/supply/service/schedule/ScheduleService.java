package com.art1001.supply.service.schedule;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.schedule.Schedule;


/**
 * scheduleService接口
 */
public interface ScheduleService {

	/**
	 * 查询分页schedule数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Schedule> findSchedulePagerList(Pager pager);

	/**
	 * 通过id获取单条schedule数据
	 * 
	 * @param id
	 * @return
	 */
	public Schedule findScheduleById(String id);

	/**
	 * 通过id删除schedule数据
	 * 
	 * @param id
	 */
	public void deleteScheduleById(String id);

	/**
	 * 修改schedule数据
	 * 
	 * @param schedule
	 */
	public void updateSchedule(Schedule schedule);

	/**
	 * 保存schedule数据
	 * 
	 * @param schedule
	 */
	public void saveSchedule(Schedule schedule);

	/**
	 * 获取所有schedule数据
	 * 
	 * @return
	 */
	public List<Schedule> findScheduleAllList();
	
}