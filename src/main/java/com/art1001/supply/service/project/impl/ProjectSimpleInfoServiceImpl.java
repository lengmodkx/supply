package com.art1001.supply.service.project.impl;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectSimpleInfo;
import com.art1001.supply.mapper.project.ProjectSimpleInfoMapper;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.project.ProjectSimpleInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName ProjectSimpleInfoServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/5/7 13:38
 * @Discription
 */
@Service
public class ProjectSimpleInfoServiceImpl extends ServiceImpl<ProjectSimpleInfoMapper, ProjectSimpleInfo> implements ProjectSimpleInfoService {

    @Resource
    private ProjectSimpleInfoMapper projectSimpleInfoMapper;

    @Resource
    private ProjectService projectService;

    /**
     * 根据项目中间表的成员id和企业id查询项目信息列表
     * prm_project_simple_info 表
     * @param memberId
     * @return
     */
    @Override
    public List<Project> getIsExperience(String memberId,String organizationId) {
        List<Project>list=Lists.newArrayList();
        //使用项目中间表查处的项目id查询项目信息，并表示已添加
        projectSimpleInfoMapper.selectList(new QueryWrapper<ProjectSimpleInfo>().eq("member_id",memberId).eq("organization_id",organizationId)).forEach(r->{
            Project projectByProjectId = projectService.findProjectByProjectId(r.getProjectId());
            projectByProjectId.setIsAdd(1);
            list.add(projectByProjectId);
        });
        return list;
    }

    /**
     * 根据项目中间表的成员id及企业id查询项目中间表信息
     * @param memberId
     * @param organizationId
     * @return
     */
    @Override
    public List<ProjectSimpleInfo> isAdd(String memberId, String organizationId) {
       return projectSimpleInfoMapper.selectList(new QueryWrapper<ProjectSimpleInfo>().eq("member_id",memberId).eq("organization_id",organizationId));
    }


}
