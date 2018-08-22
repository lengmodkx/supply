package com.art1001.supply.mapper.tag;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.tag.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

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

	/**
	 * 保存多条tag
	 * @param newTagList 多个tag信息i
	 */
	void saveMany(List<Tag> newTagList);

	List<Tag> findByPublicId(@Param("publicId")String publicId,@Param("publicType")String publicType);

	/**
	 * 查询出在该项目回收站中的标签
	 * @param projectId 项目id
	 * @return
	 */
    List<Tag> findRecycleBin(String projectId);

	/**
	 * 将标签移入回收站
	 * @param tagId 标签id
	 */
	@Update("update prm_tag set is_del = 1,update_time = #{currTime} where tag_id = #{tagId}")
	void moveToRecycleBin(@Param("tagId") String tagId, @Param("currTime") long currTime);

	/**
	 * 恢复标签
	 * @param tagId 标签id
	 */
	@Update("update prm_tag set is_del = 0 where tag_id = #{tagId}")
	void recoveryTag(String tagId);
}