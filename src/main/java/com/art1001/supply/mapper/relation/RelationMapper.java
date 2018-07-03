package com.art1001.supply.mapper.relation;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskMenuVO;
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
	void deleteRelationByRelationId(String relationId);

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


}