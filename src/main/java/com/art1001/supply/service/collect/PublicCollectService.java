package com.art1001.supply.service.collect;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.PublicCollect;
import org.apache.ibatis.annotations.Param;


/**
 * collectService接口
 */
public interface PublicCollectService {

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
	 * 查询当前用户有没有收藏任务
	 * @param memberId 当前登录用户id
	 * @param publicId 任务/日程/文件/分享的id
	 * @param collectType 收藏的类型 任务/日程/文件/分享
	 * @return
	 */
	int judgeCollectPublic(@Param("memberId") String memberId, @Param("publicId") String publicId, @Param("collectType") String collectType);

	/**
	 * 数据: 根据用户id查询该用户收藏的所有任务
	 * 功能: 实现查询我的收藏的任务 功能
	 * @param memberId 用户id
	 * @return 藏实体集合
	 */
	List<PublicCollect> findMyCollectTask(String memberId);

	/**
	 * 数据: 删除该条收藏记录
	 * 功能: 用户对当前任务取消收藏
	 * @param publicCollectId 该条收藏记录id
	 * @return 返回影响行数
	 */
	int cancelCollectTask(String publicCollectId);
}