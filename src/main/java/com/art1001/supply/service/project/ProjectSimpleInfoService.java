package com.art1001.supply.service.project;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectSimpleInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProjectSimpleInfoService extends IService<ProjectSimpleInfo> {

    /**
     * 根据项目中间表的成员id和企业id查询项目信息列表
     * @param memberId
     * @param organizationId
     * @return
     */
    List<Project> getIsExperience(String memberId,String organizationId);

    /**
     * 根据项目中间表的成员id及企业id查询项目中间表信息
     * @param memberId
     * @param organizationId
     * @return
     */
    List<ProjectSimpleInfo> isAdd(String memberId, String organizationId);
}
