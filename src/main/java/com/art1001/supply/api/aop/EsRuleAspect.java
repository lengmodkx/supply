package com.art1001.supply.api.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.EsRule;
import com.art1001.supply.entity.content.Article;
import com.art1001.supply.entity.content.Comment;
import com.art1001.supply.entity.content.Question;
import com.art1001.supply.entity.content.Reply;
import com.art1001.supply.util.EsUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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
    private EsUtil esUtil;

    private final static String ARTICLE = "article";

    private final static String COMMENT = "comment";

    private final static String QUESTION = "question";

    private final static String REPLY = "reply";

    private final static String DOCS = "docs";

    private final static int ONE = 1;

    private final static int TWO = 2;


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

    }

    private void update(EsRule esRule, JSONObject jsonObject) {
        switch (esRule.type().getName()) {
            case ARTICLE:
                Article article = JSON.parseObject(jsonObject.get("data").toString(), Article.class);
                esUtil.update(ARTICLE, DOCS, "articleId", article.getArticleId(), article);
                break;
            case COMMENT:
                List<Comment> comments = JSONArray.parseArray((String) jsonObject.get("data"), Comment.class);
                for (Comment comment : comments) {
                    esUtil.update(COMMENT, DOCS, "commentId", comment.getCommentId(), comment);
                }
                break;
            case REPLY:
                Reply reply = JSON.parseObject(jsonObject.get("data").toString(), Reply.class);
                esUtil.update(REPLY, DOCS, "replyId", reply.getReplyId(), reply);
                break;
            case QUESTION:
                Question question = JSON.parseObject(jsonObject.get("data").toString(), Question.class);
                esUtil.update(QUESTION, DOCS, "questionId", question.getQuestionId(), question);
            default:
                break;
        }

    }

    private void save(EsRule esRule, JSONObject jsonObject) {
        switch (esRule.type().getName()) {
            case ARTICLE:
                Object article = jsonObject.get("data");
                esUtil.save(ARTICLE, "docs", article, "articleId");
                break;
            case COMMENT:
                Object comment = jsonObject.get("data");
                esUtil.save(COMMENT, DOCS, comment, "commentId");
                break;
            case QUESTION:
                Object question = jsonObject.get("data");
                esUtil.save(QUESTION, DOCS, question, "questionId");
                break;
            case REPLY:
                Object reply = jsonObject.get("data");
                esUtil.save(REPLY, DOCS, reply, "replyId");
                break;
            default:
                break;
        }


    }


}
