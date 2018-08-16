package com.art1001.supply.mapper.tagrelation;

import java.util.List;
import com.art1001.supply.entity.tagrelation.TagRelation;
import com.art1001.supply.entity.base.Pager;
import org.apache.ibatis.annotations.Mapper;

/**
 * mapper接口
 */
@Mapper
public interface TagRelationMapper {

	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<TagRelation> findTagRelationPagerList(Pager pager);

	/**
	 * 通过id获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	TagRelation findTagRelationById(String id);

	/**
	 * 通过id删除数据
	 * 
	 * @param id
	 */
	void deleteTagRelationById(String id);

	/**
	 * 修改数据
	 * 
	 * @param tagRelation
	 */
	void updateTagRelation(TagRelation tagRelation);

	/**
	 * 保存数据
	 * 
	 * @param tagRelation
	 */
	void saveTagRelation(TagRelation tagRelation);

	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	List<TagRelation> findTagRelationAllList();

}