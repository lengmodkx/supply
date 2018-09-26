package com.art1001.supply.service.project;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.project.Project;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * projectService接口
 */
public interface ProjectService extends IService<Project> {

	/**
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Project> findProjectPagerList(Pager pager);

	/**
	 * 通过projectId获取单条project数据
	 * 
	 * @param projectId
	 * @return
	 */
	public Project findProjectByProjectId(String projectId);

	/**
	 * 通过projectId删除project数据
	 * 
	 * @param projectId
	 */
	public void deleteProjectByProjectId(String projectId);

	/**
	 * 修改project数据
	 * 
	 * @param project
	 */
	public void updateProject(Project project);

	/**
	 * 保存project数据
	 * 
	 * @param project
	 */
	public void saveProject(Project project);

	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	public List<Project> findProjectAllList();


	List<Project> findProjectByMemberId(String memberId,int projectDel);

	/**
	 * 查询出当前用户所执行的任务的 任务信息 和 项目信息
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

	/**
	 * 查询出用户参与的所有项目信息
	 * @param uId 用户id
	 * @return 项目实体信息集合
	 */
	List<Project> listProjectByUid(String uId);

	/**
	 * 数据:查询出当前用户收藏的所有项目
	 * 功能:添加关联页面展示 星标项目
	 * @param uId 用户id
	 * @return 用户收藏的所有项目信息
	 */
	List<Project> listProjectByUserCollect(String uId);


	/**
	 *  根据用户id查询我参与的项目
	 * @param userId 用户id
	 * @return
	 */
	List<Project> findProjectByUserId(String userId,int collect);


	/**
	 * 用于展示回收站的数据
	 * @param projectId 项目id
	 * @param type 选项卡的类型
	 */
	List<RecycleBinVO> recycleBinInfo(String projectId, String type);

}