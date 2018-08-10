package com.art1001.supply.mapper.schedule;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.schedule.ScheduleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
	List<ScheduleVo> findScheduleGroupByCreateTime(@Param("currTime") Long currTime,@Param("projectId") String projectId);

	/**
	 * 根据月份查询日程列表
	 * @param date
	 * @return
	 */
	List<Schedule> findScheduleList(String date);

	/**
	 * 查询出用户最近三天参与的日程
	 * @param uId 用户id
	 * @return
	 */
    List<Schedule> findScheduleByUserIdAndByTreeDay(String uId);

	/**
	 * 查询出该项目下的所有日程信息
	 * @param projectId 项目的id
	 * @return 日程的实体信息集合
	 */
	List<Schedule> findScheduleListByProjectId(String projectId);

	/**
	 * 查询出过去的日程
	 * @param currTime 当前系统时间
	 * @param projectId 项目的id
	 * @param ident 标识  (0 查询大于当前时间的日程, 1 查询出小于当前时间的日程)
	 * @return 日程的实体信息集合
	 */
	List<Schedule> findBeforeSchedule(@Param("currTime") long currTime,@Param("projectId") String projectId,@Param("ident")int ident);

	/**
	 * 清空日程的标签
	 * @param scheduleId 日程id
	 */
	@Update("update prm_schedule set tag_id = '' where schedule_id = #{scheduleId}")
    void clearScheduleTag(String scheduleId);

	/**
	 * 根据日程id 查询出该日程的名称
	 * @param publicId 日程id
	 * @return
	 */
	@Select("select schedule_name from prm_schedule where schedule_id = #{publicId}")
    String findScheduleNameById(String publicId);

	/**
	 * 查询出未来的日程
	 * @param currdate 当前时间
	 * @param lable (0:过去 1: 未来)
	 * @param userId 当前用户id
	 * @return
	 */
    List<ScheduleVo> afterSchedule(@Param("currdate") long currdate, @Param("lable") int lable,@Param("userId")String userId);

	/**
	 * 查询出近三天的日程
	 * @param uId  当前用户id
	 * @return
	 */
	List<Schedule> findScheduleByUserIdAndThreeDay(String uId);

	/**
	 * 查询出日历上的所有日程
	 * @param uId 当前登录用户的id
	 * @return
	 */
    List<Schedule> findCalendarSchedule(String uId);
}