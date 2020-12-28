package com.art1001.supply.service.article;

import com.art1001.supply.entity.article.Article;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ArticleService extends IService<Article> {

    /**
     * 保存文章
     * @param articleTitle    文章标题
     * @param articleContent  文章内容
     * @param coverShow       封面展示 0为不展示 1单图展示 2三图展示
     * @param coverImages     封面展示图片
     * @return
     */
    Integer addArticle(String articleTitle, String articleContent,String acId, Integer coverShow, List<String> coverImages);

    /**
     * 修改文章
     * @param articleTitle      文章标题
     * @param articleContent    文章内容
     * @param articleId         文章id
     * @param coverShow         封面展示 0为不展示 1单图展示 2三图展示
     * @param coverImages       封面展示图片
     * @return
     */
    Integer editArticle(String articleTitle, String articleContent, String articleId, Integer coverShow, List<String> coverImages);

    /**
     * 关注/取消关注 用户
     * @param memberId   被关注人id
     * @param type       关注状态 0取消关注 1关注
     * @return
     */
    Integer attentionUserStatus(String memberId, Integer type);

    /**
     * 获取文章列表
     * @return
     */
    List<Article> listArticle();
}
