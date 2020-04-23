package com.art1001.supply.service.organization.impl;


import com.art1001.supply.entity.organization.OrganizationMemberInfo;
import com.art1001.supply.mapper.organization.OrganizationMemberInfoMapper;
import com.art1001.supply.service.organization.OrganizationMemberInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName OrganizationMemberInfoServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/4/22 15:19
 * @Discription 企业用户详情实现类
 */
@Service
public class OrganizationMemberInfoServiceImpl extends ServiceImpl<OrganizationMemberInfoMapper, OrganizationMemberInfo> implements OrganizationMemberInfoService {

    @Resource
    private OrganizationMemberInfoMapper organizationMemberInfoMapper;

    @Override
    public OrganizationMemberInfo findorgMemberInfoByMemberId(String userId) {
        return organizationMemberInfoMapper.findorgMemberInfoByMemberId(userId);
    }

    @Override
    public void updateMembersInfo(OrganizationMemberInfo memberInfo) {
        organizationMemberInfoMapper.updateMembersInfo(memberInfo);
    }
}
