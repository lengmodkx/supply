package com.art1001.supply.mapper.schedule;

import java.util.List;

import com.art1001.supply.entity.Dto.SheduleTimeoutDTO;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.schedule.ScheduleApiBean;
import com.art1001.supply.entity.schedule.ScheduleVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * schedulemapper接口
 */
@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

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
	 * 查询关于用户的所有未来日程信息按照日期分组
	 * @param userId 用户id
	 * @return
	 */
	List<ScheduleVo> selectMe(@Param("userId") String userId, @Param("currTime") Long currTime);

	/**
	 * 查询出日历上的所有日程
	 * @param uId 当前登录用户的id
	 * @return
	 */
    List<Schedule> findCalendarSchedule(String uId);

	/**
	 * 查询出在该项目回收站中的日程
	 * @param projectId 项目id
	 * @return
	 */
    List<RecycleBinVO> findRecycleBin(String projectId);

	/**
	 * 日程移入到回收站
	 * @param scheduleId 日程id
	 * @param currTime 当前时间
	 */
	@Update("update prm_schedule set is_del = 1,update_time = #{currTime} where schedule_id = #{scheduleId}")
	void moveToRecycleBin(@Param("scheduleId") String scheduleId, @Param("currTime")long currTime);

	/**
	 * 恢复日程
	 * @param scheduleId 日程id
	 */
	@Update("update prm_schedule set is_del = 0 where schedule_id = #{scheduleId}")
	void recoverySchedule(String scheduleId);

	/**
	 * 根据日程id 查询出该日程的所有参与者信息
	 * @param scheduleId 日程id
	 * @return
	 */
	@Select("select member_ids from prm_schedule where schedule_id = #{scheduleId}")
    String findUidsByScheduleId(String scheduleId);

	/**
	 * 查询日程的部分信息 (日程名称,开始时间,结束时间,项目名称)
	 * @param id 日程id
	 * @return
	 */
    ScheduleApiBean selectScheduleApiBean(String id);

	/**
	 * 月份分组查询每月已过去的日程
	 * @param currTime 当前时间
	 * @param projectId 项目id
	 * @return
	 */
	List<Schedule> findScheduleGroup(@Param("currTime") Long currTime, @Param("projectId") String projectId);

	/**
	 * 获取日程信息 根据月份分组
	 * @param projectId 项目id
	 * @return 日程信息
	 */
	List<ScheduleVo> getBeforeByMonth(@Param("projectId") String projectId);

	/**
	 * 获取日程信息
	 * @param projectId 项目id
	 * @return 日程信息集合
	 */
	List<Schedule> getAfterBind(@Param("projectId") String projectId);

	/**
	 * 查询出和当前登录用户有关日程的月份信息
	 * @param userId 当前用户id
	 * @param currentTimeMillis 当前时间
	 * @return 月份集合
	 */
	List<SheduleTimeoutDTO> findScheduleMonth(@Param("userId") String userId, @Param("currTime") long currentTimeMillis);

	/**
	 * 根据月份获取日程信息
	 * @param month 月份
	 * @param userId 用户id
	 * @param currentTimeMillis 当前时间
	 * @return 日程集合
	 */
	List<Schedule> findByMonth(@Param("month") String month, @Param("userId") String userId, @Param("currTime") long currentTimeMillis);

	/**
	 * 获取一个项目的日历日程信息 (version 2.0)
	 * @param projectId 项目id
	 * @return 日历日程信息
	 */
    List<Schedule> selectCalendarSchedule(@Param("projectId") String projectId);

	/**
	 * 获取一个用户的日历日程信息
	 * @param userId 用户id
	 * @return 日程信息
	 */
	List<Schedule> relevant(@Param("userId") String userId);

	/**
	 * 获取绑定该标签的所有日程信息
	 * @param tagId 标签id
	 * @return 日程id
	 */
    List<Schedule> selectBindTagInfo(@Param("tagId") Long tagId);

	List<Schedule> getScheduleList(@Param("project_id") String projectId, @Param("member_id") String memberId);
}