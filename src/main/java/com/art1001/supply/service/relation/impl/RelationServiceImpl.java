package com.art1001.supply.service.relation.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.mapper.relation.RelationMapper;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * relationServiceImpl
 */
@Service
public class RelationServiceImpl implements RelationService {

	/** relationMapper接口*/
	@Resource
	private RelationMapper relationMapper;
	
	/**
	 * 查询分页relation数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Relation> findRelationPagerList(Pager pager){
		return relationMapper.findRelationPagerList(pager);
	}

	/**
	 * 通过relationId获取单条relation数据
	 * 
	 * @param relationId
	 * @return
	 */
	@Override 
	public Relation findRelationByRelationId(String relationId){
		return relationMapper.findRelationByRelationId(relationId);
	}

	/**
	 * 通过relationId删除relation数据
	 * 
	 * @param relationId
	 */
	@Override
	public void deleteRelationByRelationId(String relationId){
		relationMapper.deleteRelationByRelationId(relationId);
	}

	/**
	 * 修改relation数据
	 * 
	 * @param relation
	 */
	@Override
	public void updateRelation(Relation relation){
		relationMapper.updateRelation(relation);
	}
	/**
	 * 保存relation数据
	 * 
	 * @param relation
	 */
	@Override
	public void saveRelation(Relation relation){
		relation.setRelationId(IdGen.uuid());
		relationMapper.saveRelation(relation);
	}
	/**
	 * 获取所有relation数据
	 * 
	 * @return
	 */
	@Override
	public List<Relation> findRelationAllList(){
		return relationMapper.findRelationAllList();
	}
	
}