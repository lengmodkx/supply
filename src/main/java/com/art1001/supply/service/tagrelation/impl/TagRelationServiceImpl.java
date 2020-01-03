package com.art1001.supply.service.tagrelation.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.tag.TagRelation;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.tagrelation.TagRelationMapper;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * ServiceImpl
 */
@Service
public class TagRelationServiceImpl extends ServiceImpl<TagRelationMapper,TagRelation> implements TagRelationService {

	/** Mapper接口*/
	@Resource
	private TagRelationMapper tagRelationMapper;

	/**
	 * 通过id获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public TagRelation findTagRelationById(String id){
		return tagRelationMapper.findTagRelationById(id);
	}

	/**
	 * 通过id删除数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteTagRelationById(String id){
		tagRelationMapper.deleteTagRelationById(id);
	}

	/**
	 * 修改数据
	 * 
	 * @param tagRelation
	 */
	@Override
	public void updateTagRelation(TagRelation tagRelation){
		tagRelationMapper.updateTagRelation(tagRelation);
	}

	/*
	* 查询是否有关联重复的tag
	* */
	@Override
	public int findCountById(Long tagId, String publicType, String publicId) {
		TagRelation tagRelation=new TagRelation();
        tagRelation.setTagId(tagId);
		if(Constants.TASK.equals(publicType)){
			tagRelation.setTaskId(publicId);
		}
		else if(Constants.FILE.equals(publicType)){
			tagRelation.setFileId(publicId);
		}
		else if(Constants.SCHEDULE.equals(publicType)){
			tagRelation.setScheduleId(publicId);
		}
		else if(Constants.SHARE.equals(publicType)){
			tagRelation.setShareId(publicId);

		}
		return tagRelationMapper.findCountById(tagRelation);
	}



	/**
	 * 插入多条消息
	 * @param relations 标签关系实体集合
	 */
	@Override
	public void saveManyTagRelation(List<TagRelation> relations) {
		tagRelationMapper.saveManyTagRelation(relations);
	}

	/**
	 * 删除此项的标签关联信息
	 * @param publicId 项的id
	 * @param publicType 删除的项的类型 (任务,文件,分享,日程)
	 */
	@Override
	public void deleteItemTagRelation(String publicId,String publicType) {
		tagRelationMapper.deleteItemTagRelation(publicId,publicType);
	}

	/**
	 * 删除多个项的标签关联信息
	 * @param publicId 项的id
	 */
	@Override
	public void deleteManyItemTagRelation(List<String> publicId) {
		tagRelationMapper.deleteManyItemTagRelation(publicId);
	}

	/**
	 * 删除标签和指定类型的绑定关系
	 * @param ids  绑定的信息id集合
	 * @param type 信息类型
	 * @return 是否成功
	 */
	@Override
	public boolean removeBatchByType(Collection ids, String type) {
		if(CollectionUtils.isEmpty(ids) || StringUtils.isNotEmpty(type)){
			throw new ServiceException("ids 或者 type 参数不合法!");
		}
		LambdaQueryWrapper<TagRelation> eq = new QueryWrapper<TagRelation>().lambda();
		if(Constants.TASK.equals(type)){
			eq.in(TagRelation::getTaskId, ids);
		}
		if(Constants.FILE.equals(type)){
			eq.in(TagRelation::getFileId, ids);
		}
		if(Constants.SCHEDULE.equals(type)){
			eq.in(TagRelation::getScheduleId, ids);
		}
		if(Constants.SHARE.equals(type)){
			eq.in(TagRelation::getShareId, ids);
		}
		return tagRelationMapper.delete(eq) > 0;
	}


	@Override
	public List<TagRelation> findTagRelationByScheduleId(String id) {

		return tagRelationMapper.findTagRelationByScheduleId(id);
	}
}