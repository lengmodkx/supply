package com.art1001.supply.mapper.collect;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.PublicCollect;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
	 * @param memberId
	 * @param publicId 收藏的
	 * @return
	 */
	int judgeCollectPublic(@Param("memberId") String memberId,@Param("publicId") String publicId,@Param("collectType") String collectType);

	/**
	 * 收藏mapper层
	 * 数据: 根据收藏id 删除一条记录
	 * @param publicCollectId 收藏记录的id
	 * @return 返回受影响行数
	 */
	@Delete("delete from prm_public_collect where id = #{publicCollectId}")
    int cancelCollect(String publicCollectId);

	/**
	 * 收藏mapper层
	 * 数据: 查询出某个用户收藏的所用任务
	 * @param memberId 用户id
	 * @param type 要查询的类型
	 * @return 返回收藏集合
	 */
    List<PublicCollect> findMyCollect(@Param("memberId") String memberId,@Param("type") String type);

	/**
	 * 查询收藏数据的接口
	 * 数据: 根据收藏类型  查询数据
	 * @param memberId 当前用户id
	 * @param type 收藏的类型 (任务,文件,日程,分享)
	 * @return 收藏实体信息的集合
	 */
	List<PublicCollect> listMyCollect(@Param("memberId") String memberId, @Param("type") String type);

	/**
	 * 判断一下该用户是否收藏 当前信息
	 *
	 * @param uId 当前登录用户id
	 * @param publicId 信息id
	 * @return
	 */
	@Select("select count(0) from prm_public_collect where public_id = #{publicId} and member_id = #{uId}")
    int isCollItem(@Param("publicId") String publicId, @Param("uId") String uId);

	/**
	 * 根据用户删除信息
	 * @param publicId 信息id
	 * @param userId 用户id
	 * @return
	 */
	@Delete("delete from prm_public_collect where member_id = #{userId} and public_id = #{publicId}")
	int cancelCollectByUser(@Param("publicId") String publicId, @Param("userId") String userId);

	/**
	 * 删除关于 此项的所有收藏记录
	 */
	@Delete("delete from prm_public_collect where public_id = #{publicId}")
    void deleteCollectByItemId(String publicId);
}