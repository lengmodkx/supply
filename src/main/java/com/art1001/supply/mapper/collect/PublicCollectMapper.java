package com.art1001.supply.mapper.collect;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.PublicCollect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * collectmapper接口
 */
@Mapper
public interface PublicCollectMapper extends BaseMapper<PublicCollect> {

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
	 * @param publicCollect
	 */
	int updatePublicCollect(PublicCollect publicCollect);

	/**
	 * 保存collect数据
	 *
     * @param publicCollect
     */
	int savePublicCollect(PublicCollect publicCollect);

	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	List<PublicCollect> findPublicCollectAllList();

	/**
	 * 收藏mapper层
	 * 数据: 根据收藏id 删除一条记录
	 * @param publicCollectId 收藏记录的id
	 * @return 返回受影响行数
	 */
	@Delete("delete from prm_public_collect where id = #{publicCollectId}")
    int cancelCollect(String publicCollectId);

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
    void updateJson(@Param("id") String id, @Param("obj") Object obj, @Param("type") String type);
}