package com.art1001.supply.service.schedule;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.log.Log;
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
	public Log updateSchedule(Schedule schedule);

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
	List<ScheduleVo> findScheduleGroupByCreateTime(Long currTime,String projectId);

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
	 * @param projectId 项目id
	 * @param identification 标识  (用来区分 是查询出小于当前时间的还是大于当前时间的  0 大于 1 小于)
	 * @return
	 */
	List<Schedule> findBeforeSchedule(long currTime,String projectId,int identification);

	/**
	 * 添加或者移除参与者
	 * @param scheduleId 日程的id
	 * @param newJoin 新的参与者信息
	 */
    void addAndRemoveScheduleMember(String scheduleId, String newJoin);

	/**
	 * 更新日程的开始时间或者结束时间
	 * @param scheduleId  日程id
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 */
    Log updateScheduleStartAndEndTime(String scheduleId, String startTime, String endTime);

	/**
	 * 清空某个日程的标签
	 * @param scheduleId 日程的id
	 */
	void clearScheduleTag(String scheduleId);

	/**
	 * 根据日程id 查询出该日程的名称
	 * @param publicId 日程id
	 * @return 名称
	 */
    String findScheduleNameById(String publicId);
}