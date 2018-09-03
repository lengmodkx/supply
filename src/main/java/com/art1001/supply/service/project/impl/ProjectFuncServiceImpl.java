package com.art1001.supply.service.project.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.project.ProjectFunc;
import com.art1001.supply.mapper.project.ProjectFuncMapper;
import com.art1001.supply.service.project.ProjectFuncService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * projectServiceImpl
 */
@Service
public class ProjectFuncServiceImpl implements ProjectFuncService {

	/** projectMapper接口*/
	@Resource
	private ProjectFuncMapper projectFuncMapper;

	/**
	 * 修改project数据
	 * 
	 * @param projectFunc
	 */
	@Override
	public void updateProjectFunc(ProjectFunc projectFunc){
		projectFuncMapper.updateProjectFunc(projectFunc);
	}
	/**
	 * 保存project数据
	 * 
	 * @param appName
	 */
	@Override
	public void saveProjectFunc(List<String> appName,String projectId){
		projectFuncMapper.saveProjectFunc(appName,projectId);
	}

	@Override
	public List<ProjectFunc> findProjectFuncList(String projectId) {
		return projectFuncMapper.findProjectFuncList(projectId);
	}

}