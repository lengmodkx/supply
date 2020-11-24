package com.art1001.supply.service.tagrelation;

import com.art1001.supply.entity.tag.TagRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
 * Service接口
 */
public interface TagRelationService extends IService<TagRelation> {

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
	 * 查询有无重复标签
	 *
	 * @param tagId
	 * @param publicType
	 * @param publicId
	 */
	 int findCountById(Long tagId, String publicType, String publicId);

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

	/**
	 * 删除标签和指定类型的绑定关系
	 * @param ids 绑定的信息id集合
	 * @param type 信息类型
	 * @return 是否成功
	 */
    boolean removeBatchByType(Collection ids, String type);


	/**
	 * 根据日程id  查询列表
	 * @param id 日程id
	 * @return list
	 */
	List<TagRelation> findTagRelationByScheduleId(String id);

	List<String> findTaskIds(List<Integer> tagIds);
}