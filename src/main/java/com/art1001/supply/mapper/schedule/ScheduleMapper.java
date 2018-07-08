package com.art1001.supply.mapper.schedule;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.schedule.ScheduleVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * schedulemapper接口
 */
@Mapper
public interface ScheduleMapper {

	/**
	 * 查询分页schedule数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Schedule> findSchedulePagerList(Pager pager);

	/**
	 * 通过id获取单条schedule数据
	 * 
	 * @param id
	 * @return
	 */
	Schedule findScheduleById(String id);

	/**
	 * 通过id删除schedule数据
	 * 
	 * @param id
	 */
	void deleteScheduleById(String id);

	/**
	 * 修改schedule数据
	 * 
	 * @param schedule
	 */
	void updateSchedule(Schedule schedule);

	/**
	 * 保存schedule数据
	 * 
	 * @param schedule
	 */
	void saveSchedule(Schedule schedule);

	/**
	 * 获取所有schedule数据
	 * 
	 * @return
	 */
	List<Schedule> findScheduleAllList();

    List<Schedule> findByIds(String[] scheduleIds);

	/**
	 * 根据时间分组查询日程
	 * @return
	 */
	List<ScheduleVo> findScheduleGroupByCreateTime();

	/**
	 * 根据月份查询日程列表
	 * @param date
	 * @return
	 */
	List<Schedule> findScheduleList(String date);

}