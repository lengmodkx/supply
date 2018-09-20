package com.art1001.supply.mapper.template;

import java.util.List;
import com.art1001.supply.entity.template.TemplateData;
import com.art1001.supply.entity.base.Pager;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 模板mapper接口
 */
@Mapper
public interface TemplateDataMapper extends BaseMapper<TemplateData> {

	/**
	 * 查询分页模板数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<TemplateData> findTemplateDataPagerList(Pager pager);

	/**
	 * 通过id获取单条模板数据
	 * 
	 * @param id
	 * @return
	 */
	TemplateData findTemplateDataById(String id);

	/**
	 * 通过id删除模板数据
	 * 
	 * @param id
	 */
	void deleteTemplateDataById(String id);

	/**
	 * 修改模板数据
	 * 
	 * @param templateData
	 */
	void updateTemplateData(TemplateData templateData);

	/**
	 * 保存模板数据
	 * 
	 * @param templateData
	 */
	void saveTemplateData(TemplateData templateData);

	/**
	 * 获取所有模板数据
	 * 
	 * @return
	 */
	List<TemplateData> findTemplateDataAllList();


	List<TemplateData> findByTemplateId(String templateId);

	List<TemplateData> findByParentId(String id);

}