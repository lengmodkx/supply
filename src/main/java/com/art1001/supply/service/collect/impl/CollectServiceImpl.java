package com.art1001.supply.service.collect.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.collect.Collect;
import com.art1001.supply.mapper.collect.CollectMapper;
import com.art1001.supply.service.collect.CollectService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * collectServiceImpl
 */
@Service
public class CollectServiceImpl implements CollectService {

	/** collectMapper接口*/
	@Resource
	private CollectMapper collectMapper;
	
	/**
	 * 查询分页collect数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Collect> findCollectPagerList(Pager pager){
		return collectMapper.findCollectPagerList(pager);
	}

	/**
	 * 通过id获取单条collect数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public Collect findCollectById(String id){
		return collectMapper.findCollectById(id);
	}

	/**
	 * 通过id删除collect数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteCollectById(String id){
		collectMapper.deleteCollectById(id);
	}

	/**
	 * 修改collect数据
	 * 
	 * @param Collect
	 */
	@Override
	public void updateCollect(Collect Collect){
		collectMapper.updateCollect(Collect);
	}
	/**
	 * 保存collect数据
	 * 
	 * @param Collect
	 */
	@Override
	public void saveCollect(Collect Collect){
		collectMapper.saveCollect(Collect);
	}
	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	@Override
	public List<Collect> findCollectAllList(){
		return collectMapper.findCollectAllList();
	}
	
}