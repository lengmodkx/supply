package com.art1001.supply.service.schedule;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.schedule.ScheduleVo;


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

    List<Schedule> findByIds(String[] scheduleIds);

	/**
	 * 根据时间分组查询日程
	 * @return
	 */
	List<Schedule> findScheduleGroupByCreateTime(Long currTime,String projectId);

	/**
	 * 查询该用户参与的近三天的日程
	 * @param uId 用户Id
	 * @return
	 */
    List<Schedule> findScheduleByUserIdAndByTreeDay(String uId);

	/**
	 * 根据项目id 查询出该项目下的所有日程信息
	 * @param projectId 项目id
	 * @return 日程的实体信息集合
	 */
	List<Schedule> findScheduleListByProjectId(String projectId);

	/**
	 * 查询以前的日程
	 * @param currTime 当前的时间
	 * @return
	 */
	List<Schedule> findBeforeSchedule(long currTime,String projectId);
}