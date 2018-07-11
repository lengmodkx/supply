package com.art1001.supply.service.binding;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.binding.Binding;

/**
 * bindingService接口
 */
public interface BindingService {

	/**
	 * 查询分页binding数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Binding> findBindingPagerList(Pager pager);

	/**
	 * 通过id获取单条binding数据
	 * 
	 * @param id
	 * @return
	 */
	public Binding findBindingById(String id);

	/**
	 * 通过id删除binding数据
	 * 
	 * @param id
	 */
	public void deleteBindingById(String id);

	/**
	 * 修改binding数据
	 * 
	 * @param binding
	 */
	public void updateBinding(Binding binding);

	/**
	 * 保存binding数据
	 * 
	 * @param binding
	 */
	public void saveBinding(Binding binding);

	/**
	 * 获取所有binding数据
	 * 
	 * @return
	 */
	public List<Binding> findBindingAllList();
	
}