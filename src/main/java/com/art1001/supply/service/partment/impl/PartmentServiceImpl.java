package com.art1001.supply.service.partment.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.mapper.partment.PartmentMapper;
import com.art1001.supply.service.partment.PartmentService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * partmentServiceImpl
 */
@Service
public class PartmentServiceImpl implements PartmentService {

	/** partmentMapper接口*/
	@Resource
	private PartmentMapper partmentMapper;
	
	/**
	 * 查询分页partment数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Partment> findPartmentPagerList(Pager pager){
		return partmentMapper.findPartmentPagerList(pager);
	}

	/**
	 * 通过partmentId获取单条partment数据
	 * 
	 * @param partmentId
	 * @return
	 */
	@Override 
	public Partment findPartmentByPartmentId(String partmentId){
		return partmentMapper.findPartmentByPartmentId(partmentId);
	}

	/**
	 * 通过partmentId删除partment数据
	 * 
	 * @param partmentId
	 */
	@Override
	public void deletePartmentByPartmentId(String partmentId){
		partmentMapper.deletePartmentByPartmentId(partmentId);
	}

	/**
	 * 修改partment数据
	 * 
	 * @param partment
	 */
	@Override
	public void updatePartment(Partment partment){
		partmentMapper.updatePartment(partment);
	}
	/**
	 * 保存partment数据
	 * 
	 * @param partment
	 */
	@Override
	public void savePartment(Partment partment){
		partmentMapper.savePartment(partment);
	}
	/**
	 * 获取所有partment数据
	 * 
	 * @return
	 */
	@Override
	public List<Partment> findPartmentAllList(){
		return partmentMapper.findPartmentAllList();
	}
	
}