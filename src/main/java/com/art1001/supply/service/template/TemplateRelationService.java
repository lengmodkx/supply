package com.art1001.supply.service.template;

import com.art1001.supply.entity.template.TemplateRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TemplateRelationService extends IService<TemplateRelation> {

    List<TemplateRelation> getRelation(String templateId);

    void createRelation(TemplateRelation relation);
}
