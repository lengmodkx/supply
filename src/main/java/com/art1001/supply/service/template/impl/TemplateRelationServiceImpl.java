package com.art1001.supply.service.template.impl;

import com.art1001.supply.entity.template.TemplateRelation;
import com.art1001.supply.mapper.template.TemplateRelationMapper;
import com.art1001.supply.service.template.TemplateRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TemplateRelationServiceImpl extends ServiceImpl<TemplateRelationMapper, TemplateRelation> implements TemplateRelationService {
    @Resource
    TemplateRelationMapper templateRelationMapper;

    @Override
    public List<TemplateRelation> getRelation(String templateId) {
        return templateRelationMapper.getRelation(templateId);
    }
}
