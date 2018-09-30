package com.art1001.supply.mapper.fabulous;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.fabulous.Fabulous;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 得赞mapper接口
 */
@Mapper
public interface FabulousMapper extends BaseMapper<Fabulous> {

	/**
	 * 查询分页得赞数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Fabulous> findFabulousPagerList(Pager pager);

	/**
	 * 通过fabulousId获取单条得赞数据
	 * 
	 * @param fabulousId
	 * @return
	 */
	Fabulous findFabulousByFabulousId(String fabulousId);

	/**
	 * 通过fabulousId删除得赞数据
	 * 
	 * @param fabulousId
	 */
	void deleteFabulousByFabulousId(String fabulousId);

	/**
	 * 修改得赞数据
	 * 
	 * @param fabulous
	 */
	void updateFabulous(Fabulous fabulous);

	/**
	 * 获取所有得赞数据
	 * 
	 * @return
	 */
	List<Fabulous> findFabulousAllList();

	/**
	 * 用户赞后添加至该关系表
	 * @param fabulous 的实体信息
	 * @return
	 */
	int addFabulous(Fabulous fabulous);

	/**
	 * 用户取消对当前的赞
	 * @param publicId 当前任务id
	 * @param memberId 当前用户id
	 * @return
	 */
	@Delete("delete from prm_fabulous where public_id = #{publicId} and member_id = #{memberId}")
	int cancelFabulous(@Param("publicId") String publicId,@Param("memberId") String memberId);

	/**
	 * @param publicId 信息id
	 * 删除某个信息的所有赞
	 */
	@Delete("delete from prm_fabulous where public_id = #{publicId}")
	void deleteFabulousByInfoId(String publicId);

	/**
	 * @param publicId 信息id
	 * 删除多个信息的所有赞
	 */
    void deleteManyFabulousByInfoId(List<String> publicId);
}