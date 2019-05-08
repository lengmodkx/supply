package com.art1001.supply.service.partment.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.partment.PartmentMember;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.partment.PartmentMapper;
import com.art1001.supply.service.partment.PartmentMemberService;
import com.art1001.supply.service.partment.PartmentService;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import org.springframework.transaction.annotation.Transactional;

/**
 * partmentServiceImpl
 */
@Service
public class PartmentServiceImpl extends ServiceImpl<PartmentMapper,Partment> implements PartmentService {

	/** partmentMapper接口*/
	@Resource
	private PartmentMapper partmentMapper;

	@Resource
	private PartmentMemberService partmentMemberService;

	/**
	 * 查询分页partment数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Partment> findPartmentPagerList(Pager pager){
		return partmentMapper.findPartmentPagerList(pager);
	}

	/**
	 * 通过partmentId获取单条partment数据
	 * 
	 * @param partmentId
	 * @return
	 */
	@Override 
	public Partment findPartmentByPartmentId(String partmentId){
		return partmentMapper.findPartmentByPartmentId(partmentId);
	}

	/**
	 * 通过partmentId删除partment数据
	 * @param partmentId 部门id
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deletePartmentByPartmentId(String partmentId){
		//删除部门和成员的关系数据
		List<String> subIds = partmentMapper.selectList(new QueryWrapper<Partment>().eq("parent_id", partmentId).select("partment_id")).stream().map(Partment::getPartmentId).collect(Collectors.toList());
		if(CollectionUtils.isEmpty(subIds)){
			subIds = new ArrayList<String>();
		}
		subIds.add(partmentId);
		partmentMemberService.remove(new QueryWrapper<PartmentMember>().in("partment_id", subIds));
		//删除子部门
		partmentMapper.delete(new QueryWrapper<Partment>().eq("parent_id", partmentId));
		//删除部门
		partmentMapper.deletePartmentByPartmentId(partmentId);
	}

	/**
	 * 修改partment数据
	 * 
	 * @param partment
	 */
	@Override
	public void updatePartment(Partment partment){
		partmentMapper.updatePartment(partment);
	}
	/**
	 * 保存partment数据
	 * @param partment
	 */
	@Override
	public void savePartment(Partment partment){
	    if(Stringer.isNullOrEmpty(partment.getParentId())){
	        partment.setParentId("0");
        }
		int maxOrder = partmentMapper.findMaxOrder(partment.getOrganizationId(),partment.getParentId());
		partment.setPartmentOrder(maxOrder+1);
		partment.setUpdateTime(System.currentTimeMillis());
		partment.setCreateTime(System.currentTimeMillis());
		partmentMapper.insert(partment);
	}

	/**
	 * 检查该部门是存在
	 * @param partmentId 部门id
	 * @return 结果
	 */
	@Override
	public boolean checkPartmentIsExist(String partmentId) {
		return partmentMapper.selectCount(new QueryWrapper<Partment>().eq("partment_id", partmentId)) > 0;
	}

	/**
	 * 获取某个企业下的部门信息
	 * @param orgId 企业id
	 * @return 部门信息
	 */
	@Override
	public List<Partment> findOrgPartmentInfo(String orgId) {
		if(Stringer.isNullOrEmpty(orgId)){
			throw new ServiceException("orgId不能为空!");
		}
		List<Partment> partments = partmentMapper.selectOrgPartmentInfo(orgId);
		partments.forEach(item -> {
			if(!CollectionUtils.isEmpty(item.getSubPartments())){
				item.setIsExistSubPartment(true);
			} else{
				item.setIsExistSubPartment(false);
			}
			item.setSubPartments(null);
		});
		return partments;
	}

	/**
	 * 排序部门
	 * @param partmentIds 排序后的部门id
	 */
	@Override
	public Boolean orderPartment(String[] partmentIds) {
		List<Partment> partments = new ArrayList<>();
		int total = partmentIds.length;
		for (String partmentId : partmentIds) {
			Partment partment = new Partment();
			partment.setPartmentId(partmentId);
			partment.setPartmentOrder(total);
			partment.setUpdateTime(System.currentTimeMillis());
			partments.add(partment);
			total--;
		}
		return updateBatchById(partments);
	}
}