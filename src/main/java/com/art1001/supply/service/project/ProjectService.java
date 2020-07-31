package com.art1001.supply.service.project;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.organization.OrganizationMemberInfo;
import com.art1001.supply.entity.project.GantChartVO;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMemberDTO;
import com.art1001.supply.entity.project.ProjectTreeVO;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.vo.MenuVo;
import com.art1001.supply.entity.task.vo.TaskDynamicVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.text.ParseException;
import java.util.Date;
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
	void saveProject(Project project);

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
	List<GantChartVO> getGanttChart(String projectId, String groupId);

	/**
	 * 根据项目名称搜索项目
	 * @param projectName 项目名称
	 * @param condition 搜索条件
	 * @return
	 */
    List<Project> seachByName(String projectName, String condition,String orgId);

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

	/**
	 * 根据项目id查询此项目是否存在
	 * @author heShaoHua
	 * @describe 暂无
	 * @param projectId 项目id
	 * @updateInfo 暂无
	 * @date 2019/6/19 15:49
	 * @return 是否存在
	 */
	Boolean checkIsExist(String projectId);

	/**
	 * 判断该项目是否不存在
	 * 如果projectId不存在则返回null
	 * @author heShaoHua
	 * @describe 暂无
	 * @param projectId 项目id
	 * @updateInfo 暂无
	 * @date 2019/6/21 11:06
	 * @return 是否不存在
	 */
	Boolean notExist(String projectId);

	/**
	 * 获取到项目的树节点数据
	 * @author heShaoHua
	 * @param projectId 父项目id
	 * @describe 暂无
	 * @updateInfo 暂无
	 * @date 2019/7/11 10:43
	 * @return 树节点集合数据
	 */
    List<ProjectTreeVO> getTreeData(String projectId);

    /**
     * 获取到某个项目的子项目信息集合
	 * 如果projectId 为空 返回size为0的ArrayList
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 父级项目id
     * @updateInfo 暂无
     * @date 2019/7/11 11:15
     * @return 子项目信息集合
     */
    List<Project> getSubProject(String projectId);

    /**
     * 检查该项目是不是子项目
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
     * @updateInfo 暂无
     * @date 2019/10/9 14:18
     * @return 结果
     */
    Boolean checkIsSubProject(String projectId);

    void updateAllProject(String userId, String id);


    /**
    * @Author: 邓凯欣
    * @Email：dengkaixin@art1001.com
    * @Param:
    * @return:
    * @Description: 修改部门成员详细信息
    * @create: 18:33 2020/4/22
    */
	Integer updateMembersInfo(String memberId,String orgId,String userName,String entryTime, String job,String memberLabel,String address,String memberEmail,String phone,String birthday,String deptId);

	/**
	* @Author: 邓凯欣
	* @Email：dengkaixin@art1001.com
	* @Param: orgId 企业id
	* @Param: memberId 用户id
	* @Param: dateSort 查询时间戳
	* @return:
	* @Description: 根据用户id和项目id获取任务列表
	* @create: 18:08 2020/4/26
	*/
    List<Task> getTaskDynamicVOS(String orgId, String memberId, String dateSort) throws ParseException;

	/**
	 * 根据企业id和用户id查询项目
	 * @param orgId
	 * @param memberId
	 * @return
	 */
    List<Project> getProjectsByMemberIdAndOrgId(String orgId, String memberId);

	/**
	 * 查询企业下该成员所有的项目信息
	 * @param memberId
	 * @param organizationId
	 * @return
	 */
	List<Project> getExperience(String memberId, String organizationId);

	/**
	 * 项目角色判断
	 * @param projectId
	 * @return
	 */
    Integer judgmentRoles(String projectId);

	/**
	 * 添加项目成员
	 * @param projectId
	 * @param memberId
	 * @return
	 */
    String addMember(String projectId, String memberId);

    List<MenuVo> taskIndex(String projectId,String userId);
}