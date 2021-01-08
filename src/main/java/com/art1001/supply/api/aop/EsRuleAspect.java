package com.art1001.supply.api.aop;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.EsRule;
import com.art1001.supply.entity.article.Article;
import com.art1001.supply.entity.article.Comment;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.service.article.ArticleService;
import com.art1001.supply.service.article.CommentService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.util.EsUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.elasticsearch.client.RestHighLevelClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName EsRuleAspect
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/6 10:37
 * @Discription
 */
@Slf4j
@Aspect
@Component
public class EsRuleAspect {

    @Resource
    private RestHighLevelClient esClient;

    @Resource
    private ArticleService articleService;

    @Resource
    private CommentService commentService;

    @Resource
    private UserService userService;

    @Resource
    private EsUtil esUtil;

    private final static String ARTICLE = "article";

    private final static String COMMENT = "comment";

    private final static String DOCS = "docs";

    private final static int ONE = 1;

    private final static int TWO = 2;

    private final static int THREE = 3;

    /**
     * 推送的切点
     */
    @Pointcut("@annotation(com.art1001.supply.annotation.EsRule)")
    public void push() {
    }

    @AfterReturning(returning = "jsonObject", pointcut = "push()")
    public void after(JoinPoint joinPoint, JSONObject jsonObject) {
        EsRule esRule = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(EsRule.class);
        // 保存
        if (esRule.sort() == ONE) {
            this.save(esRule, jsonObject);
        }
        if (esRule.sort() == TWO) {
            this.update(esRule, jsonObject);
        }
        if (esRule.sort() == THREE) {
            this.delete(esRule, jsonObject);
        }

    }

    private void delete(EsRule esRule, JSONObject jsonObject) {
        if (esRule.type().getName().equals(ARTICLE)) {
            esUtil.delete(ARTICLE,DOCS,"articleId",jsonObject.get("data").toString());
        }
        if (esRule.type().getName().equals(COMMENT)) {
            esUtil.delete(COMMENT,DOCS,"commentId",jsonObject.get("data").toString());
        }
    }


    private void update(EsRule esRule, JSONObject jsonObject) {
        if (esRule.type().getName().equals(ARTICLE)) {
            Article article = getArticle(jsonObject);
            esUtil.update(ARTICLE, DOCS, "articleId", jsonObject.get("data").toString(), article);
        }
    }

    private void save(EsRule esRule, JSONObject jsonObject) {
        if (esRule.type().getName().equals(ARTICLE)) {
            Article article = getArticle(jsonObject);
            esUtil.save(ARTICLE, "docs", article,"articleId");
        }
        if (esRule.type().getName().equals(COMMENT)) {
            Comment comment = commentService.getOne(new QueryWrapper<Comment>().eq("comment_id", jsonObject.get("data").toString()));
            UserEntity byId = userService.findById(comment.getMemberId());
            comment.setMemberImage(byId.getImage());
            comment.setMemberName(byId.getUserName());
            esUtil.save(COMMENT, DOCS, comment,"articleId");
        }
    }

    @NotNull
    private Article getArticle(JSONObject jsonObject) {
        String articleId = jsonObject.get("data").toString();
        Article article = articleService.getOne(new QueryWrapper<Article>().eq("article_id", articleId));
        UserEntity byId = userService.findById(article.getMemberId());
        article.setMemberImage(byId.getImage());
        article.setUserName(byId.getUserName());
        return article;
    }
}
