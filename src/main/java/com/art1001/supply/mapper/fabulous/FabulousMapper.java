package com.art1001.supply.mapper.fabulous;

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