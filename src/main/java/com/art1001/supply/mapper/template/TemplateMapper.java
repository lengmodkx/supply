package com.art1001.supply.mapper.template;

import java.util.List;
import com.art1001.supply.entity.template.Template;
import com.art1001.supply.entity.base.Pager;
import org.apache.ibatis.annotations.Mapper;

/**
 * mapper接口
 */
@Mapper
public interface TemplateMapper {

	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Template> findTemplatePagerList(Pager pager);

	/**
	 * 通过templateId获取单条数据
	 * 
	 * @param templateId
	 * @return
	 */
	Template findTemplateByTemplateId(String templateId);

	/**
	 * 通过templateId删除数据
	 * 
	 * @param templateId
	 */
	void deleteTemplateByTemplateId(String templateId);

	/**
	 * 修改数据
	 * 
	 * @param template
	 */
	void updateTemplate(Template template);

	/**
	 * 保存数据
	 * 
	 * @param template
	 */
	void saveTemplate(Template template);

	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	List<Template> findTemplateAllList();

}