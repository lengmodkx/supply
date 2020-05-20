package com.art1001.supply.service.project.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.InvitationLink;
import com.art1001.supply.entity.organization.OrganizationMemberInfo;
import com.art1001.supply.entity.project.ProjectMemberDTO;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.organization.OrganizationMapper;
import com.art1001.supply.mapper.project.OrganizationMemberMapper;
import com.art1001.supply.service.organization.OrganizationMemberInfoService;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.partment.PartmentMemberService;
import com.art1001.supply.service.partment.PartmentService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import com.art1001.supply.util.ValidatedUtil;
import com.art1001.supply.util.crypto.ShortCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

/**
 * projectServiceImpl
 */
@Service
public class OrganizationMemberServiceImpl extends ServiceImpl<OrganizationMemberMapper,OrganizationMember> implements OrganizationMemberService {

	/** projectMapper接口*/
	@Resource
	private OrganizationMemberMapper organizationMemberMapper;

	/** organizationMapper接口*/
	@Resource
	private OrganizationMapper organizationMapper;

	@Resource
	private UserService userService;


	@Resource
	private RoleUserService roleUserService;

	@Resource
	private RoleService roleService;

	@Resource
	private OrganizationMemberService organizationMemberService;

	@Resource
	private PartmentService partmentService;

	@Resource
	private OrganizationService organizationService;

	@Resource
	private PartmentMemberService partmentMemberService;

	@Resource
	private OrganizationMemberInfoService organizationMemberInfoService;

	@Resource
	private RedisUtil redisUtil;

	private String orgId;
	private String userId;


