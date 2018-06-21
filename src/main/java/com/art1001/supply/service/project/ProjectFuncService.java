package com.art1001.supply.service.project;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.ProjectFunc;


/**
 * projectService接口
 */
public interface ProjectFuncService {

	/**
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<ProjectFunc> findProjectFuncPagerList(Pager pager);

	/**
	 * 通过pId获取单条project数据
	 * 
	 * @param pId
	 * @return
	 */
	public ProjectFunc findProjectFuncByPId(String pId);

	/**
	 * 通过pId删除project数据
	 * 
	 * @param pId
	 */
	public void deleteProjectFuncByPId(String pId);

	/**
	 * 修改project数据
	 * 
	 * @param projectFunc
	 */
	public void updateProjectFunc(ProjectFunc projectFunc);

	/**
	 * 保存project数据
	 * 
	 * @param projectFunc
	 */
	public void saveProjectFunc(ProjectFunc projectFunc);

	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	public List<ProjectFunc> findProjectFuncAllList();
	
}