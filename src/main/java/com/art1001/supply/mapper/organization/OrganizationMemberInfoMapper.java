package com.art1001.supply.mapper.organization;

import com.art1001.supply.entity.organization.OrganizationMemberInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrganizationMemberInfoMapper extends BaseMapper<OrganizationMemberInfo> {

    /**
    * @Author: 邓凯欣
    * @Email：dengkaixin@art1001.com
    * @Param:
    * @return:
    * @Description: 查询企业成员详情
    * @create: 17:55 2020/4/22
    */
    OrganizationMemberInfo findorgMemberInfoByMemberId(String userId);
}
