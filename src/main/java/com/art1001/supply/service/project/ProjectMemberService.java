package com.art1001.supply.service.project;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * projectMemberService接口
 */
public interface ProjectMemberService extends IService<ProjectMember> {


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

	/**
	 * 更新用户默认分组
	 * @param projectId
	 * @param userId
	 * @param groupId
	 */
	void updateDefaultGroup(String projectId, String userId,String groupId);
}