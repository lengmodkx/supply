package com.art1001.supply.mapper.collect;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.Collect;
import org.apache.ibatis.annotations.Mapper;

/**
 * collectMapper接口
 */
@Mapper
public interface CollectMapper {

	/**
	 * 查询分页collect数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Collect> findCollectPagerList(Pager pager);

	/**
	 * 通过id获取单条collect数据
	 * 
	 * @param id
	 * @return
	 */
	Collect findCollectById(String id);

	/**
	 * 通过id删除collect数据
	 * 
	 * @param id
	 */
	void deleteCollectById(String id);

	/**
	 * 修改collect数据
	 * 
	 * @param Collect
	 */
	void updateCollect(Collect Collect);

	/**
	 * 保存collect数据
	 * 
	 * @param Collect
	 */
	void saveCollect(Collect Collect);

	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	List<Collect> findCollectAllList();

}