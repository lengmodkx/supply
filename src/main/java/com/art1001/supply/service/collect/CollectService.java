package com.art1001.supply.service.collect;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.Collect;


/**
 * collectService接口
 */
public interface CollectService {

	/**
	 * 查询分页collect数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Collect> findCollectPagerList(Pager pager);

	/**
	 * 通过id获取单条collect数据
	 * 
	 * @param id
	 * @return
	 */
	public Collect findCollectById(String id);

	/**
	 * 通过id删除collect数据
	 * 
	 * @param id
	 */
	public void deleteCollectById(String id);

	/**
	 * 修改collect数据
	 * 
	 * @param Collect
	 */
	public void updateCollect(Collect Collect);

	/**
	 * 保存collect数据
	 * 
	 * @param Collect
	 */
	public void saveCollect(Collect Collect);

	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	public List<Collect> findCollectAllList();
	
}