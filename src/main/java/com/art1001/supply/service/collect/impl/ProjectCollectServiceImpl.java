package com.art1001.supply.service.collect.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.collect.ProjectCollect;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.mapper.collect.ProjectCollectMapper;
import com.art1001.supply.service.collect.ProjectCollectService;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * collectServiceImpl
 */
@Service
public class ProjectCollectServiceImpl implements ProjectCollectService {

	/** collectMapper接口*/
	@Resource
	private ProjectCollectMapper projectCollectMapper;
	
	/**
	 * 查询分页collect数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<ProjectCollect> findProjectCollectPagerList(Pager pager){
		return projectCollectMapper.findProjectCollectPagerList(pager);
	}

	/**
	 * 通过id获取单条collect数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public ProjectCollect findProjectCollectById(String id){
		return projectCollectMapper.findProjectCollectById(id);
	}

	/**
	 * 通过id删除collect数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteProjectCollectById(String id){
		projectCollectMapper.deleteProjectCollectById(id);
	}

	/**
	 * 修改collect数据
	 * 
	 * @param projectCollect
	 */
	@Override
	public void updateProjectCollect(ProjectCollect projectCollect){
		projectCollectMapper.updateProjectCollect(projectCollect);
	}
	/**
	 * 保存collect数据
	 * 
	 * @param projectCollect
	 */
	@Override
	public void saveProjectCollect(ProjectCollect projectCollect){
	    projectCollect.setId(IdGen.uuid());
		projectCollectMapper.saveProjectCollect(projectCollect);
	}
	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	@Override
	public List<ProjectCollect> findProjectCollectAllList(){
		return projectCollectMapper.findProjectCollectAllList();
	}

	@Override
	public List<Project> findProjectByMemberId(String memberId) {
		return projectCollectMapper.findProjectByMemberId(memberId);
	}

	@Override
	public int findCollectByProjectId(String projectId) {
		return projectCollectMapper.findCollectByProjectId(projectId);
	}

	@Override
	public void deleteCollectByProjectId(String projectId) {
		projectCollectMapper.deleteCollectByProjectId(projectId);
	}

}