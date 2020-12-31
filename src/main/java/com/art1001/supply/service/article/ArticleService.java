package com.art1001.supply.service.article;

import com.art1001.supply.entity.article.Article;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ArticleService extends IService<Article> {

    /**
     * 保存文章
     * @param articleTitle      文章标题
     * @param articleContent    文章内容
     * @param acId              分类id 1文章 2微头条 3视频
     * @param headlineContent   头条内容
     * @param headlineImages    头条图片
     * @param videoName         视频名称
     * @param videoAddress      视频地址
     * @param videoCover        视频封面
     * @param coverShow         文章封面展示 0为不展示 1单图展示 2三图展示
     * @param coverImages       文章封面展示图片
     * @return
     */
    void addArticle(String articleTitle, String articleContent,String articlePureContent,Integer acId,String headlineContent,List<String> headlineImages,String videoName,List<String> videoAddress,String videoCover, Integer coverShow, List<String> coverImages);

    /**
     * 修改文章
     *
     * @param articleTitle    文章标题
     * @param articleContent  文章内容
     * @param articleId       要修改的内容id
     * @param headlineContent 头条内容
     * @param headlineImages  头条图片
     * @param videoName       视频名称
     * @param videoAddress    视频地址
     * @param videoCover      视频封面
     * @param coverShow       文章封面展示 0为不展示 1单图展示 2三图展示
     * @param coverImages     文章封面展示图片
     * @return
     */
    void editArticle(String articleTitle, String articleContent, String articlePureContent,String articleId, String headlineContent, List<String> headlineImages, String videoName, List<String> videoAddress, String videoCover, Integer coverShow, List<String> coverImages);

    /**
     * 关注/取消关注 用户
     * @param memberId   被关注人id
     * @return
     */
    int attentionUserStatus(String memberId);

    /**
     * 我关注的文章列表
     * @return
     */
    IPage<Article> attentionListArticle(Integer pageNum, Integer pageSize,String acId);

    /**
     * 所有文章列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<Article> allArtile(Integer pageNum, Integer pageSize,String acId);

    /**
     * 所有粉丝
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<UserEntity> allConnectionUser(Integer pageNum, Integer pageSize,Integer type);

    /**
     * 我的文章
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<Article> myArticle(Integer pageNum, Integer pageSize);
}
