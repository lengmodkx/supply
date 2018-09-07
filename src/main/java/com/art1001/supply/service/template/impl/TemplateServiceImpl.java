package com.art1001.supply.service.template.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.mapper.template.TemplateMapper;
import com.art1001.supply.service.template.TemplateService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.template.Template;

/**
 * ServiceImpl
 */
@Service
public class TemplateServiceImpl implements TemplateService {

	/** Mapper接口*/
	@Resource
	private TemplateMapper templateMapper;
	
	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Template> findTemplatePagerList(Pager pager){
		return templateMapper.findTemplatePagerList(pager);
	}

	/**
	 * 通过templateId获取单条数据
	 * 
	 * @param templateId
	 * @return
	 */
	@Override 
	public Template findTemplateByTemplateId(String templateId){
		return templateMapper.findTemplateByTemplateId(templateId);
	}

	/**
	 * 通过templateId删除数据
	 * 
	 * @param templateId
	 */
	@Override
	public void deleteTemplateByTemplateId(String templateId){
		templateMapper.deleteTemplateByTemplateId(templateId);
	}

	/**
	 * 修改数据
	 * 
	 * @param template
	 */
	@Override
	public void updateTemplate(Template template){
		templateMapper.updateTemplate(template);
	}
	/**
	 * 保存数据
	 * 
	 * @param template
	 */
	@Override
	public void saveTemplate(Template template){
		templateMapper.saveTemplate(template);
	}
	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	@Override
	public List<Template> findTemplateAllList(){
		return templateMapper.findTemplateAllList();
	}
	
}