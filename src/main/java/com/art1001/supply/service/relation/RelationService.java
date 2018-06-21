package com.art1001.supply.service.relation;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;


/**
 * relationService接口
 */
public interface RelationService {

	/**
	 * 查询分页relation数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Relation> findRelationPagerList(Pager pager);

	/**
	 * 通过relationId获取单条relation数据
	 * 
	 * @param relationId
	 * @return
	 */
	public Relation findRelationByRelationId(String relationId);

	/**
	 * 通过relationId删除relation数据
	 * 
	 * @param relationId
	 */
	public void deleteRelationByRelationId(String relationId);

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
}