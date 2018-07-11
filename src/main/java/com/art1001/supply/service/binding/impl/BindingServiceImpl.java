package com.art1001.supply.service.binding.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.mapper.binding.BindingMapper;
import com.art1001.supply.service.binding.BindingService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.binding.Binding;

/**
 * bindingServiceImpl
 */
@Service
public class BindingServiceImpl implements BindingService {

	/** bindingMapper接口*/
	@Resource
	private BindingMapper bindingMapper;
	
	/**
	 * 查询分页binding数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Binding> findBindingPagerList(Pager pager){
		return bindingMapper.findBindingPagerList(pager);
	}

	/**
	 * 通过id获取单条binding数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public Binding findBindingById(String id){
		return bindingMapper.findBindingById(id);
	}

	/**
	 * 通过id删除binding数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteBindingById(String id){
		bindingMapper.deleteBindingById(id);
	}

	/**
	 * 修改binding数据
	 * 
	 * @param binding
	 */
	@Override
	public void updateBinding(Binding binding){
		bindingMapper.updateBinding(binding);
	}
	/**
	 * 保存binding数据
	 * 
	 * @param binding
	 */
	@Override
	public void saveBinding(Binding binding){
		bindingMapper.saveBinding(binding);
	}
	/**
	 * 获取所有binding数据
	 * 
	 * @return
	 */
	@Override
	public List<Binding> findBindingAllList(){
		return bindingMapper.findBindingAllList();
	}
	
}