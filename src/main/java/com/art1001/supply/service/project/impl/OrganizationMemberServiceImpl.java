package com.art1001.supply.service.project.impl;

import com.alibaba.druid.sql.visitor.functions.If;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.InvitationLink;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.organization.OrganizationMemberInfo;
import com.art1001.supply.entity.partment.PartmentMember;
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
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

/**
 * projectServiceImpl
 */
@Service
public class OrganizationMemberServiceImpl extends ServiceImpl<OrganizationMemberMapper, OrganizationMember> implements OrganizationMemberService {

    /**
     * projectMapper接口
     */
    @Resource
    private OrganizationMemberMapper organizationMemberMapper;

    /**
     * organizationMapper接口
     */
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
     *
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
    public List<OrganizationMember> findOrganizationMemberPagerList(Pager pager) {
        return organizationMemberMapper.findOrganizationMemberPagerList(pager);
    }

    /**
     * 通过id获取单条project数据
     *
     * @param id
     * @return
     */
    @Override
    public OrganizationMember findOrganizationMemberById(String id) {
        return organizationMemberMapper.findOrganizationMemberById(id);
    }

    /**
     * 通过id删除project数据
     *
     * @param id
     */
    @Override
    public void deleteOrganizationMemberById(String id) {
        organizationMemberMapper.deleteOrganizationMemberById(id);
    }

    /**
     * 修改project数据
     *
     * @param organizationMember
     */
    @Override
    public void updateOrganizationMember(OrganizationMember organizationMember) {
        organizationMemberMapper.updateOrganizationMember(organizationMember);
    }

