package com.art1001.supply.mapper.project;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * projectmapper接口
 */
@Mapper
public interface ProjectMapper {

	/**
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Project> findProjectPagerList(Pager pager);

	/**
	 * 通过projectId获取单条project数据
	 * 
	 * @param projectId
	 * @return
	 */
	Project findProjectByProjectId(String projectId);

	/**
	 * 通过projectId删除project数据
	 * 
	 * @param projectId
	 */
	void deleteProjectByProjectId(String projectId);

	/**
	 * 修改project数据
	 * 
	 * @param project
	 */
	void updateProject(Project project);

	/**
	 * 保存project数据
	 * 
	 * @param project
	 */
	void saveProject(Project project);

	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	List<Project> findProjectAllList();


	/**
	 * 获取项目创建人的项目
	 *
	 * @return
	 */
	List<Project> findProjectByMemberId(String memberId);

	/**
	 * 查询出当前用户执行的任务信息和该任务所在的项目信息
	 * @param id 当前用户id
	 * @return
	 */
    List<Project> findProjectAndTaskByExecutorId(String id);

	/**
	 * 查询出当前用户所参与的任务的 任务信息 和 项目信息
	 * @param id 当前用户id
	 * @return
	 */
	List<Project> findProjectAndTaskByUserId(String id);

	/**
	 * 查询出当前用户所创建的任务的 任务信息 和 项目信息
	 * @param id 当前用户id
	 * @return
	 */
    List<Project> findProjectAndTaskByCreateMember(String id);
}