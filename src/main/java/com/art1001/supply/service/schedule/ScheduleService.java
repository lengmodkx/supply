package com.art1001.supply.service.schedule;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.schedule.ScheduleApiBean;
import com.art1001.supply.entity.schedule.ScheduleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * scheduleService接口
 */
public interface ScheduleService extends IService<Schedule> {

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
	void deleteScheduleById(String id);

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
	 *
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
	 * @param memberIds 参与者信息
	 */
    void updateMembers(String scheduleId, String memberIds);

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

	/**
	 * 查询出未来的日程
     * @param lable (1:未来 0:过去)
	 * @return
	 */
	List<ScheduleVo> afterSchedule(int lable);

	/**
	 * 查询出近三天的日程
	 * @param uId  当前用户id
	 * @return
	 */
	List<Schedule> findScheduleByUserIdAndThreeDay(String uId);

	/**
	 * 查询出日历上的所有日程
	 * @return
	 */
    List<Schedule> findCalendarSchedule();

	/**
	 * 查询出在该项目回收站中的日程
	 * @param projectId 项目id
	 * @return
	 */
    List<RecycleBinVO> findRecycleBin(String projectId);

	/**
	 * 将日程移入到回收站
	 * @param scheduleId 日程id
	 */
	void moveToRecycleBin(String scheduleId);

	/**
	 * 恢复日程
	 * @param scheduleId 日程id
	 */
	void recoverySchedule(String scheduleId);

	/**
	 * 根据日程id 查询出该日程的所有参与者信息
	 * @param scheduleId 日程id
	 * @return
	 */
    String findUidsByScheduleId(String scheduleId);

	/**
	 * 查询日程的部分信息 (日程名称,开始时间,结束时间,项目名称)
	 * @param id 日程id
	 * @return
	 */
	ScheduleApiBean findScheduleApiBean(String id);

	/**
	 * 根据月份分组查询日程
	 * @param projectId 项目id
	 * @return
	 */
	List<Schedule> findScheduleGroup(String projectId);

	/**
	 * 检测成员时间范围的合法性
	 * @param userId 用户id
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return 冲突的日程信息
	 */
	List<Schedule> testMemberTimeRange(String userId, Long startTime, Long endTime);

	/**
	 * 获取日程信息 根据月份分组
	 * 注:此接口只用于获取绑定信息处
	 * 返回信息包括  id , scheduleName,startTime,endTime
	 * @param projectId 项目id
	 * @return 日程信息集合
	 */
	List<ScheduleVo> getBeforeByMonth(String projectId);

	/**
	 * 获取日程信息
	 * 注:此接口只用于获取绑定信息处
	 * 返回信息包括  id , scheduleName,startTime,endTime
	 * @param projectId 项目id
	 * @return 日程信息集合
	 */
	List<Schedule> getAfterBind(String projectId);
}