	/**
	 * 根据企业id获取企业员工
	 * @param orgId 企业id
	 * @return 员工信息
	 */
	@Override
	public List<UserEntity> getUserList(String orgId) {
		return organizationMemberMapper.getUserList(orgId);
	}

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
		OrganizationMember organizationMember1 = getOne(new QueryWrapper<OrganizationMember>().eq("member_id", organizationMember.getMemberId()).eq("organization_id", organizationMember.getOrganizationId()));
		if(organizationMember1==null){
			organizationMember.setCreateTime(currentTimeMillis());
			organizationMember.setUpdateTime(currentTimeMillis());
			organizationMemberMapper.insert(organizationMember);
			String orgId = organizationMemberService.findOrgByUserId(organizationMember.getMemberId());
			if(StringUtils.isEmpty(orgId)){
				organizationMemberService.updateUserDefaultOrg(organizationMember.getOrganizationId(),organizationMember.getMemberId());
			}
			//修改企业成员默认权限，2020-20-10 汪亚锋
			Role role = roleService.getOrgDefaultRole(organizationMember.getOrganizationId());
			RoleUser roleUser = new RoleUser();
			roleUser.setOrgId(organizationMember.getOrganizationId());
			roleUser.setRoleId(role.getRoleId());
			roleUser.setUId(organizationMember.getMemberId());
			roleUser.setTCreateTime(LocalDateTime.now());
			roleUserService.save(roleUser);
		}
	}
	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	@Override
	public List<OrganizationMember> findOrganizationMemberAllList(OrganizationMember organizationMember){
		return organizationMemberMapper.findOrganizationMemberAllList(organizationMember);
	}

	@Override
	public OrganizationMember findOrgByMemberId(String memberId, String orgId) {
		return organizationMemberMapper.findOrgByMemberId(memberId,orgId);
	}

	/**
	 * 获取用户已经加入的企业数量
	 * @return 加入的企业数量
	 */
	@Override
	public int userOrgCount() {
		return organizationMemberMapper.selectCount(new QueryWrapper<OrganizationMember>().eq("member_id",ShiroAuthenticationManager.getUserId()));
	}

	/**
	 * 修改一个用户的默认企业
	 * @param orgId  企业id
	 * @param userId 用户id
	 * @return 结果
	 * @author heShaoHua
	 * @describe 失败返回-1
	 * @updateInfo 暂无
	 * @date 2019/5/29 11:08
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer updateUserDefaultOrg(String orgId, String userId) {
		//构造修改数据信息
		OrganizationMember organizationMember = new OrganizationMember();
		organizationMember.setUserDefault(false);
		organizationMember.setUpdateTime(currentTimeMillis());

		//构造出条件对象(清除当前用户的企业记录)
		LambdaQueryWrapper<OrganizationMember> clear = new QueryWrapper<OrganizationMember>().lambda()
				.eq(OrganizationMember::getMemberId, userId)
				.eq(OrganizationMember::getUserDefault, true);

		//构造出条件对象(标记出新的用户企业记录)
		LambdaQueryWrapper<OrganizationMember> sign = new QueryWrapper<OrganizationMember>().lambda()
				.eq(OrganizationMember::getMemberId, userId)
				.eq(OrganizationMember::getOrganizationId, orgId);

		organizationMemberMapper.update(organizationMember,clear);
		organizationMember.setUserDefault(true);
		organizationMemberMapper.update(organizationMember,sign);
		return 1;
	}

	/**
	 * 保存企业的创建人信息
	 * @param orgId  企业id
	 * @param userId 用户id
	 * @return 结果
	 * @author heShaoHua
	 * @describe 暂无
	 * @updateInfo 暂无
	 * @date 2019/5/29 15:14
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer saveOrgOwnerInfo(String orgId, String userId) {
		//添加当前用户为企业拥有者
		OrganizationMember organizationMember = new OrganizationMember();
		organizationMember.setOrganizationId(orgId);
		organizationMember.setMemberId(userId);
		organizationMember.setCreateTime(currentTimeMillis());
		organizationMember.setUpdateTime(currentTimeMillis());
		organizationMember.setOrganizationLable(1);

		UserEntity user = userService.findById(userId);
		//organizationMember.setMemberId(userId);
		organizationMember.setAddress(user.getAddress());
		organizationMember.setImage(user.getImage());
		organizationMember.setPhone(user.getAccountName());
		organizationMember.setJob(user.getJob());
		organizationMember.setMemberEmail(user.getEmail());
		organizationMember.setUserName(user.getUserName());

		//todo 添加其他信息
		organizationMemberMapper.insert(organizationMember);

		this.updateUserDefaultOrg(orgId, userId);
		return 1;
	}

	@Override
	public String findOrgByUserId(String memberId) {
		return organizationMemberMapper.findOrgByUserId(memberId);
	}


	/**
	 * 移交企业权限
	 *
	 * @param orgId 企业id
	 * @param ownerId 企业拥有者id
	 * @param memberId 员工id
	 * @return
	 */
	@Override
	public Boolean transferOwner(String orgId, String ownerId, String memberId){

		try {
			Boolean updateOwner= organizationMemberMapper.updateOwner(orgId,ownerId);
			Boolean updateMember=organizationMemberMapper.updateMember(orgId,memberId);
			Boolean updatOorganization = organizationMapper.updatOorganization(orgId,ownerId,memberId);

			/*//新修改 当用户移交企业权限后，企业用户表的memberlabel更改为成员
			OrganizationMember memberInfo = new OrganizationMember();
			memberInfo.setMemberLabel("成员");
			//生成sql
			organizationMemberService.update(memberInfo,new QueryWrapper<OrganizationMember>()
					.eq("member_id",memberId).eq("organization_id",orgId));
*/
			//将userRole表权限互换
			Boolean updateRoleTransfer=roleUserService.updateRoleTransfer(orgId,ownerId,memberId);
			if (updateOwner && updateMember && updatOorganization && updateRoleTransfer){
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<UserEntity> getOrgMemberByKeyword(String orgId, String keyword) {
		ValidatedUtil.filterNullParam(orgId);

		LambdaQueryWrapper<OrganizationMember> getMemberListQW = new QueryWrapper<OrganizationMember>().lambda()
				.eq(OrganizationMember::getOrganizationId, orgId);

		List<OrganizationMember> orgMemberList = this.list(getMemberListQW);

		if(CollectionUtils.isEmpty(orgMemberList)){
			return new LinkedList<>();
		}

		List<String> orgMemberIdList = orgMemberList.stream().map(OrganizationMember::getMemberId).collect(Collectors.toList());

		LambdaQueryWrapper<UserEntity> getUserListQW = new QueryWrapper<UserEntity>().lambda()
				.in(UserEntity::getUserId, orgMemberIdList).like(UserEntity::getAccountName, keyword);

		return userService.list(getUserListQW);
	}

	@Override
	public void removeMemberByOrgId(String orgId) {
		Optional.ofNullable(orgId).orElseThrow(() -> new ServiceException("删除企业成员信息时,orgId不能为为空."));

		LambdaQueryWrapper<OrganizationMember> eq = new QueryWrapper<OrganizationMember>()
				.lambda().eq(OrganizationMember::getOrganizationId, orgId);

		this.remove(eq);
	}

	@Override
	public Boolean checkUserIdIsOrgMaster(String orgId, String userId) {
		this.orgId = orgId;
		this.userId = userId;
		// 该表示为1 代表企业拥有者
		int label = 1;
		ValidatedUtil.filterNullParam(orgId, userId);

		if(!organizationService.checkOrgIsExist(orgId)){
			throw new ServiceException("企业不存在");
		}

		LambdaQueryWrapper<OrganizationMember> eq = new QueryWrapper<OrganizationMember>().lambda()
				.eq(OrganizationMember::getOrganizationId, orgId)
				.eq(OrganizationMember::getMemberId, userId);

		OrganizationMember organizationMember = this.getOne(eq);

		if(organizationMember == null){
			throw new ServiceException("该用户不在企业中");
		}

		return organizationMember.getOrganizationLable() == label;
	}



}