package com.art1001.supply.service.schedule.impl;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.file.FilePushType;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.schedule.ScheduleLogFunction;
import com.art1001.supply.entity.schedule.ScheduleVo;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.mapper.schedule.ScheduleMapper;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.IdGen;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * scheduleServiceImpl
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

	/** scheduleMapper接口*/
	@Resource
	private ScheduleMapper scheduleMapper;

	@Resource
	private UserService userService;

	@Resource
	private LogService logService;

	@Resource
	private SimpMessagingTemplate messagingTemplate;
	/**
	 * 查询分页schedule数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Schedule> findSchedulePagerList(Pager pager){
		return scheduleMapper.findSchedulePagerList(pager);
	}

	/**
	 * 通过id获取单条schedule数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public Schedule findScheduleById(String id){
		return scheduleMapper.findScheduleById(id);
	}

	/**
	 * 通过id删除schedule数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteScheduleById(String id){
		scheduleMapper.deleteScheduleById(id);
	}

	/**
	 * 修改schedule数据
	 * 
	 * @param schedule
	 */
	@Override
	public Log updateSchedule(Schedule schedule){
		StringBuilder content = new StringBuilder();
		//更新开始时间  结束时间
		if(!StringUtils.isEmpty(schedule.getScheduleName())){
			content.append(ScheduleLogFunction.A.getName()).append(" ").append(ScheduleLogFunction.D.getName());
		}

		//更新重复规则
		if(!StringUtils.isEmpty(schedule.getRepeat())){
			content.append(ScheduleLogFunction.A.getName()).append(" ").append(ScheduleLogFunction.E.getName());
		}

		//更新提醒模式
		if(!StringUtils.isEmpty(schedule.getRemind())){
			content.append(ScheduleLogFunction.A.getName()).append(" ").append(ScheduleLogFunction.F.getName());
		}

		//更新日程地址
		if(!StringUtils.isEmpty(schedule.getAddress())){
			content.append(ScheduleLogFunction.A.getName()).append(" ").append(ScheduleLogFunction.G.getName());
		}
		//更新日程全天模式
		if(schedule.getIsAllday() != null) {
			if (schedule.getIsAllday() == 0) {
				content.append(ScheduleLogFunction.J.getName()).append(" ").append(ScheduleLogFunction.I.getName());
			} else {
				content.append(ScheduleLogFunction.J.getName()).append(" ").append(ScheduleLogFunction.H.getName());
			}
		}
		schedule.setUpdateTime(System.currentTimeMillis());
		Log log = new Log();
		if(!StringUtils.isEmpty(content.toString())){
			log = logService.saveLog(schedule.getScheduleId(),content.toString(),3);
		}
		scheduleMapper.updateSchedule(schedule);
		return log;
	}
	/**
	 * 保存schedule数据
	 * 
	 * @param schedule
	 */
	@Override
	public void saveSchedule(Schedule schedule){
		schedule.setScheduleId(IdGen.uuid());
		scheduleMapper.saveSchedule(schedule);
	}
	/**
	 * 获取所有schedule数据
	 * 
	 * @return
	 */
	@Override
	public List<Schedule> findScheduleAllList(){
		return scheduleMapper.findScheduleAllList();
	}

	@Override
	public List<Schedule> findByIds(String[] scheduleIds) {
		return scheduleMapper.findByIds(scheduleIds);
	}

	@Override
	public List<ScheduleVo> findScheduleGroupByCreateTime(Long currTime,String projectId) {
		return scheduleMapper.findScheduleGroupByCreateTime(currTime,projectId);
	}

	/**
	 * 查询出该用户参与的近三天的日程
	 * @param uId 用户Id
	 * @return
	 */
	@Override
	public List<Schedule> findScheduleByUserIdAndByTreeDay(String uId) {
		return scheduleMapper.findScheduleByUserIdAndByTreeDay(uId);
	}

	/**
	 * 数据:根据项目查询出该项目下的所有日程信息
	 * @param projectId 项目id
	 * @return 日程的实体集合
	 */
	@Override
	public List<Schedule> findScheduleListByProjectId(String projectId) {
		return scheduleMapper.findScheduleListByProjectId(projectId);
	}

	/**
	 * 查询以前的日程
	 * @param currTime 当前的时间
	 * @param identification 标识
	 * @return 返回日程信息
	 */
	@Override
	public List<Schedule> findBeforeSchedule(long currTime,String projectId,int identification) {
		return scheduleMapper.findBeforeSchedule(currTime,projectId,identification);
	}

	/**
	 * 添加或者移除参与者
	 * @param scheduleId 日程的id
	 * @param addUserEntity 新的参与者信息
	 */
	@Override
	public void addAndRemoveScheduleMember(String scheduleId, String addUserEntity) {
		//查询出当前文件中的 参与者id
		Schedule scheduleById = scheduleMapper.findScheduleById(scheduleId);

		//log日志
		Log log = new Log();
		StringBuilder logContent = new StringBuilder();

		//将数组转换成集合
		List<String> oldJoin = Arrays.asList(scheduleById.getMemberIds().split(","));
		List<String> newJoin = Arrays.asList(addUserEntity.split(","));

		//比较 oldJoin 和 newJoin 两个集合的差集  (移除)
		List<String> reduce1 = oldJoin.stream().filter(item -> !newJoin.contains(item)).collect(Collectors.toList());
		if(reduce1 != null && reduce1.size() > 0){
			logContent.append(TaskLogFunction.B.getName()).append(" ");
			for (String uId : reduce1) {
				String userName = userService.findUserNameById(uId);
				logContent.append(userName).append(" ");
			}
		}
		//比较 newJoin  和 oldJoin 两个集合的差集  (添加)
		List<String> reduce2 = newJoin.stream().filter(item -> !oldJoin.contains(item)).collect(Collectors.toList());
		if(reduce2 != null && reduce2.size() > 0){
			logContent.append(TaskLogFunction.C.getName()).append(" ");
			for (String uId : reduce2) {
				String userName = userService.findUserNameById(uId);
				logContent.append(userName).append(" ");
			}
		}

		//如果没有参与者变动直接返回
		if((reduce1 == null && reduce1.size() == 0) && (reduce2 == null && reduce2.size() == 0)){
			return;
		} else{
			Schedule schedule = new Schedule();
			schedule.setScheduleId(scheduleId);
			schedule.setMemberIds(addUserEntity);
			schedule.setUpdateTime(System.currentTimeMillis());
			scheduleMapper.updateSchedule(schedule);
			log = logService.saveLog(scheduleId,logContent.toString(),2);

			//推送信息
			FilePushType filePushType = new FilePushType(TaskLogFunction.A19.getName());
			Map<String,Object> map = new HashMap<String,Object>();
			List<UserEntity> adduser = new ArrayList<UserEntity>();
			map.put("log",log);
			for (String id : reduce2) {
				adduser.add(userService.findById(id));
			}
			map.put("reduce2",reduce2);
			map.put("reduce1",reduce1);
			map.put("adduser",adduser);
			map.put("scheduleId",scheduleId);
			filePushType.setObject(map);
			//推送至文件的详情界面
			messagingTemplate.convertAndSend("/topic/"+scheduleId,new ServerMessage(JSON.toJSONString(filePushType)));
		}
	}

	/**
	 * 更新日程的开始时间或者结束时间
	 * @param scheduleId  日程id
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return 操作的日志信息
	 */
	@Override
	public Log updateScheduleStartAndEndTime(String scheduleId, String startTime, String endTime) {
		Schedule schedule = new Schedule();
		schedule.setScheduleId(scheduleId);
		StringBuilder content = new StringBuilder();
		//日期字符串转为毫秒数
		if(!StringUtils.isEmpty(startTime)){
			schedule.setStartTime(DateUtils.strToLong(startTime));
			content.append(ScheduleLogFunction.A.getName()).append(" ").append(ScheduleLogFunction.B.getName());
		}
		if(!StringUtils.isEmpty(endTime)){
			schedule.setEndTime(DateUtils.strToLong(endTime));
			content.append(ScheduleLogFunction.A.getName()).append(" ").append(ScheduleLogFunction.C.getName());
		}
		schedule.setUpdateTime(System.currentTimeMillis());
		scheduleMapper.updateSchedule(schedule);
		Log log = logService.saveLog(scheduleId, content.toString(), 3);
		return log;
	}

	/**
	 * 清空日程的id
	 * @param scheduleId 日程的id
	 */
	@Override
	public void clearScheduleTag(String scheduleId) {
		scheduleMapper.clearScheduleTag(scheduleId);
	}
}