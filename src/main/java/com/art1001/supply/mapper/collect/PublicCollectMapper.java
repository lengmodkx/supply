package com.art1001.supply.mapper.collect;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.PublicCollect;
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
}