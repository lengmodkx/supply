package com.art1001.supply.mapper.tag;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.tag.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * tagmapper接口
 */
@Mapper
public interface TagMapper {

	/**
	 * 查询分页tag数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Tag> findTagPagerList(Pager pager);

	/**
	 * 通过tagId获取单条tag数据
	 * 
	 * @param tagId
	 * @return
	 */
	Tag findById(Integer tagId);

	/**
	 * 通过tagId删除tag数据
	 * 
	 * @param tagId
	 */
	void deleteTagByTagId(Long tagId);

	/**
	 * 修改tag数据
	 * 
	 * @param tag
	 */
	void updateTag(Tag tag);

	/**
	 * 保存tag数据
	 * 
	 * @param tag
	 */
	Long saveTag(Tag tag);

	/**
	 * 获取所有tag数据
	 * 
	 * @return
	 */
	List<Tag> findTagAllList();

	/**
	 * 根据tag名称查询tag的数量  去重
	 */
	int findCountByTagName(@Param("projectId") String projectId, @Param("tagName") String tagName);

	/**
	 * 根据项目id查询tag列表
	 */
	List<Tag> findByProjectId(@Param("projectId") String projectId);

	/**
	 * 根据多个id查询标签
	 */
    List<Tag> findByIds(Integer[] idArr);

	/**
	 * 模糊查询标签
	 * @param tagName 标签名称
	 * @return 标签集合数据
	 */
	List<Tag> searchTag(String tagName);


	List<Tag> findTagByTagIds(@Param("tagIds") String tagIds);
}