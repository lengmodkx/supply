package com.art1001.supply.mapper.relation;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.relation.Relation;
import org.apache.ibatis.annotations.Mapper;

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
	void updateRelation(Relation relation);

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


}