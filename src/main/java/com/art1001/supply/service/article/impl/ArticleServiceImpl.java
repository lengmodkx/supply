package com.art1001.supply.service.article.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.article.Article;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.article.ArticleMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.article.ArticleService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.CommonUtils;
import com.art1001.supply.util.FollowUtil;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @ClassName articleServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/24 9:50
 * @Discription
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private FollowUtil followUtil;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UserMapper userMapper;

    private static final String FOLLOWING = "FOLLOWING_";
    private static final String FANS = "FANS_";
    private static final String COMMON_KEY = "COMMON_FOLLOWING";


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
    @Override
    public void addArticle(String articleTitle, String articleContent, String articlePureContent,Integer acId, String headlineContent, List<String> headlineImages, String videoName, List<String> videoAddress, String videoCover, Integer coverShow, List<String> coverImages) {
        Article article = new Article();
        if (acId.equals(Constants.B_ONE)) {
            article.setArticleTitle(articleTitle);
            article.setArticleContent(articleContent);
            article.setArticlePureContent(articlePureContent);
            article.setCoverShow(coverShow);
            if (CollectionUtils.isNotEmpty(coverImages)) {
                article.setCoverImages(CommonUtils.listToString(coverImages));
            }
        }
        if (acId.equals(Constants.B_TWO)) {
            article.setHeadlineContent(headlineContent);
            article.setHeadlineImages(CommonUtils.listToString(headlineImages));
        }
        if (acId.equals(Constants.B_THREE)) {
            article.setVideoName(videoName);
            article.setVideoAddress(CommonUtils.listToString(videoAddress));
            article.setVideoCover(videoCover);
        }
        article.setAcId(acId);
        article.setMemberId(ShiroAuthenticationManager.getUserId());
        article.setIsDel(0);
        article.setArticleShow(1);
        article.setCreateTime(System.currentTimeMillis());
        article.setUpdateTime(System.currentTimeMillis());
        save(article);
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
    @Override
    public void editArticle(String articleTitle, String articleContent, String articlePureContent,String articleId, String headlineContent, List<String> headlineImages, String videoName, List<String> videoAddress, String videoCover, Integer coverShow, List<String> coverImages) {
        Article article = Article.builder().articleTitle(articleTitle).articleContent(articleContent)
                .articlePureContent(articlePureContent )
                .articleId(articleId).headlineContent(headlineContent)
                .videoName(videoName).videoCover(videoCover)
                .coverShow(coverShow).updateTime(System.currentTimeMillis())
                .build();
        if (CollectionUtils.isNotEmpty(headlineImages)) {
            article.setHeadlineImages(CommonUtils.listToString(headlineImages));
        }
        if (CollectionUtils.isNotEmpty(coverImages)) {
            article.setCoverImages(CommonUtils.listToString(coverImages));
        }
        if (CollectionUtils.isNotEmpty(videoAddress)) {
            article.setVideoAddress(CommonUtils.listToString(videoAddress));
        }
        articleMapper.editArticle(article);
    }


    @Override
    public int attentionUserStatus(String memberId) {
        if (StringUtils.isEmpty(memberId)) {
            return -1;
        }
        // 0 = 取消关注 1 = 关注
        int isFollow = 0;
        String followingKey = FOLLOWING + ShiroAuthenticationManager.getUserId();
        String fansKey = FANS + memberId;

        Long rank = redisUtil.zRank(followingKey, memberId);
        // 说明当前登录人没有关注过memberId
        if (rank == null) {
            redisUtil.zSet(followingKey, memberId, System.currentTimeMillis());
            redisUtil.zSet(fansKey, ShiroAuthenticationManager.getUserId(), System.currentTimeMillis());
            isFollow = 1;
            // 取消关注
        } else {
            redisUtil.zRem(followingKey, memberId);
            redisUtil.zRem(fansKey, ShiroAuthenticationManager.getUserId());
        }
        return isFollow;
    }

    @Override
    public IPage<Article> attentionListArticle(Integer pageNum, Integer pageSize, String acId) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        Set<String> follwings = followUtil.findFollwings(ShiroAuthenticationManager.getUserId());
        if (CollectionUtils.isNotEmpty(follwings)) {
            QueryWrapper<Article> query = new QueryWrapper<>();
            query.in("member_id", follwings);
            if (StringUtils.isNotEmpty(acId)) {
                query.eq("ac_id", acId);
            }
            return articleMapper.selectPage(page, query);
        }
        return null;
    }

    @Override
    public IPage<Article> allArtile(Integer pageNum, Integer pageSize, String acId) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Article> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(acId)) {
            query.eq("ac_id", acId);
        }
        IPage<Article> articleIPage = articleMapper.selectPage(page, query);
        List<Article> records = articleIPage.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            records.stream().forEach(r -> {
                UserEntity byId = userMapper.findById(r.getMemberId());
                r.setMemberImage(byId.getImage());
                r.setUserName(byId.getUserName());
            });
        }
        return articleIPage;
    }

    @Override
    public IPage<UserEntity> allConnectionUser(Integer pageNum, Integer pageSize, Integer type) {
        Page<UserEntity> page = new Page<>(pageNum, pageSize);
        if (type.equals(Constants.B_ONE)) {
            Set<String> fans = followUtil.findFans(ShiroAuthenticationManager.getUserId());
            if (CollectionUtils.isNotEmpty(fans)) {
                return userMapper.selectPage(page, new QueryWrapper<UserEntity>().in("user_id", fans));
            }
        }
        if (type.equals(Constants.B_TWO)) {
            Set<String> follow = followUtil.findFollwings(ShiroAuthenticationManager.getUserId());
            if (CollectionUtils.isNotEmpty(follow)) {
                return userMapper.selectPage(page, new QueryWrapper<UserEntity>().in("user_id", follow));
            }
        }
        return null;
    }

    @Override
    public IPage<Article> myArticle(Integer pageNum, Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        return page(page, new QueryWrapper<Article>().eq("member_id", ShiroAuthenticationManager.getUserId()));
    }
}
