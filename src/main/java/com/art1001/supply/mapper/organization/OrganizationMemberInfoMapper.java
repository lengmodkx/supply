package com.art1001.supply.mapper.organization;

import com.art1001.supply.entity.organization.OrganizationMemberInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    List<OrganizationMemberInfo> findorgMemberInfoByMemberId(@Param("userId") String userId,@Param("projectId") String projectId);

    void updateMembersInfo(OrganizationMemberInfo memberInfo);
}
