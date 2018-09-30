package com.art1001.supply.service.project.impl;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.mapper.project.ProjectMemberMapper;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * projectMemberServiceImpl
 */
@Service
public class ProjectMemberServiceImpl extends ServiceImpl<ProjectMemberMapper,ProjectMember> implements ProjectMemberService {

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
		projectMember.setId(IdGen.uuid());
		projectMember.setCreateTime(System.currentTimeMillis());
		projectMember.setUpdateTime(System.currentTimeMillis());
		projectMember.setMemberLabel(1);
		projectMemberMapper.saveProjectMember(projectMember);
	}
	/**
	 * 获取所有projectMember数据
	 * 
	 * @return
	 */
	@Override
	public List<ProjectMember> findProjectMemberAllList(ProjectMember projectMember){
		return projectMemberMapper.findProjectMemberAllList(projectMember);
	}

	@Override
	public List<Project> findProjectByMemberId(String memberId,Integer projectDel) {
		return projectMemberMapper.findProjectByMemberId(memberId,projectDel);
	}

	@Override
	public List<ProjectMember> findByProjectId(String projectId) {
		return projectMemberMapper.findByProjectId(projectId);
	}

	/**
	 * 查询成员是否存在于项目中
	 * @param projectId 项目id
	 * @param id 用户id
	 * @return
	 */
	@Override
	public int findMemberIsExist(String projectId, String id) {
		return projectMemberMapper.findMemberIsExist(projectId,id);
	}

	/**
	 * 根据用户查询出 用户在该项目中的默认分组id
	 * @param projectId 项目id
	 * @param userId 用户id
	 * @return
	 */
	@Override
	public String findDefaultGroup(String projectId, String userId) {
		return projectMemberMapper.selectOne(new QueryWrapper<ProjectMember>().select("default_group").eq("project_id",projectId).eq("member_id",userId).ne("default_group","0")).getDefaultGroup();
	}
}