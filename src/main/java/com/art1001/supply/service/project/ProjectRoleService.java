package com.art1001.supply.service.project;

import com.art1001.supply.entity.project.ProjectRole;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ProjectRoleService extends IService<ProjectRole> {

    void add(String projectRoleName,String projectId);


}
