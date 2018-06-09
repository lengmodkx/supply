package com.art1001.supply.service.project.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.mapper.project.ProjectMapper;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;

/**
 * projectServiceImpl
 */
@Service
public class ProjectServiceImpl implements ProjectService {

	/** projectMapper接口*/
	@Resource
	private ProjectMapper projectMapper;
	
	/**
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Project> findProjectPagerList(Pager pager){
		return projectMapper.findProjectPagerList(pager);
	}

	/**
	 * 通过projectId获取单条project数据
	 * 
	 * @param projectId
	 * @return
	 */
	@Override 
	public Project findProjectByProjectId(String projectId){
		return projectMapper.findProjectByProjectId(projectId);
	}

	/**
	 * 通过projectId删除project数据
	 * 
	 * @param projectId
	 */
	@Override
	public void deleteProjectByProjectId(String projectId){
		projectMapper.deleteProjectByProjectId(projectId);
	}

	/**
	 * 修改project数据
	 * 
	 * @param project
	 */
	@Override
	public void updateProject(Project project){
		projectMapper.updateProject(project);
	}
	/**
	 * 保存project数据
	 * 
	 * @param project
	 */
	@Override
	public void saveProject(Project project){
		project.setProjectId(IdGen.uuid());
		projectMapper.saveProject(project);
	}
	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	@Override
	public List<Project> findProjectAllList(){
		return projectMapper.findProjectAllList();
	}

	/**
	 * 获取项目创建人的项目
	 *
	 * @return
	 */
	@Override
	public List<Project> findProjectByMemberId(String memberId) {
		return projectMapper.findProjectByMemberId(memberId);
	}


}