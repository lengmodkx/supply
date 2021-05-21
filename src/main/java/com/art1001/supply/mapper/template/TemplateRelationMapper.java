package com.art1001.supply.mapper.template;

import com.art1001.supply.entity.template.TemplateRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TemplateRelationMapper extends BaseMapper<TemplateRelation> {
    List<TemplateRelation> getRelation(String templateId);

    Integer findMaxOrder(@Param("templateId") String templateId);
}
