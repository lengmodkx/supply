package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.EsRule;
import com.art1001.supply.annotation.EsRuleType;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.article.Article;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.article.ArticleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName ArticleApi
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/24 10:22
 * @Discription 文章api
 */
@RestController
@RequestMapping(value = "article")
public class ArticleApi {

    @Resource
    private ArticleService articleService;


    /**
     * 保存文章
     *
     * @param articleTitle        文章标题
     * @param articleContent      文章内容（无标签）
     * @param articlePureContent  文章内容
     * @param acId                分类id 1文章 2微头条 3视频
     * @param headlineContent     头条内容
     * @param headlineImages      头条图片
     * @param videoName           视频名称
     * @param videoAddress        视频地址
     * @param videoCover          视频封面
     * @param coverShow           文章封面展示 0为不展示 1单图展示 2三图展示
     * @param coverImages         文章封面展示图片
     * @return
     */
    @EsRule(sort = 1, type = EsRuleType.ARTICLE)
    @PostMapping("/add")
    public JSONObject addArticle(@RequestParam(value = "articleTitle", required = false) String articleTitle,
                                 @RequestParam(value = "articleContent", required = false) String articleContent,
                                 @RequestParam(value = "articlePureContent", required = false) String articlePureContent,
                                 @RequestParam(value = "acId") Integer acId,
                                 @RequestParam(value = "headlineContent", required = false) String headlineContent,
                                 @RequestParam(value = "headlineImages", required = false) List<String> headlineImages,
                                 @RequestParam(value = "videoName", required = false) String videoName,
                                 @RequestParam(value = "videoAddress", required = false) List<String> videoAddress,
                                 @RequestParam(value = "videoCover", required = false) String videoCover,
                                 @RequestParam(value = "coverShow", required = false) Integer coverShow,
                                 @RequestParam(value = "coverImages", required = false) List<String> coverImages) {
        try {
            JSONObject jsonObject = new JSONObject();
            String articleId = articleService.addArticle(articleTitle, articleContent, articlePureContent, acId, headlineContent, headlineImages, videoName, videoAddress, videoCover, coverShow, coverImages);
            jsonObject.put("result", 1);
            jsonObject.put("data", articleId);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

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
    @EsRule(sort = 2, type = EsRuleType.ARTICLE)
    @PostMapping("/edit")
    public JSONObject editArticle(@RequestParam(value = "articleTitle", required = false) String articleTitle,
                              @RequestParam(value = "articleContent", required = false) String articleContent,
                              @RequestParam(value = "articlePureContent", required = false) String articlePureContent,
                              @RequestParam(value = "articleId") String articleId,
                              @RequestParam(value = "headlineContent", required = false) String headlineContent,
                              @RequestParam(value = "headlineImages", required = false) List<String> headlineImages,
                              @RequestParam(value = "videoName", required = false) String videoName,
                              @RequestParam(value = "videoAddress", required = false) List<String> videoAddress,
                              @RequestParam(value = "videoCover", required = false) String videoCover,
                              @RequestParam(value = "coverShow", required = false) Integer coverShow,
                              @RequestParam(value = "coverImages", required = false) List<String> coverImages) {
        try {
            JSONObject jsonObject = new JSONObject();
            articleService.editArticle(articleTitle, articleContent, articlePureContent, articleId, headlineContent, headlineImages, videoName, videoAddress, videoCover, coverShow, coverImages);
            jsonObject.put("result",1);
            jsonObject.put("data",articleId);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 关注/取消关注 用户
     *
     * @param memberId 被关注人id
     * @return
     */
    @GetMapping("/attentionUserStatus")
    public Result attentionUserStatus(@RequestParam(value = "memberId") String memberId) {
        try {
            int i = articleService.attentionUserStatus(memberId);
            if (i == 1) {
                return Result.success("已关注");
            } else {
                return Result.success("已取消关注");
            }
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 我的文章
     *
     * @param pageNum
     * @return
     */
    @GetMapping("/myArticle")
    public Result<Page<Article>> myArticle(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                            @RequestParam(value = "acId", required = false) String acId,
                            @RequestParam(value = "keyword",required = false) String keyword,
                            @RequestParam(value = "startTime",required = false) Long startTime,
                            @RequestParam(value = "endTime",required = false) Long endTime) {

        {
            try {
                Page<Article> page = articleService.myArticle(pageNum, acId, keyword, startTime, endTime);
                return Result.success(page);
            } catch (Exception e) {
                throw new AjaxException(e);
            }
        }

    }

    /**
     * 我关注的文章列表
     *
     * @param pageNum
     * @return
     */
    @GetMapping("/attentionListArticle")
    public Result attentionListArticle(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                       @RequestParam(value = "acId", required = false) String acId) {
        try {
            Page<Article> page = articleService.attentionListArticle(pageNum, acId);
            return Result.success(page);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 所有文章
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/allArtile")
    public Result allArtile(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                            @RequestParam(value = "acId", required = false) String acId) {
        try {
            IPage<Article> page = articleService.allArtile(pageNum, pageSize, acId);
            return Result.success(page);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 删除文章
     *
     * @param articleId
     * @return
     */
    @EsRule(sort = 3, type = EsRuleType.ARTICLE)
    @GetMapping("/deleteArticle")
    public JSONObject deleteArticle(@RequestParam(value = "articleId") String articleId) {
        try {
            JSONObject jsonObject = new JSONObject();
            articleService.deleteArticle(articleId);
            jsonObject.put("result",1);
            jsonObject.put("data",articleId);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 所有粉丝/所有关注
     *
     * @param pageNum
     * @param type    1所有粉丝 2所有关注 3互粉
     * @return
     */
    @GetMapping("/allConnectionUser")
    public Result allConnectionUser(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "type") Integer type) {
        try {
            List<UserEntity> page = articleService.allConnectionUser(pageNum, type);
            return Result.success(page);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 将mysql数据存储到es中
     * @return
     */
    @GetMapping("/dateToEs")
    public Result dateToEs(){
        articleService.dateToEs();
        return Result.success();
    }



}
