package com.art1001.supply.service.project.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.project.OrganizationMember;
import com.art1001.supply.mapper.project.OrganizationMemberMapper;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * projectServiceImpl
 */
@Service
public class OrganizationMemberServiceImpl implements OrganizationMemberService {

	/** projectMapper接口*/
	@Resource
	private OrganizationMemberMapper organizationMemberMapper;
	
	/**
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<OrganizationMember> findOrganizationMemberPagerList(Pager pager){
		return organizationMemberMapper.findOrganizationMemberPagerList(pager);
	}

	/**
	 * 通过id获取单条project数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public OrganizationMember findOrganizationMemberById(String id){
		return organizationMemberMapper.findOrganizationMemberById(id);
	}

	/**
	 * 通过id删除project数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteOrganizationMemberById(String id){
		organizationMemberMapper.deleteOrganizationMemberById(id);
	}

	/**
	 * 修改project数据
	 * 
	 * @param organizationMember
	 */
	@Override
	public void updateOrganizationMember(OrganizationMember organizationMember){
		organizationMemberMapper.updateOrganizationMember(organizationMember);
	}
	/**
	 * 保存project数据
	 * 
	 * @param organizationMember
	 */
	@Override
	public void saveOrganizationMember(OrganizationMember organizationMember){
		organizationMember.setId(IdGen.uuid());
		organizationMemberMapper.saveOrganizationMember(organizationMember);
	}
	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	@Override
	public List<OrganizationMember> findOrganizationMemberAllList(){
		return organizationMemberMapper.findOrganizationMemberAllList();
	}

	@Override
	public OrganizationMember findOrgByMemberId(String memberId) {
		return organizationMemberMapper.findOrgByMemberId(memberId);
	}

}