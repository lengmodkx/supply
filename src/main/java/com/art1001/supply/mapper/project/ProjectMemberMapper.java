package com.art1001.supply.mapper.project;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
 * projectMembermapper接口
 */
@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {

	List<Project> findProjectByMemberId(@Param("memberId") String memberId, @Param("projectDel") Integer projectDel);

	/**
	 * 根据项目id 和 用户id 查询
	 */
	List<ProjectMember> findByProjectId(@Param("projectId") String projectId);

	/**
	 * 查询成员是否存在于项目中
	 * @param projectId 项目id
	 * @param id 用户id
	 * @return
	 */
	@Select("select count(0) from prm_project_member where project_id = #{projectId} and member_id = #{id}")
	int findMemberIsExist(@Param("projectId") String projectId, @Param("id") String id);

	/**
	 * 获取当前用户的星标项目
	 * @param userId 用户id
	 * @return 星标项目
	 */
    List<Project> getStarProject(@Param("userId") String userId);

	/**
	 * 获取当前用户的非星标项目
	 * @param userId 用户id
	 * @return 非星标项目
	 */
	List<Project> getNotStarProject(@Param("userId") String userId);

	/**
	 * 根据项目id集合查询出这些项目下的所有成员id
	 * @param projectIdList 项目id集合
	 * @return 返回项目成员id集合
	 */
    List<String> selectMemberIdListByProjectIdList(@Param("proIdList") Collection<String> projectIdList);

	/**
	 * 查询项目用户信息
	 * @param projectId 项目id
	 * @param keyWord 用户名或者名称
	 */
    List<UserEntity> selectProjectUserInfo(@Param("projectId") String projectId, @Param("keyWord") String keyWord);

    void updateAll(@Param("userId") String userId, @Param("id") String id);
}