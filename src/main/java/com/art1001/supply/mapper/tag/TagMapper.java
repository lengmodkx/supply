package com.art1001.supply.mapper.tag;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.tag.Tag;
import org.apache.ibatis.annotations.Mapper;

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
	Tag findTagByTagId(String tagId);

	/**
	 * 通过tagId删除tag数据
	 * 
	 * @param tagId
	 */
	void deleteTagByTagId(String tagId);

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
	int saveTag(Tag tag);

	/**
	 * 获取所有tag数据
	 * 
	 * @return
	 */
	List<Tag> findTagAllList();

}