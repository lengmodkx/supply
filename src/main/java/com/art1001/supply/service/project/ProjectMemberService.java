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

	/**
	 * 添加项目成员
	 * @author heShaoHua
	 * @describe 暂无
	 * @param projectId 项目id
	 * @param memberId 成员id
	 * @updateInfo 暂无
	 * @date 2019/6/19 15:29
	 * @return 是否成功
	 */
    Integer saveMember(String projectId, String memberId);

    /**
     * 获取到用当前所在的项目
	 * 如果没有默认项目则返回null
     * @author heShaoHua
     * @describe 暂无
     * @updateInfo 暂无
     * @date 2019/6/21 10:11
     * @return 项目id
     */
    String getUserCurrentProjectId();

    /**
     * 将用户当前所在项目更新为projectId的项目
	 * 如果没有此用户和projectId的对应关系则返回-1
	 * 如果projectId为空字符串或者null 则返回-1
	 * 如果该projectId的记录不存在则返回-1
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 要更新为所在项目的项目id
     * @updateInfo 暂无
     * @date 2019/6/21 10:18
     * @return 结果
     */
    Integer updateUserCurrentProject(String projectId);

    /**
     * 将指定的项目id设置为userId的当前所在项目,和updateUserCurrentProject() 不同的是该方法里没有没有复杂的业务处理
	 * 也就是说该方法会直接设置给定的projectId为current
	 * 比如用户之前没有任何项目,创建的第一个项目要设置为current则可以使用此方法 , 除此之外请谨慎使用
	 * 如果 projectId和userId任一为空则返回-1
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
	 * @param userId 用户id
     * @updateInfo 暂无
     * @date 2019/6/21 10:59
     * @return 结果
     */
    Integer updateTargetProjectCurrent(String projectId, String userId);

    /**
     * 查询用户和项目的对应关系是否存在
	 * 如果projectId为空则返回false
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 查询的项目id
     * @updateInfo 暂无
     * @date 2019/6/21 10:21
     * @return 是否存在
     */
    Boolean checkUserProjectBindIsExist(String projectId);

    /**
     * 获取出和当前用户有关的项目数
     * @author heShaoHua
     * @describe 暂无
     * @updateInfo 暂无
     * @date 2019/6/21 10:52
     * @return 项目数
     */
    Integer getUserProjectCount();

    /**
     * 获取到用户为该角色的所有用户id
	 * 如果proRoleId 为空则返回空集合
     * @author heShaoHua
     * @describe 暂无
     * @param proRoleId 角色id
     * @updateInfo 暂无
     * @date 2019/6/21 11:54
     * @return 用户id集合
     */
    List<String> getProRoleUsers(Integer proRoleId);

	/**
	 * 将用户更新为新的默认角色
	 * 如果roleId和projectId 任一为空则返回-1
	 * userIds为空则返回-1
	 * @author heShaoHua
	 * @describe 暂无
	 * @param roleId 新的默认角色id
	 * @param userIds 用户id集合
	 * @param projectId 项目id
	 * @updateInfo 暂无
	 * @date 2019/6/21 14:20
	 * @return 结果
	 */
	Integer updateUserToNewDefaultRole(List<String> userIds,Integer roleId, String projectId);
}