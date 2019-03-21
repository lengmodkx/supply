package com.art1001.supply.service.project.impl;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.mapper.project.ProjectMemberMapper;
import com.art1001.supply.service.project.ProjectMemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
		ProjectMember projectMember = projectMemberMapper.selectOne(new QueryWrapper<ProjectMember>().select("default_group").eq("project_id", projectId).eq("member_id", userId).ne("default_group", "0"));
		if(projectMember == null){
			throw new NullPointerException("该项目不存在!");
		}
		return projectMember.getDefaultGroup();
	}

	@Override
	public void updateDefaultGroup(String projectId, String userId, String groupId) {
		ProjectMember projectMember = new ProjectMember();
		projectMember.setProjectId(projectId);
		projectMember.setMemberId(userId);
		update(projectMember,new UpdateWrapper<ProjectMember>().set("default_group",groupId));
	}
}