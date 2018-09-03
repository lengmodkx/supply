package com.art1001.supply.service.project;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.ProjectFunc;


/**
 * projectService接口
 */
public interface ProjectFuncService {

	/**
	 * 修改project数据
	 * 
	 * @param projectFunc
	 */
	void updateProjectFunc(ProjectFunc projectFunc);

	/**
	 * 批量保存项目应用插件
	 * 
	 * @param appName
	 */
	void saveProjectFunc(List<String> appName,String projectId);


	/**
	 * 根据项目id获取所有project 功能菜单数据
	 *
	 * @return
	 */
	List<ProjectFunc> findProjectFuncList(String projectId);
	
}