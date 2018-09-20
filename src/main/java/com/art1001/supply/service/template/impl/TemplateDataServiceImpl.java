package com.art1001.supply.service.template.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.mapper.template.TemplateDataMapper;
import com.art1001.supply.service.template.TemplateDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.template.TemplateData;

/**
 * 模板ServiceImpl
 */
@Service
public class TemplateDataServiceImpl extends ServiceImpl<TemplateDataMapper,TemplateData> implements TemplateDataService {

	/** 模板Mapper接口*/
	@Resource
	private TemplateDataMapper templateDataMapper;
	
	/**
	 * 查询分页模板数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<TemplateData> findTemplateDataPagerList(Pager pager){
		return templateDataMapper.findTemplateDataPagerList(pager);
	}

	/**
	 * 通过id获取单条模板数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public TemplateData findTemplateDataById(String id){
		return templateDataMapper.findTemplateDataById(id);
	}

	/**
	 * 通过id删除模板数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteTemplateDataById(String id){
		templateDataMapper.deleteTemplateDataById(id);
	}

	/**
	 * 修改模板数据
	 * 
	 * @param templateData
	 */
	@Override
	public void updateTemplateData(TemplateData templateData){
		templateDataMapper.updateTemplateData(templateData);
	}
	/**
	 * 保存模板数据
	 * 
	 * @param templateData
	 */
	@Override
	public void saveTemplateData(TemplateData templateData){
		templateDataMapper.saveTemplateData(templateData);
	}
	/**
	 * 获取所有模板数据
	 * 
	 * @return
	 */
	@Override
	public List<TemplateData> findTemplateDataAllList(){
		return templateDataMapper.findTemplateDataAllList();
	}

	@Override
	public List<TemplateData> findByTemplateId(String templateId) {
		return templateDataMapper.findByTemplateId(templateId);
	}

	@Override
	public List<TemplateData> findByParentId(String id) {
		return templateDataMapper.findByParentId(id);
	}

}