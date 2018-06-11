package com.art1001.supply.service.tag.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.mapper.tag.TagMapper;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.IdGen;
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
	public Tag findTagByTagId(String tagId){
		return tagMapper.findTagByTagId(tagId);
	}

	/**
	 * 通过tagId删除tag数据
	 * 
	 * @param tagId
	 */
	@Override
	public void deleteTagByTagId(String tagId){
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
	public int saveTag(Tag tag,String[] oldTags,String taskId){
		//设置标签id
		tag.setTagId(IdGen.uuid());
		StringBuilder newTagName = new StringBuilder("");
		//在原来的标签名字基础上添加新的标签名称
		if(oldTags != null){
			for (int i = 0; i < oldTags.length ; i++) {
				newTagName.append(oldTags[i]);
				newTagName.append(",");
			}
		}
		newTagName.append(tag.getTagName());
		Task task = new Task();
		task.setTaskId(taskId);
		//给该任务设置新标签名称
		task.setTagId(newTagName.toString());
		//更新任务标签
		taskService.updateTask(task);
		//保存标签至数据库
		return tagMapper.saveTag(tag);
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
	
}