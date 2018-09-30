package com.art1001.supply.service.relation;

import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.TaskMenuVO;
import com.art1001.supply.entity.template.TemplateData;
import com.baomidou.mybatisplus.extension.service.IService;

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
	public List<Relation> findRelationAllList(Relation relation);

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
	void setMenuAllTaskEndTime(String relationId, Long endTime);

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
}