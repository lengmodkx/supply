package com.art1001.supply.service.tag;

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
	void updateTag(Tag tag) throws ServiceException;

	/**
	 * 保存tag数据
	 *
	 * @param tag
	 */
	Tag saveTag(Tag tag);

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
    List<Tag> searchTag(String tagName,String projectId);

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
	 * 创建标签的同时绑定到某个信息上
	 * @param tag 标签信息
	 * @param publicId 绑定信息的id
	 * @param publicType 绑定信息的类型(任务,文件,日程,分享)
	 * @return 是否创建并绑定成功
	 */
	Boolean addAndBind(Tag tag, String publicId, String publicType);

	/**
	 * 获取标签的绑定信息
	 * @param tagId 标签id
	 * @return 绑定信息
	 */
	Tag getTagBindInfo(Long tagId);

	/**
	 * 根据标签id检查标签存不存在
	 * @param tagId 标签id
	 * @return 是否存在
	 */
	Boolean checkIsExist(Long tagId);

	/**
	 * 根据项目id 查询出标签信息
	 * @param projectId 项目id
	 * @return 标签集合
	 */
	List<Tag> getByProjectId(String projectId);

	List<Tag> findTagByTaskId(String taskId);
}