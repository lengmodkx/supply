package com.art1001.supply.service.binding;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.binding.BindingVo;

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
	 * @param id 当前绑定信息的id
	 * @param bindingId 被绑定信息的id
	 */
	public void deleteBindingById(String id, String bindingId);

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

	/**
	 * 查询该目标的关联
	 * 数据: 查询出目标的关联的所有数据
	 * 功能: 页面上看到任务 或者 日程 详情页的关联信息
	 * @param  publicId 目标id
	 * @return 返回关联的集合
	 */
	BindingVo listBindingInfoByPublicId(String publicId);

	/**
	 * 查询要关联的信息  在库中存不存在
	 * @param publicId 关联的信息id
	 * @param bindId 被关联的信息id数组
	 * @return 库中的记录行数
	 */
	String[] getBindingRecord(String publicId, String[] bindId);
}