package com.art1001.supply.service.project.impl;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.mapper.project.ProjectMemberMapper;
import com.art1001.supply.service.project.ProjectMemberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * projectMemberServiceImpl
 */
@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

	/** projectMemberMapper接口*/
	@Resource
	private ProjectMemberMapper projectMemberMapper;
	
	/**
	 * 查询分页projectMember数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<ProjectMember> findProjectMemberPagerList(Pager pager){
		return projectMemberMapper.findProjectMemberPagerList(pager);
	}

	/**
	 * 通过id获取单条projectMember数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public ProjectMember findProjectMemberById(String id){
		return projectMemberMapper.findProjectMemberById(id);
	}

	/**
	 * 通过id删除projectMember数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteProjectMemberById(String id){
		projectMemberMapper.deleteProjectMemberById(id);
	}

	/**
	 * 修改projectMember数据
	 * 
	 * @param projectMember
	 */
	@Override
	public void updateProjectMember(ProjectMember projectMember){
		projectMemberMapper.updateProjectMember(projectMember);
	}
	/**
	 * 保存projectMember数据
	 * 
	 * @param projectMember
	 */
	@Override
	public void saveProjectMember(ProjectMember projectMember){
		projectMemberMapper.saveProjectMember(projectMember);
	}
	/**
	 * 获取所有projectMember数据
	 * 
	 * @return
	 */
	@Override
	public List<ProjectMember> findProjectMemberAllList(){
		return projectMemberMapper.findProjectMemberAllList();
	}

	@Override
	public List<Project> findProjectByMemberId(String memberId,Integer projectDel) {
		return projectMemberMapper.findProjectByMemberId(memberId,projectDel);
	}

}