package com.art1001.supply.service.organization.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.mapper.organization.OrganizationGroupMapper;
import com.art1001.supply.service.organization.OrganizationGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.OrganizationGroup;

/**
 * 组织群组ServiceImpl
 */
@Service
public class OrganizationGroupServiceImpl extends ServiceImpl<OrganizationGroupMapper,OrganizationGroup> implements OrganizationGroupService {

	/** 组织群组Mapper接口*/
	@Resource
	private OrganizationGroupMapper organizationGroupMapper;
	
	/**
	 * 查询分页组织群组数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<OrganizationGroup> findOrganizationGroupPagerList(Pager pager){
		return organizationGroupMapper.findOrganizationGroupPagerList(pager);
	}

	/**
	 * 通过groupId获取单条组织群组数据
	 * 
	 * @param groupId
	 * @return
	 */
	@Override 
	public OrganizationGroup findOrganizationGroupByGroupId(String groupId){
		return organizationGroupMapper.findOrganizationGroupByGroupId(groupId);
	}

	/**
	 * 通过groupId删除组织群组数据
	 * 
	 * @param groupId
	 */
	@Override
	public void deleteOrganizationGroupByGroupId(String groupId){
		organizationGroupMapper.deleteOrganizationGroupByGroupId(groupId);
	}

	/**
	 * 修改组织群组数据
	 * 
	 * @param organizationGroup
	 */
	@Override
	public void updateOrganizationGroup(OrganizationGroup organizationGroup){
		organizationGroupMapper.updateOrganizationGroup(organizationGroup);
	}
	/**
	 * 保存组织群组数据
	 * 
	 * @param organizationGroup
	 */
	@Override
	public void saveOrganizationGroup(OrganizationGroup organizationGroup){
		organizationGroupMapper.saveOrganizationGroup(organizationGroup);
	}
	/**
	 * 获取所有组织群组数据
	 * 
	 * @return
	 */
	@Override
	public List<OrganizationGroup> findOrganizationGroupAllList(){
		return organizationGroupMapper.findOrganizationGroupAllList();
	}
	
}