package com.art1001.supply.service.project;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.project.GantChartVO;
import com.art1001.supply.entity.project.Project;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
	 *  根据用户id查询我的项目
	 * @param userId 用户id
	 * @return
	 */
	List<Project> findProjectByUserId(String userId);


	/**
	 * 用于展示回收站的数据
	 * @param projectId 项目id
	 * @param type 选项卡的类型
	 */
	List<RecycleBinVO> recycleBinInfo(String projectId, String type);

	/**
	 * 查询我参与的企业项目
	 * @param userId 用户id
	 * @return
	 */
	List<Project> findOrgProject(String userId,String orgId);

	/**
	 * 查询出该项目的默认分组
	 * @param projectId 项目id
	 * @return
	 */
    String findDefaultGroup(String projectId);

	/**
	 * 获取项目的甘特图数据
	 * @param projectId 项目id
	 * @return
	 */
	List<GantChartVO> getGanttChart(String projectId);

	/**
	 * 根据项目名称搜索项目
	 * @param projectName 项目名称
	 * @param condition 搜索条件
	 * @return
	 */
    List<Project> seachByName(String projectName, String condition);

	/**
	 * 获取项目下的所有任务id字符串 (逗号隔开)
	 * 包括子任务id
	 * 使用时需要自己分割
	 * @param projectId 项目id
	 * @return id字符串
	 */
	String findProjectAllTask(String projectId);

	/**
	 * 修改项目封面图片
	 * @param projectId 项目id
	 * @param fileUrl  文件路径
	 * @return int
	 */
	Integer updatePictureById(String projectId, String fileUrl);
}