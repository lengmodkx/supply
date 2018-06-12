package com.art1001.supply.service.tag.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.mapper.tag.TagMapper;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.IdGen;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * tagServiceImpl
 */
@Service
public class TagServiceImpl implements TagService {

	/** tagMapper接口*/
	@Resource
	private TagMapper tagMapper;

	/** 任务Service接口 */
	@Resource
	private TaskService taskService;

	@Resource
	private ScheduleService scheduleService;
	
	/**
	 * 查询分页tag数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Tag> findTagPagerList(Pager pager){
		return tagMapper.findTagPagerList(pager);
	}

	/**
	 * 通过tagId获取单条tag数据
	 * 
	 * @param tagId
	 * @return
	 */
	@Override 
	public Tag findTagByTagId(Long tagId){
		return tagMapper.findTagByTagId(tagId);
	}

	/**
	 * 通过tagId删除tag数据
	 * 
	 * @param tagId
	 */
	@Override
	public void deleteTagByTagId(Long tagId){
		tagMapper.deleteTagByTagId(tagId);
	}

	/**
	 * 修改tag数据
	 * 
	 * @param tag
	 */
	@Override
	public void updateTag(Tag tag){
		tagMapper.updateTag(tag);
	}

	@Override
	public int saveTag(Tag tag, String[] oldTags, String taskId) {
		return 0;
	}

	/**
	 * 保存tag数据
	 *
     * @param tag 标签实体信息
     */
	@Override
	public Long saveTag(Tag tag){
		tag.setCreateTime(System.currentTimeMillis());
		tag.setUpdateTime(System.currentTimeMillis());
		tagMapper.saveTag(tag);
		return tag.getTagId();
    }
	/**
	 * 获取所有tag数据
	 * 
	 * @return
	 */
	@Override
	public List<Tag> findTagAllList(){
		return tagMapper.findTagAllList();
	}

	@Override
	public int findCountByTagName(String projectId, String tagName) {
		return tagMapper.findCountByTagName(projectId, tagName);
	}

	@Override
	public List<Tag> findByProjectId(String projectId) {
		return tagMapper.findByProjectId(projectId);
	}

	@Override
	public Map<String, Object> findByTag(Tag tag) {
		Map<String, Object> map = new HashMap<>();
		// 取出任务id
		String taskIds = tag.getTaskId();
		if (StringUtils.isNoneEmpty(taskIds)) {
			// TODO: 2018/6/12 查询任务
			String[] taskIdArr = taskIds.split(",");
			List<Task> taskList = taskService.findManyTask(taskIdArr);
			map.put(Constants.TASK, taskList);
		}
		// 去除日程id
		String scheduleIds = tag.getScheduleId();
		if (StringUtils.isNoneEmpty(scheduleIds)) {
			String[] scheduleIdArr = scheduleIds.split(",");
			List<Schedule> scheduleList = scheduleService.findByIds(scheduleIdArr);
			map.put(Constants.SCHEDULE, scheduleList);
		}
		// 取出分享id
		String shareIds = tag.getShareId();
		if (StringUtils.isNoneEmpty(shareIds)) {
			// TODO: 2018/6/12 查询分享
			String[] shareIdArr = shareIds.split(",");
		}
		return map;
	}

	@Override
	public List<Tag> findByIds(Integer[] idArr) {
		return tagMapper.findByIds(idArr);
	}

}