package com.art1001.supply.service.template;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.template.Template;
import com.art1001.supply.entity.template.TemplateData;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 模板Service接口
 */
public interface TemplateDataService extends IService<TemplateData> {

	/**
	 * 查询分页模板数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<TemplateData> findTemplateDataPagerList(Pager pager);

	/**
	 * 通过id获取单条模板数据
	 * 
	 * @param id
	 * @return
	 */
	public TemplateData findTemplateDataById(String id);

	/**
	 * 通过id删除模板数据
	 * 
	 * @param id
	 */
	public void deleteTemplateDataById(String id);

	/**
	 * 修改模板数据
	 * 
	 * @param templateData
	 */
	public void updateTemplateData(TemplateData templateData);

	/**
	 * 保存模板数据
	 * 
	 * @param templateData
	 */
	public void saveTemplateData(TemplateData templateData);

	/**
	 * 获取所有模板数据
	 * 
	 * @return
	 */
	public List<TemplateData> findTemplateDataAllList();

	List<TemplateData> findByTemplateId(String templateId);

	List<TemplateData> findByParentId(String id);
	
}