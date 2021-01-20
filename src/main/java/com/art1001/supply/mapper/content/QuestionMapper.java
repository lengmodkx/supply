package com.art1001.supply.mapper.content;

import com.art1001.supply.entity.content.Question;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    Page<Question> listByPage(Page<Question> page,  @Param("ew")QueryWrapper<Question> query);
}
