package com.art1001.supply.service.relation;

import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.relation.GroupVO;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.TaskMenuVO;
import com.art1001.supply.entity.template.TemplateData;
import com.baomidou.mybatisplus.extension.service.IService;
import org.quartz.SimpleTrigger;

import java.util.List;


/**
 * relationService接口
 */
public interface RelationService extends IService<Relation> {

	/**
	 * 删除分组信息
	 * 
	 * @param groupId 分组id
	 */
	void deleteGroup(String groupId);

	/**
	 * 修改relation数据
	 * 
	 * @param relation
	 */
	public void updateRelation(Relation relation);

	/**
	 * 保存relation数据
	 * 
	 * @param relation
	 */
	public void saveRelation(Relation relation);

	void saveRelationBatch(List<String> relationList,String projectId,String parentId);


	void saveRelationBatch2(List<TemplateData> templateDataList, String projectId, String parentId);
	/**
	 * 获取所有relation数据
	 * 
	 * @return
	 */
	List<Relation> findRelationAllList(Relation relation);

	List<Relation> findMenus(String groupId);
	/**
	 * 根据分组删除分组下的所有菜单
	 * @param relationId
	 */
	void deletenMenuByRelationId(String relationId);

	/**
	 * 在分组下创建菜单
	 * @param parentId 分组的id
	 * @param relation 菜单信息
	 */
    void addMenu(String parentId, Relation relation);

	/**
	 * 编辑菜单信息
	 * @param relation 菜单实体信息
	 * @return
	 */
	int editMenu(Relation relation);

	/**
	 * 根据菜单id 排序任务
	 * @param relationId 菜单id
	 * @return
	 */
	Relation taskSort(String relationId);

	/**
	 * 排序分组内的菜单
	 * @param relationId 分组id
	 * @return
	 */
	List<Relation> menuSort(String relationId);

	/**
	 * 将分组和分组下的所有任务移至回收站
	 * @param relationId 分组的id
	 */
	void moveRecycleBin(String relationId,String relationDel);

	/**
	 * 设置菜单下的所有任务的执行者
	 * @param relationId 列表id
	 * @param userId 新的执行者的id
	 */
    void setMenuAllTaskExecutor(String relationId,String userId,String uName);

	/**
	 * 设置此菜单下的所有的任务的截止时间
	 * @param relationId 菜单id
	 * @param endTime 截止时间
	 */
	int setAllTaskEndTime(String relationId, Long endTime);

	/**
	 * 移动菜单下的所有任务信息
	 * @param oldTaskMenuVO 旧的任务位置信息
	 * @param newTaskMenuVO 新的任务位置信息
	 */
	void moveMenuAllTask(TaskMenuVO oldTaskMenuVO, TaskMenuVO newTaskMenuVO);

	/**
	 * 复制了列表下所有任务
	 * @param oldTaskMenuVO 复制前的任务位置信息
	 * @param newTaskMenuVO 复制到的任务位置信息
	 */
	void copyMenuAllTask(TaskMenuVO oldTaskMenuVO, TaskMenuVO newTaskMenuVO);

	/**
	 * 把菜单下的所有任务移到回收站
	 * @param relationId 菜单id
	 */
	void menuAllTaskToRecycleBin(String relationId);

	/**
	 * 根据菜单id 查询出该菜单所在的 分组信息以及项目信息
	 * @param relationId 菜单id
	 * @return
	 */
	TaskMenuVO findProjectAndGroupInfoByMenuId(String relationId);

	/**
	 * 根据查询菜单id 查询 菜单id 下的 最大排序号
	 * @param taskMenuId 菜单id
	 */
	int findMenuTaskMaxOrder(String taskMenuId);

	/**
	 * 查询出某个项目下的所有分组信息
	 * @param projectId 项目id
	 * @return
	 */
    List<Relation> findAllGroupInfoByProjectId(String projectId);

	/**
	 * 查询出某个分组下的所有菜单信息
	 * @param groupId 分组id
	 * @return
	 */
	List<Relation> findAllMenuInfoByGroupId(String groupId);

	/**
	 * 根据分组id 查询出该分组下的所有任务信息
	 * @param id 分组id
	 * @return 任务实体信息集合
	 */
	List<Relation> findGroupAllTask(String id);

	/**
	 * @param id 菜单id
	 * 根据菜单的id 查询出该菜单的信息及菜单下的所有任务信息
	 */
	Relation getRelationAndAllTaskInfo(String id);

