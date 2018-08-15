package com.art1001.supply.service.tag;

import java.util.List;
import java.util.Map;

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
	Tag findById(Integer tagId);

	/**
	 * 通过tagId删除tag数据
	 * 
	 * @param tagId
	 */
	public void deleteTagByTagId(Long tagId);

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
	public Tag saveTag(Tag tag);

	/**
	 * 获取所有tag数据
	 * 
	 * @return
	 */
	public List<Tag> findTagAllList();

	/**
	 * 根据tag名称查询tag的数量  去重
	 */
	int findCountByTagName(String projectId, String tagName);

	/**
	 * 根据项目id查询tag列表
	 */
	List<Tag> findByProjectId(String projectId);

	/**
	 * 查询标签的关联项
	 */
	Map<String,Object> findByTag(Tag tag);

	/**
	 * 根据多个id查询标签
	 */
	List<Tag> findByIds(Integer[] idArr);

	/**
	 * 搜索标签
	 * @param tagName 标签名称
	 * @return 标签实体集合
	 */
    List<Tag> searchTag(String tagName);

	void removeTag(String publicId, String publicType, String tagId);

	/**
	 * 给(任务,文件,日程,分享) 添加标签
	 * @param tagId 标签id
	 * @param publicId (任务 ,文件, 日程, 分享) id
	 * @param publicType (任务,文件,日程,分享) 类型
	 */
	void addItemTag(String tagId, String publicId, String publicType);

	/**
	 * 保存多条tag
	 * @param newTagList 多个tag信息i
	 */
    void saveMany(List<Tag> newTagList);
}