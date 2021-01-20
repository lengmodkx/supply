package com.art1001.supply.mapper.content;

import com.art1001.supply.entity.content.Comment;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 分页查询评论列表
     * @param page
     * @param queryWrapper
     * @return
     */
    Page<Comment> listCommentByPage(Page<Comment> page, @Param("ew") QueryWrapper queryWrapper);


}
