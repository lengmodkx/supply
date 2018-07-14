package com.art1001.supply.service.binding.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.annotation.Resource;

import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.binding.BindingMapper;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.util.IdGen;
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
		binding.setId(IdGen.uuid());
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

	/**
	 * 功能: 查询出该目标关联的所有信息
	 * 数据处理: 关联信息分为四种 (任务,分享,日程,文件) 需要分成四个集合然后按照关联时间排序 再放入统一的数组
	 * @param  publicId 目标id
	 * @return 返回关联数据
	 */
	@Override
	public List<Binding> listBindingInfoByPublicId(String publicId) {
		List<Binding> list = bindingMapper.listBindingInfoByPublicId(publicId);
		return list;
	}
}