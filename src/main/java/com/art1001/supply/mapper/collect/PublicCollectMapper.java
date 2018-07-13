package com.art1001.supply.mapper.collect;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.PublicCollect;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * collectmapper接口
 */
@Mapper
public interface PublicCollectMapper {

	/**
	 * 查询分页collect数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<PublicCollect> findPublicCollectPagerList(Pager pager);

	/**
	 * 通过id获取单条collect数据
	 * 
	 * @param id
	 * @return
	 */
	PublicCollect findPublicCollectById(String id);

	/**
	 * 通过id删除collect数据
     */
	int deletePublicCollectById(String id);

	/**
	 * 修改collect数据
	 * 
	 * @param PublicCollect
	 */
	int updatePublicCollect(PublicCollect PublicCollect);

	/**
	 * 保存collect数据
	 *
     * @param PublicCollect
     */
	int savePublicCollect(PublicCollect PublicCollect);

	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	List<PublicCollect> findPublicCollectAllList();

	/**
	 * 判断当前用户有没有收藏该任务
	 * @return
	 */
	int judgeCollectPublic(@Param("memberId") String memberId,@Param("publicId") String publicId,@Param("collectType") String collectType);

	/**
	 * 收藏mapper层
	 * 数据: 查询出某个用户收藏的所用任务
	 * @param memberId 用户id
	 * @return 返回收藏集合
	 */
	List<PublicCollect> findMyCollectTask(String memberId);

	/**
	 * 收藏mapper层
	 * 数据: 根据收藏id 删除一条记录
	 * @param publicCollectId 收藏记录的id
	 * @return 返回受影响行数
	 */
	@Delete("delete from prm_public_collect where id = #{publicCollectId}")
    int cancelCollectTask(String publicCollectId);
}