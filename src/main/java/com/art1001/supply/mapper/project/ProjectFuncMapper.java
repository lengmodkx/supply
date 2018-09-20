package com.art1001.supply.mapper.project;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.ProjectFunc;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * projectmapper接口
 */
@Mapper
public interface ProjectFuncMapper extends BaseMapper<ProjectFunc> {

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
	 * @param funcId
	 * @return
	 */
	ProjectFunc findProjectFuncByPId(String funcId);

	/**
	 * 通过pId删除project数据
	 * 
	 * @param funcId
	 */
	void deleteProjectFuncByPId(String funcId);

	/**
	 * 修改project数据
	 * 
	 * @param projectFunc
	 */
	void updateProjectFunc(ProjectFunc projectFunc);

	/**
	 * 保存project数据
	 * 
	 * @param appName
	 */
	void saveProjectFunc(@Param("appName") List<String> appName, @Param("projectId") String projectId);

	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	List<ProjectFunc> findProjectFuncAllList();

	/**
	 * 根据项目id获取所有project 功能菜单数据
	 *
	 * @return
	 */
	List<ProjectFunc> findProjectFuncList(String projectId);

}