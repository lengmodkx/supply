package com.art1001.supply.mapper.content;

import com.art1001.supply.entity.content.Reply;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @ClassName ReplyMapper
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/13 10:23
 * @Discription
 */
@Mapper
public interface ReplyMapper extends BaseMapper<Reply> {
    Page<Reply> queryByExample(Page<Reply> page,@Param("ew") QueryWrapper<Reply> query);
}
