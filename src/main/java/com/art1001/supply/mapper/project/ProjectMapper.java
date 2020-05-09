package com.art1001.supply.mapper.project;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * projectmapper接口
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

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

	Project getProjectGanttChart(@Param("projectId") String projectId);

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
	List<Project> findProjectByMemberId(@Param("memberId") String memberId,@Param("projectDel") int projectDel);

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

	/**
	 * 查询出用户参与的所有项目信息
	 * @param uId 用户id
	 * @return 项目实体信息集合
	 */
	List<Project> listProjectByUid(String uId);

	/**
	 * 查询出用户收藏的所有项目信息
	 * @param uId 用户的id
	 * @return 用户收藏的所有项目信息
	 */
	List<Project> listProjectByUserCollect(String uId);

	/**
	 *  根据用户id查询所有项目，包含我创建的，我参与的，星标项目，回收站的项目
	 * @param userId 用户id
	 * @return
	 */
	List<Project> findProjectByUserId(@Param("userId") String userId);

	/**
	 * 查询我参与的企业项目
	 * @param userId 用户id
	 * @return
	 */
	List<Project> findOrgProject(@Param("userId") String userId,@Param("orgId") String orgId);

	/**
	 * 查询出当前项目的默认分组
	 * @param projectId 项目id
	 * @return
	 */
    String selectDefaultGroup(String projectId);

	/**
	 * 获取项目下的所有任务id (逗号隔开 需自己分割)
	 * 包括子任务信息  但是只返回任务id 并且任务之前没有父子关系 需要自行排列
	 * @param projectId 项目id
	 * @return
	 */
	String selectProjectAllTask(@Param("projectId") String projectId);

	/**
	 * 根据项目名称模糊查询星标项目
	 * @param projectName 项目名称
	 * @return
	 */
    List<Project> selectStarByName(@Param("projectName") String projectName, @Param("userId") String userId);

	/**
	 * @param userId 用户id
	 * @param projectName 项目名称
	 * @return
	 */
	List<Project> selectJoin(@Param("userId") String userId, @Param("projectName") String projectName);

	/**
	 * 查询出当前用户创建的项目 并且根据项目名称模糊查询
	 * @param userId 用户id
	 * @param projectName 项目名称
	 * @return
	 */
	List<Project> selectCreatedByName(@Param("userId")String userId, @Param("projectName")String projectName);

	/**
	 * 修改项目封面图片
	 * @param projectId 项目id
	 * @param fileUrl  文件路径
	 * @return int
	 */
	@Update("UPDATE prm_project p SET p.project_cover = #{fileUrl} WHERE p.project_id = #{projectId}")
	Integer updatePictureById(@Param("projectId")String projectId, @Param("fileUrl")String fileUrl);

    void updateAllProject(@Param("userId") String userId,@Param("id") String id);

	/**
	 * 根据企业id和用户id查询项目
	 * @param orgId
	 * @param memberId
	 * @return
	 */
    List<Project> getProjectsByMemberIdAndOrgId(@Param("orgId") String orgId,@Param("memberId") String memberId);


}