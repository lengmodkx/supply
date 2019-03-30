package com.art1001.supply.service.tag;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.exception.ServiceException;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * tagService接口
 */
public interface TagService extends IService<Tag> {

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
	public void updateTag(Tag tag) throws ServiceException;

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

	void removeTag(String publicId, String publicType, long tagId);

	/**
	 * 给(任务,文件,日程,分享) 添加标签
	 * @param tagId 标签id
	 * @param publicId (任务 ,文件, 日程, 分享) id
	 * @param publicType (任务,文件,日程,分享) 类型
	 * @return 是否绑定成功
	 */
	boolean addItemTag(long tagId, String publicId, String publicType);

	/**
	 * 保存多条tag
	 * @param newTagList 多个tag信息i
	 */
    void saveMany(List<Tag> newTagList);


	List<Tag> findByPublicId(@Param("publicId")String publicId, @Param("publicType")String publicType);

	/**
	 * 查询出在该项目回收站中的标签
	 * @param projectId 项目id
	 * @return
	 */
	List<RecycleBinVO> findRecycleBin(String projectId);

	/**
	 * 将标签移入回收站
	 * @param tagId 标签id
	 */
	void moveToRecycleBin(String tagId);

	/**
	 * 恢复标签
	 * @param tagId 标签id
	 */
	void recoveryTag(String tagId);


	List<Tag> classification(List<Tag> tagList);

	/**
	 * 查询出项目下的所有标签
	 * @param projectId 项目id
	 * @return
	 */
	List<Tag> findTagByProjectIdWithAllInfo(String projectId);
}