package com.art1001.supply.service.organization;


import com.art1001.supply.entity.organization.OrganizationMemberInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrganizationMemberInfoService extends IService<OrganizationMemberInfo> {
    /**
    * @Author: 邓凯欣
    * @Email：dengkaixin@art1001.com
    * @Param:
    * @return:
    * @Description: 根据用户id查询企业成员详情
    * @create: 17:16 2020/4/22
    */
    List<OrganizationMemberInfo> findorgMemberInfoByMemberId( String userId,String projectId);

    void updateMembersInfo(OrganizationMemberInfo memberInfo);
}
