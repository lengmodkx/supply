package com.art1001.supply.service.project;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectSimpleInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProjectSimpleInfoService extends IService<ProjectSimpleInfo> {

    List<Project> getIsExperience(String memberId,String organizationId);

    List<ProjectSimpleInfo> isAdd(String memberId, String organizationId);
}
