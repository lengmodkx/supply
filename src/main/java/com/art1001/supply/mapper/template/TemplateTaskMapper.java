package com.art1001.supply.mapper.template;

import com.art1001.supply.entity.template.TemplateTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TemplateTaskMapper  extends BaseMapper<TemplateTask> {
    List<TemplateTask> findTaskByMenuId(String menuId);
}
