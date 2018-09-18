package com.art1001.supply.service.tagrelation;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.tagrelation.TagRelation;

/**
 * Service接口
 */
public interface TagRelationService {

	/**
	 * 通过id获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	public TagRelation findTagRelationById(String id);

	/**
	 * 通过id删除数据
	 * 
	 * @param id
	 */
	public void deleteTagRelationById(String id);

	/**
	 * 修改数据
	 * 
	 * @param tagRelation
	 */
	public void updateTagRelation(TagRelation tagRelation);

	/**
	 * 保存数据
	 * 
	 * @param tagRelation
	 */
	public void saveTagRelation(TagRelation tagRelation);

	List<TagRelation> findTagRelationByTagId(long tagId);

	/**
	 * 插入多条消息
	 * @param relations 标签关系实体集合
	 */
    void saveManyTagRelation(List<TagRelation> relations);

	/**
	 * 删除此项的标签关联信息
	 * @param publicId 项的id
	 * @param publicType 删除的项的类型 (任务,文件,分享,日程)
	 */
	void deleteItemTagRelation(String publicId,String publicType);

	/**
	 * 删除多个项的标签关联信息
	 * @param publicId 项的id
	 */
	void deleteManyItemTagRelation(List<String> publicId);
}