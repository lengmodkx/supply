package com.art1001.supply.service.content;

import com.art1001.supply.entity.content.Article;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticleService extends IService<Article> {

    /**
     * 我关注的
     */
    String FOLLOWING = "FOLLOWING_:";
    /**
     * 我的粉丝
     */
    String FANS = "FANS_:";
    /**
     * 我的互粉
     */
    String MUTUAL_FANS = "MUTUAL_FANS_:";
    /**
     * 我关注的人的文章
     */
    String FOLLOWED_ARTICLE = "FOLLOWED_ARTICLE_:";
    /**
     * 我写的文章
     */
    String MY_ARTICLE = "MY_ARTICLE_:";

    String ARTICLE = "article";

    String COMMENT = "comment";

    String DOCS = "docs";

    String TOPIC_NAME = "topic_name";
    String NICK_NAME = "nick_name";

    Integer ZERO=0;
    Integer ONE= 1;
    Integer TWO= 2;
    Integer THREE= 3;

    /**
     * 保存文章
     *
     * @param articleTitle    文章标题
     * @param articleContent  文章内容
     * @param acId            分类id 1文章 2微头条 3视频
     * @param headlineContent 头条内容
     * @param headlineImages  头条图片
     * @param videoName       视频名称
     * @param videoAddress    视频地址
     * @param videoCover      视频封面
     * @param coverShow       文章封面展示 0为不展示 1单图展示 2三图展示
     * @param coverImages     文章封面展示图片
     * @return
     */
    String addArticle(String articleTitle, String articleContent, String articlePureContent, Integer acId, String headlineContent, List<String> headlineImages, String videoName, List<String> videoAddress, String videoCover, Integer coverShow, List<String> coverImages);

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
    void editArticle(String articleTitle, String articleContent, String articlePureContent, String articleId, String headlineContent, List<String> headlineImages, String videoName, List<String> videoAddress, String videoCover, Integer coverShow, List<String> coverImages);

    /**
     * 关注/取消关注 用户
     *
     * @param memberId 被关注人id
     * @return
     */
    @Transactional
    int attentionUserStatus(String memberId);

    /**
     * 我关注的文章列表
     *
     * @return
     */
    Page<Article> attentionListArticle(Integer pageNum,String acId);

    /**
     * 所有文章列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<Article> allArtile(Integer pageNum, Integer pageSize, String acId);

    /**
     * 所有粉丝
     *
     * @param pageNum
     * @return
     */
    List<UserEntity> allConnectionUser(Integer pageNum, Integer type,String memberId);

    /**
     * 我的文章
     *
     * @param pageNum
     * @return
     */
    Page<Article> myArticle(Integer pageNum, String acId, String keyword, Long startTime, Long endTime,String memberId);

    /**
     * 删除文章
     * @param articleId
     */
    void deleteArticle(String articleId);

    void dateToEs();

    /**
     * 审核文章
     * @param articleId
     * @param state               1审核通过 2审核不通过
     * @param checkFailReason     审核失败原因
     */
    void checkArticle(String articleId,Integer state,String checkFailReason);
}
