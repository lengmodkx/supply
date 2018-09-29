package com.art1001.supply.service.project;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * projectMemberService接口
 */
public interface ProjectMemberService extends IService<ProjectMember> {

	/**
	 * 查询分页projectMember数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<ProjectMember> findProjectMemberPagerList(Pager pager);

	/**
	 * 通过id获取单条projectMember数据
	 * 
	 * @param id
	 * @return
	 */
	public ProjectMember findProjectMemberById(String id);

	/**
	 * 通过id删除projectMember数据
	 * 
	 * @param id
	 */
	public void deleteProjectMemberById(String id);

	/**
	 * 修改projectMember数据
	 * 
	 * @param projectMember
	 */
	public void updateProjectMember(ProjectMember projectMember);

	/**
	 * 保存projectMember数据
	 * 
	 * @param projectMember
	 */
	public void saveProjectMember(ProjectMember projectMember);

	/**
	 * 获取所有projectMember数据
	 * 
	 * @return
	 */
	public List<ProjectMember> findProjectMemberAllList(ProjectMember projectMember);

	List<Project> findProjectByMemberId(String memberId,Integer projectDel);

	/**
	 * 根据项目id 和 用户id 查询
	 */
	List<ProjectMember> findByProjectId(String projectId);

	/**
	 * 查询成员是否存在于项目中
	 * @param projectId 项目id
	 * @param id 用户id
	 * @return
	 */
    int findMemberIsExist(String projectId, String id);

	/**
	 * 根据用户查询出 用户在该项目中的默认分组id
	 * @param projectId 项目id
	 * @param userId 用户id
	 * @return
	 */
	String findDefaultGroup(String projectId, String userId);
}