	/**
	 * 根据项目的id 查询出该项目下的所有菜单信息 (不保括分组信息)
	 * @param projectId 项目id
	 * @return
	 */
    List<Relation> findMenusByProjectId(String projectId);

	/**
	 * 加载所有分组信息
	 * @param projectId 项目Id
	 * @return
	 */
	List<Relation> loadGroupInfo(String projectId);

	/**
	 * 查询出某个项目下 回收站中的所有任务分组
	 * @param projectId 项目id
	 * @return
	 */
	List<RecycleBinVO> findRecycleBin(String projectId);

	/**
	 * 查询一个菜单的名称 和  该菜单所属项目的名称
	 * @param menuId 菜单id
	 */
	TaskMenuVO findRelationNameAndProjectName(String menuId);

	/**
	 * 添加任务菜单
	 * @param relation 菜单信息 (名称,所在项目id,所在分组id)
	 */
	void saveMenu(Relation relation);

	/**
	 * 添加任务分组
	 * @param relation 分组信息(名称,所在项目)
	 */
	void saveGroup(Relation relation);

	/**
	 * 排序菜单
	 * @param menuIds 菜单id
	 */
    void orderMenu(String[] menuIds);

	/**
	 * 获取分组下所有菜单以及任务信息数据
	 * @param groupId 分组id
	 * @return 菜单和任务数据集合
	 */
	List<Relation> initMainPage(String groupId);

	/**
	 * 获取一个分组下的菜单以及菜单下的任务信息
	 * 注意(任务信息只包含执行者头像和任务名称,任务id)
	 * @param groupId 分组id
	 * @return 菜单以及任务信息
	 */
    List<Relation> bindMenuInfo(String groupId);

	/**
	 * 获取项目下的全部分组信息
	 * 分组信息中包括了 任务总数  完成数 优先级信息等
	 * @param  projectId 项目id
	 * @return 分组详情信息
	 */
    List<GroupVO> getGroupsInfo(String projectId);

	/**
	 * 移动列表下的所有任务
	 * @param menuId 列表id
	 * @param projectId 项目id
	 * @param groupId 分组id
	 * @param toMenuId 移动到的列表id
	 * @return 结果
	 */
	boolean moveAllTask(String menuId, String projectId, String groupId, String toMenuId);

	/**
	 * 复制列表下的所有任务
	 * @param menuId 要复制的列表id
	 * @param projectId 项目id
	 * @param groupId 分组id
	 * @param toMenuId 复制到的列表id
	 * @return 是否成功
	 */
	boolean copyAllTask(String menuId, String projectId, String groupId, String toMenuId);

	/**
	 * 列表下所有任务移动到回收站
	 * @param menuId 列表id
	 * @return 是否成功
	 */
	boolean allTaskMoveRecycleBin(String menuId);

	/**
	 * 设置该列表下的所有执行者
	 * @param menuId 列表id
	 * @param executor 执行者id
	 * @return 是否成功
	 */
	boolean setAllTaskExecutor(String menuId, String executor);

	/**
	 * 删除此列表
	 * @param menuId 列表id
	 * @return 是否成功
	 */
	boolean removeMenu(String menuId);

	/**
	 * 通过列表id获取项目id
	 * @param relationId 列表id
	 * @return Obj
	 */
	String getObject(String relationId);

	/**
	 * 获取分组下的任务id集合
	 * 如果groupId为空的话则返回空集合
	 * @author heShaoHua
	 * @describe 暂无
	 * @param groupId 分组id
	 * @updateInfo 暂无
	 * @date 2019/7/15 9:50
	 * @return id集合信息
	 */
	List<String> getGroupTaskId(String groupId);

	/**
	 * 检查用户是否具有访问该分组的权限
	 * @author heShaoHua
	 * @describe 暂无
	 * @param groupId 分组id
	 * @updateInfo 暂无
	 * @date 2019/9/25 13:38
	 * @return 结果
	 */
    int checkUserIsExistGroup(String groupId);

    /**
     * 获取一个分组或者 列表的项目id
     * @author heShaoHua
     * @describe 暂无
     * @param relationId 分组或者列表id
     * @updateInfo 暂无
     * @date 2019/10/9 11:29
     * @return 项目id
     */
    String getProjectId(String relationId);
}