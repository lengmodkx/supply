package com.art1001.supply.service.tag.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.mapper.tag.TagMapper;
import com.art1001.supply.service.tag.TagService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * tagServiceImpl
 */
@Service
public class TagServiceImpl implements TagService {

	/** tagMapper接口*/
	@Resource
	private TagMapper tagMapper;
	
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
	public Tag findTagByTagId(String tagId){
		return tagMapper.findTagByTagId(tagId);
	}

	/**
	 * 通过tagId删除tag数据
	 * 
	 * @param tagId
	 */
	@Override
	public void deleteTagByTagId(String tagId){
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
	 * @param tag
	 */
	@Override
	public void saveTag(Tag tag){
		tagMapper.saveTag(tag);
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
	
}