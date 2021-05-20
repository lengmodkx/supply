package com.art1001.supply.service.template;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.template.Template;
import com.art1001.supply.entity.template.TemplateTask;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service接口
 */
public interface TemplateService extends IService<Template> {

	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Template> findTemplatePagerList(Pager pager);

	/**
	 * 通过templateId获取单条数据
	 * 
	 * @param templateId
	 * @return
	 */
	public Template findTemplateByTemplateId(String templateId);

	/**
	 * 通过templateId删除数据
	 * 
	 * @param templateId
	 */
	public void deleteTemplateByTemplateId(String templateId);

	/**
	 * 修改数据
	 * 
	 * @param template
	 */
	public void updateTemplate(Template template);

	/**
	 * 保存数据
	 * 
	 * @param template
	 */
	public Template saveTemplate(Template template);

	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	public List<Template> findTemplateAllList();

	@Transactional(rollbackFor = Exception.class)
	void addProject(String templateId,String projectName);


	TemplateTask getTemplateTaskList(String taskId);

	/**
	 * 模板添加子任务
	 * @param parentTaskId
	 * @param taskName
	 * @param relationId
	 * @return
	 */
    void insertChildTask(String parentTaskId, String taskName, String relationId);
}