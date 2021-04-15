package com.art1001.supply.service.partment.impl;

import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.partment.PartmentMember;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.partment.PartmentMemberMapper;
import com.art1001.supply.service.partment.PartmentMemberService;
import com.art1001.supply.service.partment.PartmentService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shaohua
 * @since 2019-04-29
 */
@Service
public class PartmentMemberServiceImpl extends ServiceImpl<PartmentMemberMapper, PartmentMember> implements PartmentMemberService {

    @Resource
    private PartmentMemberMapper partmentMemberMapper;

    @Resource
    private PartmentService partmentService;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private UserService userService;

    /**
     * 添加部门成员
     * @param partmentMember 成员信息
     * @return 是否成功
     */
    @Override
    public Boolean addPartmentMember(PartmentMember partmentMember) {
        if(!partmentService.checkPartmentIsExist(partmentMember.getPartmentId())){
            throw new ServiceException("该部门不存在!");
        }
        //构造出部门成员是否存在的

        if(this.checkMemberIsExist(partmentMember.getPartmentId(), partmentMember.getMemberId())){
            throw new ServiceException("部门中已经存在该成员,不能重复添加!");
        }
        partmentMember.setCreateTime(System.currentTimeMillis());
        partmentMember.setUpdateTime(System.currentTimeMillis());
        return partmentMemberMapper.insert(partmentMember) > 0;
    }


    /**
     * 部门中是否已经存在了该成员
     * 存在返回true
     * @param partmentId 部门id
     * @param memberId 成员id
     * @return 结果
     */
    @Override
    public Boolean checkMemberIsExist(String partmentId, String memberId) {
        return partmentMemberMapper.selectCount(new QueryWrapper<PartmentMember>().eq("partment_id", partmentId).eq("member_id", memberId)) > 0;
    }
    /**
     * 获取一个部门的全部成员信息
     * @param partmentId 部门id
     * @return 成员信息
     */
    @Override
    public List<PartmentMember> getMemberByPartmentId(String partmentId) {

        List<PartmentMember> partmentMembers = partmentMemberMapper.selectList(new QueryWrapper<PartmentMember>().eq("partment_id", partmentId));

        Partment partment = partmentService.findPartmentByPartmentId(partmentId);

        if (CollectionUtils.isNotEmpty(partmentMembers)) {
            partmentMembers.stream().forEach(p->{
                OrganizationMember one = organizationMemberService.getOne(new QueryWrapper<OrganizationMember>().eq("organization_id", partment.getOrganizationId()).eq("member_id", p.getMemberId()));
                one.setUserEntity(userService.findById(p.getMemberId()));
                p.setOrganizationMember(one);
                p.setPartmentName(partment.getPartmentName());
            });
        }

        return partmentMembers;

    }

    @Override
    public String getSimplePartmentMemberInfo(String partmentId) {
        return partmentMemberMapper.getSimplePartmentMemberInfo(partmentId);
    }

    @Override
    public List<PartmentMember> getPartmentByOrgId(String orgId) {
        return partmentMemberMapper.getPartmentByOrgId(orgId);
    }

    @Override
    public List<PartmentMember> getMemberInfoByPartmentId(String partmentId) {
       return partmentMemberMapper.selectList(new QueryWrapper<PartmentMember>().eq("partment_id",partmentId));
    }

    @Override
    public PartmentMember getpartmentMemberByOrgId(String orgId, String memberId) {
        return partmentMemberMapper.getpartmentMemberByOrgId(orgId,memberId);
    }

    @Override
    public Integer addDeptMember(String partmentId, String orgId, List<String> memberId) {

        List<OrganizationMember> list = organizationMemberService.list(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).in("member_id", memberId));
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().forEach(r->{
                PartmentMember partmentMember = new PartmentMember();
                partmentMember.setPartmentId(partmentId);
                partmentMember.setMemberId(r.getMemberId());

                Integer integer = partmentMemberMapper.selectCount(new QueryWrapper<PartmentMember>().eq("partment_id", partmentId).eq("member_id", r.getMemberId()));
                if (integer>0) {
                    partmentMember.setUpdateTime(System.currentTimeMillis());
                    partmentMemberMapper.update(partmentMember,new QueryWrapper<PartmentMember>().eq("partment_id", partmentId).eq("member_id", r.getMemberId()));
                }else {
                    partmentMember.setCreateTime(System.currentTimeMillis());
                    partmentMember.setUpdateTime(System.currentTimeMillis());
                    partmentMemberMapper.insert(partmentMember);
                }
                OrganizationMember member = new OrganizationMember();

                member.setPartmentId(partmentId);
                organizationMemberService.update(member,new UpdateWrapper<OrganizationMember>().eq("organization_id",orgId).eq("member_id",r.getMemberId()));
            });
        }
        return 1;
    }

    @Override
    public void savePartmentMember(String partmentId, String memberId) {
        PartmentMember partmentMember = new PartmentMember();
        PartmentMember partmentMember1 = new PartmentMember();

        partmentMember.setPartmentId(partmentId);
        partmentMember.setMemberId(ShiroAuthenticationManager.getUserId());
        partmentMember.setIsMaster(true);
        partmentMember.setMemberLabel(2);
        partmentMember.setCreateTime(System.currentTimeMillis());
        partmentMember.setUpdateTime(System.currentTimeMillis());
        partmentMember.setMemberType("拥有者");

        if (StringUtils.isNotEmpty(memberId)) {
            partmentMember.setIsMaster(false);

            partmentMember1.setPartmentId(partmentId);
            partmentMember1.setMemberId(memberId);
            partmentMember1.setIsMaster(true);
            partmentMember1.setMemberLabel(3);
            partmentMember1.setCreateTime(System.currentTimeMillis());
            partmentMember1.setUpdateTime(System.currentTimeMillis());
            partmentMember1.setMemberType("管理员");
            partmentMemberMapper.insert(partmentMember1);
        }
        partmentMemberMapper.insert(partmentMember);
    }

    @Override
    public void updatePartMentMaster(String partmentId, String memberId) {
        Integer integer = partmentMemberMapper.selectCount(new QueryWrapper<PartmentMember>().eq("partment_id", partmentId).eq("member_id", memberId));
        if (integer==0) {
            PartmentMember partmentMember = new PartmentMember();
            partmentMember.setPartmentId(partmentId);
            partmentMember.setMemberId(memberId);
            partmentMember.setIsMaster(true);
            partmentMember.setMemberLabel(3);
            partmentMember.setCreateTime(System.currentTimeMillis());
            partmentMember.setUpdateTime(System.currentTimeMillis());
            partmentMember.setMemberType("管理员");
            partmentMemberMapper.insert(partmentMember);
        }else{
            partmentMemberMapper.updatePartMentMaster(partmentId,memberId);
        }
    }

    @Override
    public Integer countPartMentMember(String orgId, List<String> memberId) {
        return partmentMemberMapper.countPartMentMember(orgId,memberId);
    }

    @Override
    public void removePartmentMember(String orgId, List<String> memberId) {
        List<Partment> list = partmentService.list(new QueryWrapper<Partment>().eq("organization_id", orgId));
        List<String> partmentIds=Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {
             partmentIds = list.stream().map(Partment::getPartmentId).collect(Collectors.toList());
        }
        partmentMemberMapper.delete(new QueryWrapper<PartmentMember>().in("partment_id",partmentIds).in("member_id",memberId));
    }


}
