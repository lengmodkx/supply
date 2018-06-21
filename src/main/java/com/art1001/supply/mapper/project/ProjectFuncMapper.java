package com.art1001.supply.mapper.project;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.ProjectFunc;
import org.apache.ibatis.annotations.Mapper;

/**
 * projectmapper接口
 */
@Mapper
public interface ProjectFuncMapper {

	/**
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<ProjectFunc> findProjectFuncPagerList(Pager pager);

	/**
	 * 通过pId获取单条project数据
	 * 
	 * @param pId
	 * @return
	 */
	ProjectFunc findProjectFuncByPId(String pId);

	/**
	 * 通过pId删除project数据
	 * 
	 * @param pId
	 */
	void deleteProjectFuncByPId(String pId);

	/**
	 * 修改project数据
	 * 
	 * @param projectFunc
	 */
	void updateProjectFunc(ProjectFunc projectFunc);

	/**
	 * 保存project数据
	 * 
	 * @param projectFunc
	 */
	void saveProjectFunc(ProjectFunc projectFunc);

	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	List<ProjectFunc> findProjectFuncAllList();

}