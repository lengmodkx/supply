package com.art1001.supply.service.collect;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.collect.PublicCollectVO;
import com.art1001.supply.entity.share.Share;
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
	 * 数据: 根据用户id查询该用户的收藏
	 * 功能: 实现查询我的收藏功能
	 * @param memberId 用户id
	 * @param type 需要查询的收藏的类型(任务,分享,日程,文件)
	 * @return 收藏实体集合
	 */
	List<PublicCollect> findMyCollect(String memberId,String type);

	/**
	 * 数据: 删除该条收藏记录
	 * 功能: 用户对当前任务取消收藏
	 * @param publicCollectId 该条收藏记录id
	 * @return 返回影响行数
	 */
	int cancelCollect(String publicCollectId);

	/**
	 * 根据类型查询出收藏的数据
	 * @param memberId 当前用户id
	 * @param type 收藏的类型 (任务,文件,日程,分享)
	 * @return 收藏实体信息的集合
	 */
	List<PublicCollectVO> listMyCollect(String memberId, String type);

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
}