package com.art1001.supply.service.article.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.article.Article;
import com.art1001.supply.mapper.article.ArticleMapper;
import com.art1001.supply.service.article.ArticleService;
import com.art1001.supply.service.article.UserAttentionService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName articleServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/24 9:50
 * @Discription
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Resource
    private UserAttentionService userAttentionService;

    @Resource
    private RedisUtil redisUtil;




    @Override
    public Integer addArticle(String articleTitle, String articleContent, String acId, Integer coverShow, List<String> coverImages) {
        Article article = new Article();
        article.setAcId(acId);
        article.setArticleTitle(articleTitle);
        article.setArticleContent(articleContent);
        article.setCoverShow(coverShow);
        article.setMemberId(ShiroAuthenticationManager.getUserId());
        StringBuilder images = listToString(coverImages);
        article.setCoverImages(images.toString());
        article.setIsDel(0);
        article.setArticleShow(1);
        article.setCreateTime(System.currentTimeMillis());
        article.setUpdateTime(System.currentTimeMillis());
        save(article);
        return 1;
    }

    @NotNull
    private StringBuilder listToString(List<String> coverImages) {
        StringBuilder images = new StringBuilder();
        for (int i = 0; i < coverImages.size(); i++) {
            if (i != coverImages.size() - 1) {
                images.append(coverImages.get(i)).append(",");
            } else {
                images.append(coverImages.get(i));
            }
        }
        return images;
    }

    @Override
    public Integer editArticle(String articleTitle, String articleContent, String articleId, Integer coverShow, List<String> coverImages) {
        Article article = new Article();
        article.setArticleId(articleId);
        article.setArticleTitle(articleTitle);
        article.setCoverShow(coverShow);
        article.setCoverImages(listToString(coverImages).toString());
        article.setUpdateTime(System.currentTimeMillis());
        updateById(article);
        return 1;
    }

    @Override
    public Integer attentionUserStatus(String memberId, Integer type) {
        if (type.equals(Constants.B_ONE)) {
            if (redisUtil.exists(Constants.MEMBER_ID + ShiroAuthenticationManager.getUserId())) {
                String s = redisUtil.get(Constants.MEMBER_ID + ShiroAuthenticationManager.getUserId());
                List<String> parse = (List<String>) JSON.parse(s);
                parse.add(memberId);
                String str= JSONObject.toJSONString(parse);
                redisUtil.remove(Constants.MEMBER_ID + ShiroAuthenticationManager.getUserId());
                redisUtil.set(Constants.MEMBER_ID + ShiroAuthenticationManager.getUserId(),str );
            } else {
                List<String> list = Lists.newArrayList();
                list.add(memberId);
                String str= JSONObject.toJSONString(list);
                redisUtil.set(Constants.MEMBER_ID + ShiroAuthenticationManager.getUserId(),str );
            }
        }else {
            if (redisUtil.exists(Constants.MEMBER_ID + ShiroAuthenticationManager.getUserId())) {
                String s = redisUtil.get(Constants.MEMBER_ID + ShiroAuthenticationManager.getUserId());
                List<String> parse = (List<String>) JSON.parse(s);
                List<String> collect = parse.stream().filter(f -> !f.equals(memberId)).collect(Collectors.toList());
                String str= JSONObject.toJSONString(collect);
                redisUtil.remove(Constants.MEMBER_ID + ShiroAuthenticationManager.getUserId());
                redisUtil.set(Constants.MEMBER_ID + ShiroAuthenticationManager.getUserId(),str );
            }
        }
        return 1;
    }

    @Override
    public List<Article> listArticle() {
        List<Article> list=Lists.newArrayList();
        if (redisUtil.exists(Constants.MEMBER_ID+ ShiroAuthenticationManager.getUserId())) {
            String s = redisUtil.get(Constants.MEMBER_ID + ShiroAuthenticationManager.getUserId());
            List<String> parse = (List<String>) JSON.parse(s);
            list= list(new QueryWrapper<Article>().in("member_id",parse));
        }else {
            list = list(new QueryWrapper<>());
        }
        Optional.ofNullable(list).ifPresent(r->r.sort(Comparator.comparing(Article::getCreateTime)));
        return list;
    }


}
