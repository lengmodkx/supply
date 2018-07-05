package com.art1001.supply.service.schedule.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.schedule.ScheduleVo;
import com.art1001.supply.mapper.schedule.ScheduleMapper;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.util.IdGen;
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
	public void updateSchedule(Schedule schedule){
		scheduleMapper.updateSchedule(schedule);
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
	public List<ScheduleVo> findScheduleGroupByCreateTime() {
		return scheduleMapper.findScheduleGroupByCreateTime();
	}

}