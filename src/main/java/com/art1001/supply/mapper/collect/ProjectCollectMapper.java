package com.art1001.supply.mapper.collect;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.ProjectCollect;
import com.art1001.supply.entity.project.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * collectmapper接口
 */
@Mapper
public interface ProjectCollectMapper {

	/**
	 * 查询分页collect数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<ProjectCollect> findProjectCollectPagerList(Pager pager);

	/**
	 * 通过id获取单条collect数据
	 * 
	 * @param id
	 * @return
	 */
	ProjectCollect findProjectCollectById(String id);

	/**
	 * 通过id删除collect数据
	 * 
	 * @param id
	 */
	void deleteProjectCollectById(String id);

	/**
	 * 修改collect数据
	 * 
	 * @param projectCollect
	 */
	void updateProjectCollect(ProjectCollect projectCollect);

	/**
	 * 保存collect数据
	 * 
	 * @param projectCollect
	 */
	void saveProjectCollect(ProjectCollect projectCollect);

	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	List<ProjectCollect> findProjectCollectAllList();


	List<Project> findProjectByMemberId(String memberId);

	int findCollectByProjectId(String projectId,String memberId);

	void deleteCollectByProjectId(String projectId);


}