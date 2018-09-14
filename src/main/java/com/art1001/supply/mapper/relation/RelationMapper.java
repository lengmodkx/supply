package com.art1001.supply.mapper.relation;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.relation.GroupVO;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.TaskMenuVO;
import com.art1001.supply.entity.template.TemplateData;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * relationmapper接口
 */
@Mapper
public interface RelationMapper {

	/**
	 * 查询分页relation数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Relation> findRelationPagerList(Pager pager);

	/**
	 * 通过relationId获取单条relation数据
	 * 
	 * @param relationId
	 * @return
	 */
	Relation findRelationByRelationId(String relationId);

	/**
	 * 通过relationId删除relation数据
	 * 
	 * @param relationId
	 */
	void deleteGroup(String relationId);

	/**
	 * 修改relation数据
	 *
     * @param relation
     */
	int updateRelation(Relation relation);

	/**
	 * 保存relation数据
	 * 
	 * @param relation
	 */
	void saveRelation(Relation relation);

	void saveRelationBatch(@Param("relationList") List<String> relationList,@Param("projectId") String projectId,@Param("parentId") String parentId);

	void saveRelationBatch2(@Param("templateDataList") List<TemplateData> templateDataList, @Param("projectId") String projectId, @Param("parentId") String parentId);

	/**
	 * 获取所有relation数据
	 * 
	 * @return
	 */
	List<Relation> findRelationAllList(Relation relation);

	/**
	 * 根据分组删除分组下的所有菜单
	 * @param relationId
	 */
	void deletenMenuByRelationId(String relationId);


	/**
	 * 根据菜单id排序任务
	 * @param relationId 菜单id
	 * @return
	 */
	Relation taskSort(String relationId);

	/**
	 * 排序分组内的菜单
	 * @param relationId
	 * @return
	 */
	List<Relation> menuSort(String relationId);

	/**
	 * 将菜单或者任务放入回收站
	 * @param relationId
	 */
	void moveRecycleBin(@Param("relationId") String relationId,@Param("relationDel") String relationDel,@Param("updateTime")Long updateTime);

	/**
	 *
	 * @param relationId
	 */
    void setMenuAllTaskExecutor(String relationId);

	/**
	 * 根据任务id查询出该任务的菜单信息
	 * @param taskId
	 * @return
	 */
	Relation findMenuInfoByTaskId(String taskId);

	/**
	 * 根据菜单id 查询出 菜单说在的分组信息 和 项目信息
	 * @param relationId 菜单id
	 * @return
	 */
    TaskMenuVO findProjectAndGroupInfoByMenuId(String relationId);

	/**
	 * 查询当前菜单下的任务的最大序号
	 * @param taskMenuId 菜单id
	 * @return
	 */
	int findMenuTaskMaxOrder(String taskMenuId);

	/**
	 * 查询某个项目下的所有分组信息
	 * @param projectId 项目id
	 * @return
	 */
    List<Relation> findAllGroupInfoByProjectId(String projectId);

	/**
	 * 查询出某个分组中的所有菜单信息
	 * @param groupId 分组的id
	 * @return
	 */
	List<Relation> findAllMenuInfoByGroupId(String groupId);

	/**
	 * 根据菜单id 查询出该菜单的id 及菜单下的所有任务信息
	 * @param relationId 菜单id
	 * @return
	 */
	Relation getRelationAndAllTaskInfo(String relationId);

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
    List<GroupVO> loadGroupInfo(String projectId);

	/**
	 * 查询出某个项目下 回收站中的所有任务分组
	 * @param projectId 项目id
	 * @return
	 */
    List<RecycleBinVO> findRecycleBin(String projectId);

	/**
	 * 根据分组id 查询出 分组下所有菜单的id
	 * @param relationId 分组id
	 * @return
	 */
	List<String> findMenuIdByGroup(String relationId);

	/**
	 * 分组下菜单的所有任务id
	 * @param menuIds 菜单集合
	 * @return
	 */
	List<String> findTaskIdByMenus(List<String> menuIds);

	/**
	 * 删除多个 relation
	 * @param menuIds relationId 集合
	 */
    void deleteManyRelation(List<String> menuIds);

	/**
	 * 删除一条relation
	 * @param relationId
	 */
	@Delete("delete from prm_relation where relation_id = #{relationId}")
    void deleteRelationById(String relationId);

	/**
	 * 查询一个菜单的名称 和  该菜单所属项目的名称
	 * @param menuId 菜单id
	 */
	TaskMenuVO findRelationNameAndProjectName(String menuId);

	/**
	 * 查询出该分组下最大的排序编号
	 * @param publicId 分组id 或者 项目id
	 * @return
	 */
	int findMaxOrder(@Param("publicId") String publicId, @Param("lable") int lable);
}