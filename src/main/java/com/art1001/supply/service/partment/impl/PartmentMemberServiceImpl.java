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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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
    public PartmentMember getMemberByPartmentId(String partmentId) {
        PartmentMember partmentMember = new PartmentMember();
        partmentMember.setPartmentId(partmentId);
        Partment part = partmentService.findPartmentByPartmentId(partmentId);
        partmentMember.setPartmentName(part.getPartmentName());
        List<OrganizationMember>organizationMembers=organizationMemberService.getMemberByPartmentId(partmentId);
        Optional.ofNullable(organizationMembers).ifPresent(orgs->{
            orgs.stream().forEach(org->{
                org.setUserEntity(userService.findById(org.getMemberId()));
                org.setDeptName(part.getPartmentName());
            });
        });
        partmentMember.setOrganizationMembers(organizationMembers);
        return partmentMember;
    }

    @Override
    public PartmentMember getSimplePartmentMemberInfo(String partmentId, String memberId) {
        return partmentMemberMapper.getSimplePartmentMemberInfo(partmentId,memberId);
    }

    @Override
    public List<PartmentMember> getPartmentByOrgId(String orgId) {
        return partmentMemberMapper.getPartmentByOrgId(orgId);
    }

    @Override
    public List<PartmentMember> getMemberInfoByPartmentId(String partmentId) {
       return partmentMemberMapper.selectList(new QueryWrapper<PartmentMember>().eq("partment_id",partmentId));
    }


}
