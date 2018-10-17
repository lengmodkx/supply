package com.art1001.supply.service.fabulous;

import com.art1001.supply.entity.fabulous.Fabulous;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 得赞Service接口
 */
public interface FabulousService extends IService<Fabulous> {

	/**
	 * @param publicId 信息id
	 * 删除某个信息的所有赞
	 */
	void deleteFabulousByInfoId(String publicId);

	/**
	 * @param publicId 信息id
	 * 删除多个信息的所有赞
	 */
	void deleteManyFabulousByInfoId(List<String> publicId);
	
}