package com.art1001.supply.service.project;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.Project;


/**
 * projectService接口
 */
public interface ProjectService {

	/**
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Project> findProjectPagerList(Pager pager);

	/**
	 * 通过projectId获取单条project数据
	 * 
	 * @param projectId
	 * @return
	 */
	public Project findProjectByProjectId(String projectId);

	/**
	 * 通过projectId删除project数据
	 * 
	 * @param projectId
	 */
	public void deleteProjectByProjectId(String projectId);

	/**
	 * 修改project数据
	 * 
	 * @param project
	 */
	public void updateProject(Project project);

	/**
	 * 保存project数据
	 * 
	 * @param project
	 */
	public void saveProject(Project project);

	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	public List<Project> findProjectAllList();
	
}