    /**
     * 保存project数据
     *
     * @param organizationMember
     */
    @Override
    public void saveOrganizationMember(OrganizationMember organizationMember) {
        OrganizationMember organizationMember1 = getOne(new QueryWrapper<OrganizationMember>().eq("member_id", organizationMember.getMemberId()).eq("organization_id", organizationMember.getOrganizationId()));
        if (organizationMember1 == null) {
            organizationMember.setCreateTime(currentTimeMillis());
            organizationMember.setUpdateTime(currentTimeMillis());
            organizationMemberMapper.insert(organizationMember);
            String orgId = organizationMemberService.findOrgByUserId(organizationMember.getMemberId());
            if (StringUtils.isEmpty(orgId)) {
                organizationMemberService.updateUserDefaultOrg(organizationMember.getOrganizationId(), organizationMember.getMemberId());
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
    public List<OrganizationMember> findOrganizationMemberAllList(OrganizationMember organizationMember) {
        return organizationMemberMapper.findOrganizationMemberAllList(organizationMember);
    }

    @Override
    public OrganizationMember findOrgByMemberId(String memberId, String orgId) {
        return organizationMemberMapper.findOrgByMemberId(memberId, orgId);
    }

    /**
     * 获取用户已经加入的企业数量
     *
     * @return 加入的企业数量
     */
    @Override
    public int userOrgCount() {
        return organizationMemberMapper.selectCount(new QueryWrapper<OrganizationMember>().eq("member_id", ShiroAuthenticationManager.getUserId()));
    }

    /**
     * 修改一个用户的默认企业
     *
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

        organizationMemberMapper.update(organizationMember, clear);
        organizationMember.setUserDefault(true);
        organizationMemberMapper.update(organizationMember, sign);
        return 1;
    }

    /**
     * 保存企业的创建人信息
     *
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
     * @param orgId    企业id
     * @param ownerId  企业拥有者id
     * @param memberId 员工id
     * @return
     */
    @Override
    public Boolean transferOwner(String orgId, String ownerId, String memberId) {

        try {
            Boolean updateOwner = organizationMemberMapper.updateOwner(orgId, ownerId);
            Boolean updateMember = organizationMemberMapper.updateMember(orgId, memberId);
            Boolean updatOorganization = organizationMapper.updatOorganization(orgId, ownerId, memberId);

			/*//新修改 当用户移交企业权限后，企业用户表的memberlabel更改为成员
			OrganizationMember memberInfo = new OrganizationMember();
			memberInfo.setMemberLabel("成员");
			//生成sql
			organizationMemberService.update(memberInfo,new QueryWrapper<OrganizationMember>()
					.eq("member_id",memberId).eq("organization_id",orgId));
*/
            //将userRole表权限互换
            Boolean updateRoleTransfer = roleUserService.updateRoleTransfer(orgId, ownerId, memberId);
            if (updateOwner && updateMember && updatOorganization && updateRoleTransfer) {
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

        if (CollectionUtils.isEmpty(orgMemberList)) {
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

        if (!organizationService.checkOrgIsExist(orgId)) {
            throw new ServiceException("企业不存在");
        }

        LambdaQueryWrapper<OrganizationMember> eq = new QueryWrapper<OrganizationMember>().lambda()
                .eq(OrganizationMember::getOrganizationId, orgId)
                .eq(OrganizationMember::getMemberId, userId);

        OrganizationMember organizationMember = this.getOne(eq);

        if (organizationMember == null) {
            throw new ServiceException("该用户不在企业中");
        }

        return organizationMember.getOrganizationLable() == label;
    }

    /**
     * 查询成员是否存在于项目中
     *
     * @param orgId
     * @param memberId
     * @return
     */
    @Override
    public int findOrgMemberIsExist(String orgId, String memberId) {
        return organizationMemberMapper.findMemberIsExist(orgId, memberId);
    }

    @Override
    public List<OrganizationMember> getMembersAndPartment(String orgId, Integer flag) {
        List<OrganizationMember> orgMembers = organizationMemberMapper.selectList(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).eq("other", flag));
        setParams(orgMembers);
        return orgMembers;
    }


    /**
     * 获取企业下部门成员信息
     *
     * @param orgId
     * @return
     */
    @Override
    public List<PartmentMember> getOrgPartment(String orgId) {
        Integer integer = organizationMapper.selectCount(new QueryWrapper<Organization>().eq("organization_id", orgId).eq("organization_member", ShiroAuthenticationManager.getUserId()));
        List<PartmentMember> orgParentByOrgId = Lists.newArrayList();
        List<OrganizationMember> organizationMembers = Lists.newArrayList();
        Organization org = organizationMapper.selectOne(new QueryWrapper<Organization>().eq("organization_id", orgId));
        if (integer != 0) {
            orgParentByOrgId = partmentMemberService.getPartmentByOrgId(orgId);
            if (CollectionUtils.isNotEmpty(orgParentByOrgId)) {
                for (PartmentMember partment : orgParentByOrgId) {
                    organizationMembers = organizationMemberMapper.selectList(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).eq("partment_id", partment.getPartmentId()));
                    setParams(organizationMembers);
                    partment.setOrganizationName(org.getOrganizationName());
                    partment.setOrganizationMembers(organizationMembers);
                }
            }
        }
        return orgParentByOrgId;
    }

    @Override
    public List<OrganizationMember> getOrgPartmentByMemberLebel(String partmentId, String memberLebel, String flag, String orgId) {
        List<OrganizationMember> orgs = Lists.newArrayList();
        List<PartmentMember> partmentMembers = Lists.newArrayList();
        OrganizationMember org = new OrganizationMember();
        if (StringUtils.isNotEmpty(partmentId)) {
            partmentMembers = partmentMemberService.getMemberInfoByPartmentId(partmentId);
            if (CollectionUtils.isNotEmpty(partmentMembers)) {
                for (PartmentMember partmentMember : partmentMembers) {
                    org = organizationMemberMapper.selectOne(new QueryWrapper<OrganizationMember>()
                            .eq("member_id", partmentMember.getMemberId())
                            .eq("partment_id", partmentId)
                            .eq("other", flag));
                    if (org != null) {
                        org.setUserEntity(userService.findById(partmentMember.getMemberId()));
                        if (StringUtils.isEmpty(memberLebel)) {
                            orgs.add(org);
                        }
                        OrganizationMember orgsResult = getOrgsResult(memberLebel, org);
                        if (orgsResult != null) {
                            orgs.add(orgsResult);
                        }
                    }
                }
            }
        } else {
            if (StringUtils.isEmpty(memberLebel)) {
                orgs = organizationMemberMapper.selectList(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId));
                orgs.stream().forEach(r -> r.setUserEntity(userService.findById(r.getMemberId())));
            }
            orgs = organizationMemberMapper.selectList(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId));
            orgs.stream().forEach(r -> r.setUserEntity(userService.findById(r.getMemberId())));
            Optional.ofNullable(orgs).ifPresent(os -> {
                if (StringUtils.isNotEmpty(memberLebel)) {
                    if (Constants.ONE.equals(memberLebel)) os.stream().filter(o -> o.getMemberLabel() != null && o.getMemberLabel().equals(Constants.MEMBER_CN)).collect(Collectors.toList());
                    else if (Constants.TWO.equals(memberLebel)) os.stream().filter(o -> o.getMemberLabel() != null && o.getMemberLabel().equals(Constants.OWNER_CN)).collect(Collectors.toList());
                    else if (Constants.THREE.equals(memberLebel)) os.stream().filter(o -> o.getMemberLabel() != null && o.getMemberLabel().equals(Constants.ADMIN_CN)).collect(Collectors.toList());
                }
            });
        }
        return orgs;
    }

    /**
     * 获取筛选结果
     *
     * @param memberLebel
     * @param org
     */
    private OrganizationMember getOrgsResult(String memberLebel, OrganizationMember org) {

        if (Constants.ONE.equals(memberLebel)) {
            if (org.getMemberLabel() != null && org.getMemberLabel().equals(Constants.MEMBER_CN)) {
                return org;
            }
        } else if (Constants.TWO.equals(memberLebel)) {
            if (org.getMemberLabel() != null && org.getMemberLabel().equals(Constants.OWNER_CN)) {
                return org;
            }
        } else {
            if (org.getMemberLabel() != null && org.getMemberLabel().equals(Constants.ADMIN_CN)) {
                return org;
            }
        }
        return null;
    }

    /**
     * 根据电话号邀请企业成员
     *
     * @param orgId
     * @param phone
     * @return
     */
    @Override
    public List<String> inviteOrgMemberByPhone(String orgId, List<String> phone, List<String> memberEmail) {
        List<String> results = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(phone)) {
            for (String s : phone) {
                String result;
                UserEntity userEntity = userService.selectUserByPhone(s);
                Integer integer = organizationMemberMapper.selectCount(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).eq("member_id", userEntity.getUserId()).eq("partment_id", "0"));
                if (integer == 0) {
                    result = saveOrganizationMemberInfo(orgId, userEntity);
                } else {
                    result = "该成员已在企业中";
                }
                results.add(result);
            }
        }
        if (CollectionUtils.isNotEmpty(memberEmail)) {
            for (String s : memberEmail) {
                String result;
                UserEntity userEntity = userService.selectUserByEmail(s);
                Integer integer = organizationMemberMapper.selectCount(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).eq("member_id", userEntity.getUserId()).eq("partment_id", "0"));
                if (integer == 0) {
                    result = saveOrganizationMemberInfo(orgId, userEntity);
                } else {
                    result = "该成员已在企业中";
                }
                results.add(result);
            }
        }
        return results;
    }

    @Override
    public List<OrganizationMember> getMemberByPartmentId(String partmentId) {
        List<OrganizationMember> partment_id = organizationMemberMapper.selectList(new QueryWrapper<OrganizationMember>().eq("partment_id", partmentId));
        return partment_id;
    }

    @Override
    public OrganizationMember findOrgMembersByUserId(String userId, String orgId) {
        return organizationMemberMapper.selectOne(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).eq("member_id", userId));
    }

    /**
     * 查询企业外部成员
     *
     * @param memberId
     * @param orgId
     * @return
     */
    @Override
    public Integer findOrgOtherByMemberId(String memberId, String orgId) {
        return organizationMemberMapper.selectCount(new QueryWrapper<OrganizationMember>().eq("member_id", memberId).eq("organization_id", orgId).eq("other", "0"));
    }

    @Override
    public void saveOrganizationMember2(String orgId, UserEntity userEntity) {
        saveOrganizationMemberInfo(orgId, userEntity);
    }

    /**
     * 根据部门成员id查询企业成员
     *
     * @param ids
     * @return
     */
    @Override
    public List<OrganizationMember> getMemberByPartmentIds(List<String> ids, String orgId) {
        return organizationMemberMapper.selectList(new QueryWrapper<OrganizationMember>().in("member_id", ids).eq("organization_id", orgId));
    }

    /**
     * 保存企业成员信息
     *
     * @param orgId
     * @param userEntity
     */
    private String saveOrganizationMemberInfo(String orgId, UserEntity userEntity) {
        OrganizationMember organizationMember = new OrganizationMember();
        organizationMember.setMemberEmail(userEntity.getEmail());
        organizationMember.setCreateTime(System.currentTimeMillis());
        organizationMember.setImage(userEntity.getImage());
        organizationMember.setJob(userEntity.getJob());
        organizationMember.setMemberId(userEntity.getUserId());
        organizationMember.setOrganizationId(orgId);
        organizationMember.setPhone(userEntity.getAccountName());
        organizationMember.setUpdateTime(System.currentTimeMillis());
        organizationMember.setUserName(userEntity.getUserName());
        organizationMember.setAddress(userEntity.getAddress());
        organizationMember.setPartmentId("0");
        if (userEntity.getBirthday() != null) {
            Instant instant = userEntity.getBirthday().toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            instant.atZone(zoneId).toLocalDate();
            organizationMember.setBirthday(instant.atZone(zoneId).toLocalDate() + "");
        }
        organizationMember.setMemberLock(1);
        organizationMember.setOther(1);
        organizationMember.setMemberLabel("外部成员");
        organizationMember.setUserDefault(true);
        organizationMember.setPartmentId("0");
        this.save(organizationMember);
        RoleUser roleUser = new RoleUser();
        roleUser.setOrgId(orgId);
        roleUser.setUId(userEntity.getUserId());
        roleUser.setTCreateTime(LocalDateTime.now());
        Role one = roleService.getOne(new QueryWrapper<Role>().eq("organization_id", orgId).eq("role_name", "外部成员"));
        roleUser.setRoleId(one.getRoleId());
        roleUserService.save(roleUser);
        return "邀请成功";
    }

    /**
     * @param orgMembers
     */
    private void setParams(List<OrganizationMember> orgMembers) {
        String partmentName = "";
        if (CollectionUtils.isNotEmpty(orgMembers)) {
            for (OrganizationMember r : orgMembers) {
                UserEntity byId = userService.findById(r.getMemberId());
                r.setJob(byId.getJob());
                r.setImage(byId.getImage());
                r.setMemberEmail(byId.getEmail());
                r.setUserName(byId.getUserName());
                r.setPhone(byId.getAccountName());
                r.setUserEntity(byId);
                if (StringUtils.isEmpty(r.getBirthday())) {
                    r.setBirthday(byId.getBirthday() + "");
                }
                if (StringUtils.isNotEmpty(r.getEntryTime())) {
                    LocalDate todayDate = LocalDate.now();
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate date = LocalDate.parse(r.getEntryTime(), fmt);
                    if (date.until(todayDate, ChronoUnit.DAYS) < 30) {
                        r.setStayComDate("刚刚入职");
                    } else if (date.until(todayDate, ChronoUnit.YEARS) > 1) {
                        r.setStayComDate(date.until(todayDate, ChronoUnit.YEARS) + "年");
                    } else {
                        r.setStayComDate("不满一年");
                    }
                }
                PartmentMember partmentMember = partmentMemberService.getSimplePartmentMemberInfo(r.getPartmentId(), r.getMemberId());
                if (partmentMember != null) {
                    r.setDeptName(partmentMember.getPartmentName());
                    if (partmentMember.getIsMaster()) {
                        partmentName = partmentMember.getPartmentName();
                    }
                }
                if (!"".equals(partmentName)) {
                    r.setParentName(partmentName);
                }
            }
        }
    }
}