package com.art1001.supply.service.relation.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.mapper.relation.RelationMapper;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * relationServiceImpl
 */
@Service
public class RelationServiceImpl implements RelationService {

	/** relationMapper接口*/
	@Resource
	private RelationMapper relationMapper;

	/**taskService 逻辑层接口  */
	@Resource
	private TaskService taskService;

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
		//已完成的菜单
		Relation completed = new Relation(IdGen.uuid(),"已完成",relation.getRelationId(),1,0,System.currentTimeMillis(),System.currentTimeMillis());
		//进行中
		Relation conduct = new Relation(IdGen.uuid(),"进行中",relation.getRelationId(),1,0,System.currentTimeMillis(),System.currentTimeMillis());
		//待处理
		Relation pending = new Relation(IdGen.uuid(),"待处理",relation.getRelationId(),1,0,System.currentTimeMillis(),System.currentTimeMillis());
		relationMapper.saveRelation(completed);
		relationMapper.saveRelation(conduct);
		relationMapper.saveRelation(pending);
	}
	/**
	 * 获取所有relation数据
	 * 
	 * @return
	 */
	@Override
	public List<Relation> findRelationAllList(Relation relation){
		return relationMapper.findRelationAllList(relation);
	}

	/**
	 * 根据分组删除分组下的所有菜单
	 * @param relationId 分组id
	 */
	@Override
	public void deletenMenuByRelationId(String relationId) {
		relationMapper.deletenMenuByRelationId(relationId);
	}

	/**
	 * 在分组下创建菜单
	 * @param parentId 分组的id
	 * @param relation 菜单信息
	 */
	@Override
	public void addMenu(String parentId, Relation relation) {
		relation.setRelationId(IdGen.uuid());
		relation.setParentId(parentId);
		relation.setLable(1);
		relation.setRelationDel(0);
		relation.setCreateTime(System.currentTimeMillis());
		relation.setUpdateTime(System.currentTimeMillis());
		relationMapper.saveRelation(relation);
	}

	/**
	 * 编辑菜单信息
	 * @param relation 菜单实体信息
	 * @return
	 */
	@Override
	public int editMenu(Relation relation) {
		return relationMapper.updateRelation(relation);
	}

	/**
	 * 根据菜单id 排序任务
	 * @param relationId 菜单id
	 * @return
	 */
	@Override
	public Relation taskSort(String relationId) {
		return relationMapper.taskSort(relationId);
	}

	/**
	 * 排序分组内的菜单
	 * @param relationId 分组id
	 * @return
	 */
	@Override
	public List<Relation> menuSort(String relationId) {
		return relationMapper.menuSort(relationId);
	}

	/**
	 * 将分组和分组下的所有任务(移至回收站 或者 恢复)
	 * @param relationId 分组的id
	 * @param relationDel 当前分组的状态
	 */
	@Override
	public void moveRecycleBin(String relationId,String relationDel) {
		List<Relation> relationList = relationMapper.menuSort(relationId);
		for (Relation relation : relationList) {
			List<Task> tasks = taskService.taskMenu(relation.getRelationId());
			for (Task task : tasks) {
				taskService.moveToRecycleBin(task.getTaskId(),String.valueOf(task.getTaskDel()));
			}
			relationMapper.moveRecycleBin(relation.getRelationId(),String.valueOf(relation.getRelationDel()));
		}
		relationMapper.moveRecycleBin(relationId,relationDel);
	}
}