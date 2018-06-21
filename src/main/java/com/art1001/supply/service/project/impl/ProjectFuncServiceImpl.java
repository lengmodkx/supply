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
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<ProjectFunc> findProjectFuncPagerList(Pager pager){
		return projectFuncMapper.findProjectFuncPagerList(pager);
	}

	/**
	 * 通过pId获取单条project数据
	 * 
	 * @param pId
	 * @return
	 */
	@Override 
	public ProjectFunc findProjectFuncByPId(String pId){
		return projectFuncMapper.findProjectFuncByPId(pId);
	}

	/**
	 * 通过pId删除project数据
	 * 
	 * @param pId
	 */
	@Override
	public void deleteProjectFuncByPId(String pId){
		projectFuncMapper.deleteProjectFuncByPId(pId);
	}

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
	 * @param projectFunc
	 */
	@Override
	public void saveProjectFunc(ProjectFunc projectFunc){
		projectFuncMapper.saveProjectFunc(projectFunc);
	}
	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	@Override
	public List<ProjectFunc> findProjectFuncAllList(){
		return projectFuncMapper.findProjectFuncAllList();
	}

	@Override
	public List<ProjectFunc> findProjectFuncList(String projectId) {
		return projectFuncMapper.findProjectFuncList(projectId);
	}

}