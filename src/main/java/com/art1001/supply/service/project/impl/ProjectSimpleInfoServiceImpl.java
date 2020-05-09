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
     * 查询项目信息列表
     *
     * @param memberId
     * @return
     */
    @Override
    public List<ProjectSimpleInfo> getIsExperience(String memberId,String organizationId) {
        return projectSimpleInfoMapper.selectList(new QueryWrapper<ProjectSimpleInfo>().eq("member_id", memberId).eq("organization_id",organizationId));

    }


}
