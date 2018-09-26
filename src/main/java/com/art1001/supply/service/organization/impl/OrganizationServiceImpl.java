package com.art1001.supply.service.organization.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.organization.OrganizationGroup;
import com.art1001.supply.mapper.organization.OrganizationGroupMapper;
import com.art1001.supply.mapper.organization.OrganizationMapper;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * organizationServiceImpl
 */
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper,Organization> implements OrganizationService {

	/** organizationMapper接口*/
	@Resource
	private OrganizationMapper organizationMapper;

	/**
	 * 查询分页organization数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Organization> findOrganizationPagerList(Pager pager){
		return organizationMapper.findOrganizationPagerList(pager);
	}

	/**
	 * 通过organizationId获取单条organization数据
	 * 
	 * @param organizationId
	 * @return
	 */
	@Override 
	public Organization findOrganizationByOrganizationId(String organizationId){
		return organizationMapper.findOrganizationByOrganizationId(organizationId);
	}

	/**
	 * 通过organizationId删除organization数据
	 * 
	 * @param organizationId
	 */
	@Override
	public void deleteOrganizationByOrganizationId(String organizationId){
		organizationMapper.deleteOrganizationByOrganizationId(organizationId);
	}

	/**
	 * 修改organization数据
	 * 
	 * @param organization
	 */
	@Override
	public void updateOrganization(Organization organization){
		organization.setUpdateTime(System.currentTimeMillis());
		organizationMapper.updateOrganization(organization);
	}
	/**
	 * 保存organization数据
	 * 
	 * @param organization 企业信息
	 */
	@Override
	public void saveOrganization(Organization organization){
		String userId = ShiroAuthenticationManager.getUserId();
		organization.setOrganizationId(IdGen.uuid());
		organization.setOrganizationImage("");
		organization.setOrganizationMember(userId);
		organization.setCreateTime(System.currentTimeMillis());
		organization.setUpdateTime(System.currentTimeMillis());
		organizationMapper.saveOrganization(organization);
	}
	/**
	 * 获取所有organization数据
	 * 
	 * @return
	 */
	@Override
	public List<Organization> findOrganizationAllList(){
		return organizationMapper.findOrganizationAllList();
	}

	/**
	 * 获取用户参与的所有企业 以及 企业项目
	 * @param userId
	 * @return
	 */
	@Override
	public List<Organization> findJoinOrgProject(String userId) {
		return organizationMapper.selectJoinOrgProject(userId);
	}
}