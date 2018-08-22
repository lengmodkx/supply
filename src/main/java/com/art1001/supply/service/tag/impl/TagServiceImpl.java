package com.art1001.supply.service.tag.impl;

import java.util.*;
import javax.annotation.Resource;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.tagrelation.TagRelation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.tag.TagMapper;
import com.art1001.supply.mapper.tagrelation.TagRelationMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import org.springframework.web.bind.annotation.RequestParam;
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

	@Resource
	private TagRelationMapper tagRelationMapper;

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
		//删除和这个标签绑定的关系信息
		tagRelationMapper.deleteTagRelationByTagId(tagId);
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
		tag.setIsDel(0);
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
     * @param publicId
     * @param publicType
     * @param tagId
     */
	@Override
	public void removeTag(String publicId, String publicType, long tagId) {
		TagRelation tagRelation = new TagRelation();
		tagRelation.setTagId(tagId);
		if("任务".equals(publicType)){
			tagRelation.setTaskId(publicId);
		}else if("文件".equals(publicType)){
			tagRelation.setFileId(publicId);
		}else if("日程".equals(publicType)){
			tagRelation.setScheduleId(publicId);
		}else{
			tagRelation.setShareId(publicId);
		}
		tagRelationMapper.deleteTagRelationByPublicId(tagRelation);
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
    public void addItemTag(long tagId, String publicId, String publicType) {
		TagRelation tagRelation = new TagRelation();
		tagRelation.setId(IdGen.uuid());
		tagRelation.setTagId(tagId);
		if("任务".equals(publicType)){
			tagRelation.setTaskId(publicId);
		}else if("文件".equals(publicType)){
			tagRelation.setFileId(publicId);
		}else if("日程".equals(publicType)){
            tagRelation.setScheduleId(publicId);
		}else{
			tagRelation.setShareId(publicId);
		}

		tagRelationMapper.saveTagRelation(tagRelation);
    }

	/**
	 * 保存多条tag
	 * @param newTagList 多个tag信息i
	 */
	@Override
	public void saveMany(List<Tag> newTagList) {
		tagMapper.saveMany(newTagList);
	}

	@Override
	public List<Tag> findByPublicId(String publicId, String publicType) {
		return tagMapper.findByPublicId(publicId,publicType);
	}

	/**
	 * 查询出在该项目回收站中的标签
	 * @param projectId 项目id
	 * @return
	 */
	@Override
	public List<Tag> findRecycleBin(String projectId) {
		return tagMapper.findRecycleBin(projectId);
	}

	/**
	 * 将标签移入回收站
	 * @param tagId 标签id
	 */
	@Override
	public void moveToRecycleBin(String tagId) {
		tagMapper.moveToRecycleBin(tagId,System.currentTimeMillis());
	}

	/**
	 * 恢复标签
	 * @param tagId 标签id
	 */
	@Override
	public void recoveryTag(String tagId) {
		tagMapper.recoveryTag(tagId);
	}
}