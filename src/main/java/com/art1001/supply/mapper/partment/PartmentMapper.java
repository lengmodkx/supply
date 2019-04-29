package com.art1001.supply.mapper.partment;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.partment.Partment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * partmentmapper接口
 */
@Mapper
public interface PartmentMapper extends BaseMapper<Partment> {

	/**
	 * 查询分页partment数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Partment> findPartmentPagerList(Pager pager);

	/**
	 * 通过partmentId获取单条partment数据
	 * 
	 * @param partmentId
	 * @return
	 */
	Partment findPartmentByPartmentId(String partmentId);

	/**
	 * 通过partmentId删除partment数据
	 * 
	 * @param partmentId
	 */
	void deletePartmentByPartmentId(String partmentId);

	/**
	 * 修改partment数据
	 * 
	 * @param partment
	 */
	void updatePartment(Partment partment);

	/**
	 * 保存partment数据
	 * 
	 * @param partment
	 */
	void savePartment(Partment partment);

	/**
	 * 获取所有partment数据
	 * 
	 * @return
	 */
	List<Partment> findPartmentAllList(Partment partment);

	int findMaxOrder(String orgId);

}