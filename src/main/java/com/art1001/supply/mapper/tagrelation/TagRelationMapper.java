package com.art1001.supply.mapper.tagrelation;

import java.util.List;
import com.art1001.supply.entity.tagrelation.TagRelation;
import com.art1001.supply.entity.base.Pager;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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


	void deleteTagRelationByPublicId(TagRelation tagRelation);

	List<TagRelation> findTagRelationByTagId(long tagId);

	/**
	 * 插入多条消息
	 * @param relations 标签关系实体集合
	 */
    void saveManyTagRelation(List<TagRelation> relations);

	/**
	 * 删除此项的标签关联信息
	 * @param publicId 项的id
	 * @param publicType 要删除的项是什么类型的
	 */
    void deleteItemTagRelation(@Param("publicId") String publicId, @Param("publicType") String publicType);

	/**
	 * 根据标签id 删除标签关系表的数据
	 * @param tagId 标签id
	 */
	@Delete("delete from prm_tag_relation where tag_id = #{tagId}")
	void deleteTagRelationByTagId(Long tagId);
}