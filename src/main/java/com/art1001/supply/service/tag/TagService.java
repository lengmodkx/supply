package com.art1001.supply.service.tag;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.tag.Tag;


/**
 * tagService接口
 */
public interface TagService {

	/**
	 * 查询分页tag数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Tag> findTagPagerList(Pager pager);

	/**
	 * 通过tagId获取单条tag数据
	 * 
	 * @param tagId
	 * @return
	 */
	public Tag findTagByTagId(String tagId);

	/**
	 * 通过tagId删除tag数据
	 * 
	 * @param tagId
	 */
	public void deleteTagByTagId(String tagId);

	/**
	 * 修改tag数据
	 * 
	 * @param tag
	 */
	public void updateTag(Tag tag);

	/**
	 * 保存tag数据
	 * 
	 * @param tag
	 */
	public void saveTag(Tag tag);

	/**
	 * 获取所有tag数据
	 * 
	 * @return
	 */
	public List<Tag> findTagAllList();
	
}