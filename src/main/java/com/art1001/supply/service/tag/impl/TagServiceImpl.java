package com.art1001.supply.service.tag.impl;

import java.util.*;
import javax.annotation.Resource;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.tag.TagMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import org.springframework.web.bind.annotation.ResponseBody;

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

	@Resource
	private FileService fileService;

	@Resource
	private ShareService shareService;
	
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
	public Tag findById(Integer tagId){
		return tagMapper.findById(tagId);
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

	/**
	 * 保存tag数据
	 *
     * @param tag 标签实体信息
     */
	@Override
	public Tag saveTag(Tag tag){
		tag.setCreateTime(System.currentTimeMillis());
		tag.setUpdateTime(System.currentTimeMillis());
		UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
		tag.setMemberId(userEntity.getId());
        tagMapper.saveTag(tag);
        return tag;
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

	/**
	 * 根据用户输入的标签名称模糊查询出标签
	 * @param tagName 标签名称
	 * @return 标签集合
	 */
	@Override
	public List<Tag> searchTag(String tagName) {
		return tagMapper.searchTag(tagName);
	}

    /**
     * 移除标签
     * 逻辑步骤
     * 1:根据publicId 查询出 (任务,文件,日程,分享)的 标签id字符串
     * 2:把查询出来的标签字符串转为集合
     * 3:比较 如果当前集合中存在要被移除的标签id 则remove掉
     * 4:更新最新的标签id 至数据库
     * @param publicId
     * @param publicType
     * @param tagId
     */
	@Override
	public void removeTag(String publicId, String publicType, String tagId) {
		String tags = "";
		//根据publicId 和 pubilcType 查询出 标签字符串
		if(BindingConstants.BINDING_TASK_NAME.equals(publicType)){
			Task taskByTaskId = taskService.findTaskByTaskId(publicId);
			tags = taskByTaskId.getTagId();
		}
		if(BindingConstants.BINDING_FILE_NAME.equals(publicType)){
			File file = fileService.findFileById(publicId);
			tags = file.getTagId();
		}
		if(BindingConstants.BINDING_SCHEDULE_NAME.equals(publicType)){
			Schedule schedule = scheduleService.findScheduleById(publicId);
			tags = schedule.getTagId();
		}
		if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
			Share share = shareService.findById(publicId);
			tags = share.getTagIds();
		}
		//字符串根据逗号分隔转为集合
		List<String> tagsIds = Arrays.asList(tags.split(","));
        List<String> newTagsIds = new ArrayList<String>(tagsIds);
        //判断 如果集合中存在要被移除的标签id  就 在当前集合中remove掉
		if(newTagsIds.contains(tagId)){
            newTagsIds.remove(tagId);
		}
		//把集合转为字符串  得到最新的标签信息  更新数据库
        String join = StringUtils.join(newTagsIds, ",");
        if(BindingConstants.BINDING_TASK_NAME.equals(publicType)){
            if(StringUtils.isEmpty(join)){
                taskService.clearTaskTag(publicId);
            } else {
                Task task = new Task();
                task.setTaskId(publicId);
                task.setUpdateTime(System.currentTimeMillis());
                task.setTagId(join);
                taskService.updateTask(task);
            }
		}
		if(BindingConstants.BINDING_FILE_NAME.equals(publicType)){
			if(StringUtils.isEmpty(join)){
			    fileService.fileClearTag(publicId);
            } else{
                File file = new File();
                file.setFileId(publicId);
                file.setTagId(join);
                file.setUpdateTime(System.currentTimeMillis());
			    fileService.updateFile(file);
            }
		}
		if(BindingConstants.BINDING_SCHEDULE_NAME.equals(publicType)){
			if(StringUtils.isEmpty(join)){
			    scheduleService.clearScheduleTag(publicId);
            } else{
                Schedule schedule = new Schedule();
                schedule.setScheduleId(publicId);
                schedule.setTagId(join);
                schedule.setUpdateTime(System.currentTimeMillis());
			    scheduleService.updateSchedule(schedule);
            }
		}
		if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
			if(StringUtils.isEmpty(join)){
			    shareService.shareClearTag(publicId);
            } else{
                Share share = new Share();
                share.setId(publicId);
                share.setUpdateTime(System.currentTimeMillis());
                share.setTagIds(join);
			    shareService.updateShare(share);
            }
		}
	}

    /**
     * 添加标签
     * 步骤逻辑
     * 1:查询出标签id  字符串
     * 2:如果 原先已经存在标签id 则把新的标签插入到集合第一个位置 否则 直接add进去
     * 3:更新最新的标签信息至数据库
     * @param tagId 标签id
     * @param publicId (任务 ,文件, 日程, 分享) id
     * @param publicType (任务,文件,日程,分享) 类型
     */
    @Override
    public void addItemTag(String tagId, String publicId, String publicType) {
        String tags = "";
        //查询出标签信息
        if(BindingConstants.BINDING_TASK_NAME.equals(publicType)){
            Task taskByTaskId = taskService.findTaskByTaskId(publicId);
            tags = taskByTaskId.getTagId();
        }
        if(BindingConstants.BINDING_FILE_NAME.equals(publicType)){
            File file = fileService.findFileById(publicId);
            tags = file.getTagId();
        }
        if(BindingConstants.BINDING_SCHEDULE_NAME.equals(publicType)){
            Schedule schedule = scheduleService.findScheduleById(publicId);
            tags = schedule.getTagId();
        }
        if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
            Share share = shareService.findById(publicId);
            tags = share.getTagIds();
        }
        if(StringUtils.isEmpty(tags)){
            tags = "";
        }
        //字符串转成集合  添加新的标签id
        List<String> tagsIds = Arrays.asList(tags.split(","));
        List<String> newTagIds = new ArrayList<String>(tagsIds);
        if(!tagsIds.isEmpty()){
            newTagIds.add(0,tagId);
        } else{
            newTagIds.add(tagId);
        }
        //集合转化为字符串 准备更新至数据库
        String join = StringUtils.join(newTagIds, ",");
        //更新到数据库
        if(BindingConstants.BINDING_TASK_NAME.equals(publicType)){
            Task task = new Task();
            task.setTaskId(publicId);
            task.setUpdateTime(System.currentTimeMillis());
            task.setTagId(join);
            taskService.updateTask(task);
        }
        if(BindingConstants.BINDING_FILE_NAME.equals(publicType)){
            File file = new File();
            file.setFileId(publicId);
            file.setTagId(join);
            file.setUpdateTime(System.currentTimeMillis());
            fileService.updateFile(file);
        }
        if(BindingConstants.BINDING_SCHEDULE_NAME.equals(publicType)){
            Schedule schedule = new Schedule();
            schedule.setScheduleId(publicId);
            schedule.setTagId(join);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleService.updateSchedule(schedule);
        }
        if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
            Share share = new Share();
            share.setId(publicId);
            share.setUpdateTime(System.currentTimeMillis());
            share.setTagIds(join);
            shareService.updateShare(share);
        }
    }

	/**
	 * 保存多条tag
	 * @param newTagList 多个tag信息i
	 */
	@Override
	public void saveMany(List<Tag> newTagList) {
		tagMapper.saveMany(newTagList);
	}
}