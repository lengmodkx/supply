package com.art1001.supply.service.article.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.article.Article;
import com.art1001.supply.listener.Redis2;
import com.art1001.supply.listener.RedisReceiver;
import com.art1001.supply.mapper.article.ArticleMapper;
import com.art1001.supply.service.article.ArticleService;
import com.art1001.supply.service.article.RedisService;
import com.art1001.supply.service.article.UserAttentionService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName articleServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/24 9:50
 * @Discription
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private RedisService redisService;

    @Resource
    private UserAttentionService userAttentionService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RedisReceiver redisReceiver;


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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memberId", ShiroAuthenticationManager.getUserId());
        jsonObject.put("articleId", article.getArticleId());
        jsonObject.put("time", System.currentTimeMillis());
        redisService.sendChannelMess(Constants.SEND_MESSAGE_CHANNEL, jsonObject);
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("feedMemberId", memberId);
        jsonObject.put("type", type);
        if (redisUtil.exists("feedMemberId:" + memberId)) {

            redisUtil.remove("feedMemberId:" + memberId);

            String s = redisUtil.get("feedMemberId:" + memberId);
            List<String> parse = (List<String>) JSON.parse(s);
            parse.add(ShiroAuthenticationManager.getUserId());
            JSONObject json = JSONObject.parseObject(parse.toString());
            redisUtil.set("feedMemberId:" + memberId,json );
        } else {
            List<String> set = Lists.newArrayList();
            set.add(ShiroAuthenticationManager.getUserId());
            JSONArray objects = JSON.parseArray(set.toString());
            redisUtil.set("feedMemberId:" + memberId,objects );
        }

        redisService.sendChannelMess(Constants.ATTENTION_CHANNEL, jsonObject);
        return 1;
    }

    @Resource
    private Redis2 redis2;

    @Override
    public List<Article> listArticle() {
        String message = redis2.getMsg();
        return list(new QueryWrapper<Article>().eq("article_id", message));
    }


}
