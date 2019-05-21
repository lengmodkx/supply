package com.art1001.supply.service.project;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.user.UserEntity;
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

	/**
	 * 获取到模块在当前项目的的参与者信息与非参与者信息
	 * @param type 模块类型
	 * @param id 信息id
	 * @param projectId 所在项目id
	 * @return 该项目成员在当前模块信息中的参与者信息与非参与者信息
	 */
	List<UserEntity> getModelProjectMember(String type, String id, String projectId);

	/**
	 * 查询出一个项目中的所有成员id
	 * @param projectId 项目id
	 * @return 成员id集合
	 */
	List<String> getProjectAllMemberId(String projectId);

	/**
	 * 获取当前用户的星标项目
	 * @param userId 用户id
	 * @return 星标项目
	 */
	List<Project> getStarProject(String userId);

	/**
	 * 获取当前用户的非星标项目
	 * @param userId 用户id
	 * @return 非星标项目
	 */
	List<Project> getNotStarProject(String userId);
}