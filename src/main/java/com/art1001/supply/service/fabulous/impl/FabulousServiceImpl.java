package com.art1001.supply.service.fabulous.impl;

import com.art1001.supply.entity.fabulous.Fabulous;
import com.art1001.supply.mapper.fabulous.FabulousMapper;
import com.art1001.supply.service.fabulous.FabulousService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 得赞ServiceImpl
 */
@Service
public class FabulousServiceImpl extends ServiceImpl<FabulousMapper,Fabulous> implements FabulousService {

	/** 得赞Mapper接口*/
	@Resource
	private FabulousMapper fabulousMapper;

	/**
	 * @param publicId 信息id
	 * 删除某个信息的所有赞
	 */
	@Override
	public void deleteFabulousByInfoId(String publicId) {
		fabulousMapper.deleteFabulousByInfoId(publicId);
	}

	/**
	 * @param publicId 信息id
	 * 删除多个信息的所有赞
	 */
	@Override
	public void deleteManyFabulousByInfoId(List<String> publicId) {
		fabulousMapper.deleteManyFabulousByInfoId(publicId);
	}
}