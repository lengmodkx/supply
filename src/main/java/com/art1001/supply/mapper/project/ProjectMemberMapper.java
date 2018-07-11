package com.art1001.supply.mapper.project;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * projectMembermapper接口
 */
@Mapper
public interface ProjectMemberMapper {

	/**
	 * 查询分页projectMember数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<ProjectMember> findProjectMemberPagerList(Pager pager);

	/**
	 * 通过id获取单条projectMember数据
	 * 
	 * @param id
	 * @return
	 */
	ProjectMember findProjectMemberById(String id);

	/**
	 * 通过id删除projectMember数据
	 * 
	 * @param id
	 */
	void deleteProjectMemberById(String id);

	/**
	 * 修改projectMember数据
	 * 
	 * @param projectMember
	 */
	void updateProjectMember(ProjectMember projectMember);

	/**
	 * 保存projectMember数据
	 * 
	 * @param projectMember
	 */
	void saveProjectMember(ProjectMember projectMember);

	/**
	 * 获取所有projectMember数据
	 * 
	 * @return
	 */
	List<ProjectMember> findProjectMemberAllList(ProjectMember projectMember);

	List<Project> findProjectByMemberId(@Param("memberId") String memberId, @Param("projectDel") Integer projectDel);

	/**
	 * 根据项目id 和 用户id 查询
	 */
	List<ProjectMember> findByProjectId(@Param("projectId") String projectId);

}