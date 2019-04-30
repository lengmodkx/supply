package com.art1001.supply.service.organization.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.mapper.organization.OrganizationMapper;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import org.springframework.transaction.annotation.Transactional;

/**
 * organizationServiceImpl
 */
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper,Organization> implements OrganizationService {

	/** organizationMapper接口*/
	@Resource
	private OrganizationMapper organizationMapper;

	@Resource
	private OrganizationMemberService organizationMemberService;

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
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveOrganization(Organization organization){
		String userId = ShiroAuthenticationManager.getUserId();
		organization.setOrganizationId(IdGen.uuid());
		organization.setOrganizationMember(userId);
		organization.setCreateTime(System.currentTimeMillis());
		organization.setUpdateTime(System.currentTimeMillis());
		organizationMapper.saveOrganization(organization);

		//添加当前用户为企业拥有者
		OrganizationMember organizationMember = new OrganizationMember();
		organizationMember.setId(IdGen.uuid());
		organizationMember.setOrganizationId(organization.getOrganizationId());
		organizationMember.setMemberId(ShiroAuthenticationManager.getUserId());
		organizationMember.setCreateTime(System.currentTimeMillis());
		organizationMember.setUpdateTime(System.currentTimeMillis());
		organizationMember.setOrganizationLable(1);
		organizationMemberService.save(organizationMember);
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

	/**
	 * 获取和我相关的企业
	 * @param flag 标识
	 * @return 企业列表
	 */
	@Override
	public List<Organization> getMyOrg(Integer flag) {
		return organizationMapper.getMyOrg(flag,ShiroAuthenticationManager.getUserId());
	}

	/**
	 * 判断企业是否存在
	 * @param organizationId 企业id
	 * @return 结果
	 */
	@Override
	public Boolean checkOrgIsExist(String organizationId) {
		return organizationMapper.selectCount(new QueryWrapper<Organization>().eq("organization_id", organizationId)) > 0;
	}
}