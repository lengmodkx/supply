package com.art1001.supply.mapper.project;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}