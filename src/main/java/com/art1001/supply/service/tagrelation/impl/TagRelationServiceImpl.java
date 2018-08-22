package com.art1001.supply.service.tagrelation.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.mapper.tagrelation.TagRelationMapper;
import com.art1001.supply.service.tagrelation.TagRelationService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.tagrelation.TagRelation;

/**
 * ServiceImpl
 */
@Service
public class TagRelationServiceImpl implements TagRelationService {

	/** Mapper接口*/
	@Resource
	private TagRelationMapper tagRelationMapper;
	
	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<TagRelation> findTagRelationPagerList(Pager pager){
		return tagRelationMapper.findTagRelationPagerList(pager);
	}

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
	/**
	 * 保存数据
	 * 
	 * @param tagRelation
	 */
	@Override
	public void saveTagRelation(TagRelation tagRelation){
		tagRelationMapper.saveTagRelation(tagRelation);
	}
	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	@Override
	public List<TagRelation> findTagRelationAllList(){
		return tagRelationMapper.findTagRelationAllList();
	}

	@Override
	public List<TagRelation> findTagRelationByTagId(long tagId) {
		return tagRelationMapper.findTagRelationByTagId(tagId);
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
}