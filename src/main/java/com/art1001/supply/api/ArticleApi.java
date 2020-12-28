package com.art1001.supply.api;

import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.article.Article;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.article.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * @param articleTitle    文章标题
     * @param articleContent  文章内容
     * @param coverShow       封面展示 0为不展示 1单图展示 2三图展示
     * @param coverImages     封面展示图片
     * @return
     */
    @GetMapping("/add")
    public Result addArticle(@RequestParam(value = "articleTitle") String articleTitle,
                             @RequestParam(value = "articleContent") String articleContent,
                             @RequestParam(value ="acId" )String acId,
                             @RequestParam(value = "coverShow") Integer coverShow,
                             @RequestParam(value = "coverImages") List<String> coverImages){
        try {
            articleService.addArticle(articleTitle,articleContent,acId,coverShow,coverImages);
            return Result.success("保存成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 修改文章
     * @param articleTitle      文章标题
     * @param articleContent    文章内容
     * @param articleId         文章id
     * @param coverShow         封面展示 0为不展示 1单图展示 2三图展示
     * @param coverImages       封面展示图片
     * @return
     */
    @GetMapping("/edit")
    public Result editArticle(@RequestParam(value = "articleTitle") String articleTitle,
                              @RequestParam(value = "articleContent") String articleContent,
                              @RequestParam(value = "articleId" )String articleId,
                              @RequestParam(value = "coverShow") Integer coverShow,
                              @RequestParam(value = "coverImages") List<String> coverImages){
        try {
            articleService.editArticle(articleTitle,articleContent,articleId,coverShow,coverImages);
            return Result.success("修改成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 关注/取消关注 用户
     * @param memberId   被关注人id
     * @param type       关注状态 0取消关注 1关注
     * @return
     */
    @GetMapping("/attentionUserStatus")
    public Result attentionUserStatus(@RequestParam(value = "memberId") String memberId,
                                      @RequestParam(value = "type") Integer type){
        try {
            articleService.attentionUserStatus(memberId,type);
            return Result.success("已成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    @GetMapping("/list")
    public Result listArticle(){
        try {
            List<Article>list=articleService.listArticle();
            return Result.success(list);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

}
