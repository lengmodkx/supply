package com.art1001.supply.service.collect;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.ProjectCollect;
import com.art1001.supply.entity.project.Project;


/**
 * collectService接口
 */
public interface ProjectCollectService {

	/**
	 * 查询分页collect数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<ProjectCollect> findProjectCollectPagerList(Pager pager);

	/**
	 * 通过id获取单条collect数据
	 * 
	 * @param id
	 * @return
	 */
	public ProjectCollect findProjectCollectById(String id);

	/**
	 * 通过id删除collect数据
	 * 
	 * @param id
	 */
	public void deleteProjectCollectById(String id);

	/**
	 * 修改collect数据
	 * 
	 * @param projectCollect
	 */
	public void updateProjectCollect(ProjectCollect projectCollect);

	/**
	 * 保存collect数据
	 * 
	 * @param projectCollect
	 */
	public void saveProjectCollect(ProjectCollect projectCollect);

	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	public List<ProjectCollect> findProjectCollectAllList();

	List<Project> findProjectByMemberId(String memberId);
	
}