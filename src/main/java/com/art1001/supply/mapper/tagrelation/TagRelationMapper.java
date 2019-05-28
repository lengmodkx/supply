package com.art1001.supply.mapper.tagrelation;

import com.art1001.supply.entity.tag.TagRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * mapper接口
 */
@Mapper
public interface TagRelationMapper extends BaseMapper<TagRelation> {
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

	void deleteByPublicId(TagRelation tagRelation);

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

	/**
	 * 删除多个项的标签关联信息
	 * @param publicId 项的id
	 */
    void deleteManyItemTagRelation(List<String> publicId);


    /*
    * 查询是否有重复关联标签
    * */
	int findCountById(TagRelation tr);
}