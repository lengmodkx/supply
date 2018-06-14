package com.art1001.supply.service.partment;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.partment.Partment;


/**
 * partmentService接口
 */
public interface PartmentService {

	/**
	 * 查询分页partment数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Partment> findPartmentPagerList(Pager pager);

	/**
	 * 通过partmentId获取单条partment数据
	 * 
	 * @param partmentId
	 * @return
	 */
	public Partment findPartmentByPartmentId(String partmentId);

	/**
	 * 通过partmentId删除partment数据
	 * 
	 * @param partmentId
	 */
	public void deletePartmentByPartmentId(String partmentId);

	/**
	 * 修改partment数据
	 * 
	 * @param partment
	 */
	public void updatePartment(Partment partment);

	/**
	 * 保存partment数据
	 * 
	 * @param partment
	 */
	public void savePartment(Partment partment);

	/**
	 * 获取所有partment数据
	 * 
	 * @return
	 */
	public List<Partment> findPartmentAllList();
	
}