package com.art1001.supply.service.relation;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.relation.Relation;


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
	public List<Relation> findRelationAllList();
	
}