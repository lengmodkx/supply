package com.art1001.supply.api.aop;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.EsRule;
import com.art1001.supply.entity.content.Article;
import com.art1001.supply.entity.content.Comment;
import com.art1001.supply.entity.content.Question;
import com.art1001.supply.entity.content.Reply;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.service.content.ArticleService;
import com.art1001.supply.service.content.CommentService;
import com.art1001.supply.service.content.QuestionService;
import com.art1001.supply.service.content.ReplyService;
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
    private QuestionService questionService;

    @Resource
    private ReplyService replyService;

    @Resource
    private EsUtil esUtil;

    private final static String ARTICLE = "article";

    private final static String COMMENT = "comment";

    private final static String QUESTION = "question";

    private final static String REPLY = "reply";

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
        // 修改
        if (esRule.sort() == TWO) {
            this.update(esRule, jsonObject);
        }
        // 删除
        if (esRule.sort() == THREE) {
            this.delete(esRule, jsonObject);
        }

    }

    private void delete(EsRule esRule, JSONObject jsonObject) {
        switch (esRule.type().getName()) {
            case ARTICLE:
                esUtil.delete(ARTICLE, DOCS, "articleId", jsonObject.get("data").toString());
                break;
            case COMMENT:
                esUtil.delete(COMMENT, DOCS, "commentId", jsonObject.get("data").toString());
                break;
            case QUESTION:
                esUtil.delete(QUESTION, DOCS, "questionId", jsonObject.get("data").toString());
                break;
            case REPLY:
                esUtil.delete(REPLY, DOCS, "replyId", jsonObject.get("data").toString());
                break;
            default:
                break;
        }
    }


    private void update(EsRule esRule, JSONObject jsonObject) {
        UserEntity byId;
        switch (esRule.type().getName()) {
            case ARTICLE:
                Article article = getArticle(jsonObject);
                esUtil.update(ARTICLE, DOCS, "articleId", jsonObject.get("data").toString(), article);
                break;
            case COMMENT:
                String[] commentIds = jsonObject.get("data").toString().split(",");
                for (String commentId : commentIds) {
                    Comment comment = commentService.getOne(new QueryWrapper<Comment>().eq("comment_id", commentId));
                    byId = getUserEntity(comment.getMemberId());
                    comment.setMemberName(byId.getUserName());
                    comment.setMemberImage(byId.getImage());
                    esUtil.update(COMMENT, DOCS, "commentId", jsonObject.get("data").toString(), comment);
                }
                break;
            case REPLY:
                String replyId = jsonObject.get("data").toString();
                Reply reply = replyService.getOne(new QueryWrapper<Reply>().eq("reply_id", replyId));
                byId = getUserEntity(reply.getReplyMemberId());
                reply.setReplyMemberName(byId.getUserName());
                reply.setReplyMemberImage(byId.getImage());
                esUtil.update(REPLY, DOCS, "replyId", jsonObject.get("data").toString(), reply);
                break;
            default:
                break;
        }

    }

    private void save(EsRule esRule, JSONObject jsonObject) {
        UserEntity byId;
        switch (esRule.type().getName()) {
            case ARTICLE:
                Article article = getArticle(jsonObject);
                esUtil.save(ARTICLE, "docs", article, "articleId");
                break;
            case COMMENT:
                Comment comment = commentService.getOne(new QueryWrapper<Comment>().eq("comment_id", jsonObject.get("data").toString()));
                byId = getUserEntity(comment.getMemberId());
                comment.setMemberImage(byId.getImage());
                comment.setMemberName(byId.getUserName());
                esUtil.save(COMMENT, DOCS, comment, "commentId");
                break;
            case QUESTION:
                Question question = questionService.getOne(new QueryWrapper<Question>().eq("question_id", jsonObject.get("data").toString()));
                byId = getUserEntity(question.getQuestionMemberId());
                question.setQuestionMemberName(byId.getUserName());
                question.setQuestionMemberImage(byId.getImage());
                esUtil.save(QUESTION, DOCS, question, "questionId");
                break;
            case REPLY:
                Reply reply = replyService.getOne(new QueryWrapper<Reply>().eq("reply_id", jsonObject.get("data").toString()));
                byId = getUserEntity(reply.getReplyMemberId());
                reply.setReplyMemberName(byId.getUserName());
                reply.setReplyMemberImage(byId.getImage());
                esUtil.save(REPLY, DOCS, reply, "replyId");
                break;
            default:
                break;
        }


    }

    private UserEntity getUserEntity(String memberId) {
        return userService.findById(memberId);
    }

    @NotNull
    private Article getArticle(JSONObject jsonObject) {
        String articleId = jsonObject.get("data").toString();
        Article article = articleService.getOne(new QueryWrapper<Article>().eq("article_id", articleId));
        UserEntity byId = getUserEntity(article.getMemberId());
        article.setMemberImage(byId.getImage());
        article.setUserName(byId.getUserName());
        return article;
    }
}
