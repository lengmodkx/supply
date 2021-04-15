package com.art1001.supply.service.project.impl;

import com.art1001.supply.entity.project.ProjectRole;
import com.art1001.supply.mapper.project.ProjectRoleMapper;
import com.art1001.supply.service.project.ProjectRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @ClassName ProjectRoleServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/4/15 16:28
 * @Discription
 */
@Service
public class ProjectRoleServiceImpl extends ServiceImpl<ProjectRoleMapper, ProjectRole> implements ProjectRoleService {
    @Override
    public void add(String projectRoleName,String projectId) {
        ProjectRole projectRole = new ProjectRole();
        projectRole.setProjectRoleName(projectRoleName);
        Date date = Date.from(LocalDateTime.now().atZone(ZoneId.of("CTT")).toInstant());
        projectRole.setCreateTime(date);
        projectRole.setUpdateTime(date);
        projectRole.setProjectId(projectId);
        save(projectRole);
    }


}
