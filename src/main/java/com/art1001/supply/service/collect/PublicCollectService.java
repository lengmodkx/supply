package com.art1001.supply.service.collect;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.PublicCollect;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * collectService接口
 */
public interface PublicCollectService extends IService<PublicCollect> {

	/**
	 * 查询分页collect数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<PublicCollect> findPublicCollectPagerList(Pager pager);

	/**
	 * 通过id获取单条collect数据
	 * 
	 * @param id
	 * @return
	 */
	public PublicCollect findPublicCollectById(String id);

	/**
	 * 通过id删除collect数据
	 * 
	 * @param memberId 当前用户id
	 * @param PublicId 任务id
	 */
	public int deletePublicCollectById(String memberId,String PublicId);

	/**
	 * 修改collect数据
	 * 
	 * @param PublicCollect
	 */
	public void updatePublicCollect(PublicCollect PublicCollect);

	/**
	 * 保存collect数据
	 *
     * @param PublicCollect
     */
	public int savePublicCollect(PublicCollect PublicCollect);

	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	public List<PublicCollect> findPublicCollectAllList();

	/**
	 * 数据: 删除该条收藏记录
	 * 功能: 用户对当前任务取消收藏
	 * @param publicCollectId 该条收藏记录id
	 * @return 返回影响行数
	 */
	int cancelCollect(String publicCollectId);

	/**
	 * 收藏项 (文件,日程,分享)
	 * @param publicId 项id
	 * @param publicType 项的类型 (文件,日程,分享)
	 * @return
	 */
	void collectItem(String publicId, String publicType);

	/**
	 * 判断一下该用户是否收藏 当前信息
	 * @param publicId 信息id
	 * @return
	 */
	boolean isCollItem(String publicId);

	/**
	 * 根据用户取消收藏信息
	 * @param publicId 信息id
	 * @return
	 */
	int cancelCollectByUser(String publicId);

	/**
	 * 删除关于此项的所有收藏记录
	 */
	void deleteCollectByItemId(String publicId);

	/**
	 * 删除关于多个此项的所有收藏记录
	 */
	void deleteManyCollectByItemId(List<String> publicId);

	/**
	 * 更新收藏表的 json 数据信息
	 * @param id 信息id
	 * @param obj 要更新的字段信息
	 * @param type 要更新的信息类型
	 */
    void updateJson(String id, Object obj, String type);

	/**
	 * 根据类型获取该用户的收藏信息
	 * @param collectType 收藏类型
	 * @return 收藏数据信息
	 */
	List<PublicCollect> getByType(String collectType);
}