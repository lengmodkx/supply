package com.art1001.supply.service.organization.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.art1001.supply.communication.service.ChatGroupAPI;
import com.art1001.supply.communication.service.impl.EasemobChatGroup;
import com.art1001.supply.entity.organization.OrganizationGroup;
import com.art1001.supply.entity.organization.OrganizationGroupMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.organization.OrganizationGroupMapper;
import com.art1001.supply.service.organization.OrganizationGroupMemberService;
import com.art1001.supply.service.organization.OrganizationGroupService;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.client.model.Group;
import io.swagger.client.model.UserName;
import io.swagger.client.model.UserNames;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import org.springframework.transaction.annotation.Transactional;

/**
 * 组织群组ServiceImpl
 */
@Service
public class OrganizationGroupServiceImpl extends ServiceImpl<OrganizationGroupMapper,OrganizationGroup> implements OrganizationGroupService {

	/** 组织群组Mapper接口*/
	@Resource
	private OrganizationGroupMapper organizationGroupMapper;

	/**
	 * 企业逻辑层bean
	 */
	@Resource
	private OrganizationService organizationService;

	/**
	 * 群组成员逻辑层Bean
	 */
	@Resource
	OrganizationGroupMemberService organizationGroupMemberService;

	@Resource
	private ChatGroupAPI chatGroupAPI;

	@Resource
	private UserService userService;
	
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

	/**
	 * 创建企业群组
	 * @param organizationGroup 群组信息对象
	 * @param memberIds 成员id 数组
	 * @return 结果
	 */
	@Override
	public Boolean createGroup(OrganizationGroup organizationGroup, String[] memberIds) {
		if(!organizationService.checkOrgIsExist(organizationGroup.getOrganizationId())){
			throw new ServiceException("该企业不能存在,不能创建群组!");
		}
		//保存群组数据
		organizationGroup.setCreateTime(System.currentTimeMillis());
		organizationGroup.setUpdateTime(System.currentTimeMillis());
		organizationGroup.setOwner(ShiroAuthenticationManager.getUserId());
		organizationGroupMapper.insert(organizationGroup);

		//在环信创建企业群组信息
		Group group = new Group();
		UserEntity byId = userService.findById(ShiroAuthenticationManager.getUserId());
		List<String> accountNames = userService.list(new QueryWrapper<UserEntity>().in("user_id", memberIds)).stream().map(UserEntity::getAccountName).collect(Collectors.toList());
		UserName userName=new UserName();
		userName.addAll(accountNames);
		group.groupname(organizationGroup.getGroupName()).desc("")._public(true).maxusers(50).approval(false).owner(byId.getAccountName()).members(userName);
		chatGroupAPI.createChatGroup(group);

		//保存分组成员信息
		if(memberIds != null){
			List<OrganizationGroupMember> gms = new ArrayList<>();
			for (String memberId : memberIds) {
				OrganizationGroupMember om = new OrganizationGroupMember();
				om.setGroupId(organizationGroup.getGroupId());
				om.setMemberId(memberId);
				om.setUpdateTime(System.currentTimeMillis());
				om.setCreateTime(System.currentTimeMillis());
				gms.add(om);
			}
			organizationGroupMemberService.saveBatch(gms);
		}
		return true;
	}

	/**
	 * 删除群组
	 * @param groupId 群组id
	 * @return 结果
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Boolean removeGroup(String groupId) {
		organizationGroupMapper.deleteById(groupId);
		organizationGroupMemberService.remove(new QueryWrapper<OrganizationGroupMember>().eq("group_id", groupId));
		return true;
	}

	/**
	 * 根据企业id 获取企业下的所有群组信息
	 * @param orgId 企业id
	 * @return 企业群组信息
	 */
	@Override
	public List<OrganizationGroup> getOrgGroups(String orgId) {
		if(!organizationService.checkOrgIsExist(orgId)){
			throw new ServiceException("该企业已经不存在!");
		}
		return organizationGroupMapper.selectOrgGroups(orgId);
	}

	/**
	 * 查询该群组是否存在
	 * @param groupId 群组id
	 * @return 是否存在
	 */
	@Override
	public Boolean checkGroupIsExist(String groupId) {
		return organizationGroupMapper.selectCount(new QueryWrapper<OrganizationGroup>().eq("group_id", groupId)) > 0;
	}

	/**
	 * 获取某个群组的拥有者信息
	 * @param groupId 群组id
	 * @return 拥有者信息
	 */
	@Override
	public OrganizationGroupMember getGroupOwnerInfo(String groupId) {
		if(!this.checkGroupIsExist(groupId)){
			throw new ServiceException("该群组不存在!");
		}
		return organizationGroupMapper.selectOwnerInfo(groupId);
	}
}