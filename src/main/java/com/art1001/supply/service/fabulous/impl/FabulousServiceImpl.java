package com.art1001.supply.service.fabulous.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.mapper.fabulous.FabulousMapper;
import com.art1001.supply.service.fabulous.FabulousService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.fabulous.Fabulous;

/**
 * 得赞ServiceImpl
 */
@Service
public class FabulousServiceImpl implements FabulousService {

	/** 得赞Mapper接口*/
	@Resource
	private FabulousMapper fabulousMapper;
	
	/**
	 * 查询分页得赞数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Fabulous> findFabulousPagerList(Pager pager){
		return fabulousMapper.findFabulousPagerList(pager);
	}

	/**
	 * 通过fabulousId获取单条得赞数据
	 * 
	 * @param fabulousId
	 * @return
	 */
	@Override 
	public Fabulous findFabulousByFabulousId(String fabulousId){
		return fabulousMapper.findFabulousByFabulousId(fabulousId);
	}

	/**
	 * 通过fabulousId删除得赞数据
	 * 
	 * @param fabulousId
	 */
	@Override
	public void deleteFabulousByFabulousId(String fabulousId){
		fabulousMapper.deleteFabulousByFabulousId(fabulousId);
	}

	/**
	 * 修改得赞数据
	 * 
	 * @param fabulous
	 */
	@Override
	public void updateFabulous(Fabulous fabulous){
		fabulousMapper.updateFabulous(fabulous);
	}

	/**
	 * 获取所有得赞数据
	 * 
	 * @return
	 */
	@Override
	public List<Fabulous> findFabulousAllList(){
		return fabulousMapper.findFabulousAllList();
	}

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