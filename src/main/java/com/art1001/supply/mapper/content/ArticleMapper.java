package com.art1001.supply.mapper.content;

import com.art1001.supply.entity.content.Article;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {



    /**
     * 按类别分页查询文章列表
     * @param page
     * @param acId
     * @return
     */
    List<Article> selectByExample(IPage<Article> page, @Param("acId") String acId,@Param("state")Integer state);

    /**
     * 根据文章名称模糊查询文章列表
     * @param page
     * @param queryWrapper
     * @return
     */
    Page<Article> selectArticlePage(IPage<Article> page, @Param("ew")QueryWrapper queryWrapper);

    /**
     * 查询评论数量
     * @param articleId
     * @return
     */
    Integer selectCommentCount(@Param("articleId")String articleId);
}
