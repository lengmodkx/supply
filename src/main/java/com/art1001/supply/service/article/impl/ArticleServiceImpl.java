package com.art1001.supply.service.article.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.article.Article;
import com.art1001.supply.entity.fans.Attention;
import com.art1001.supply.entity.fans.Fans;
import com.art1001.supply.entity.fans.MutualFans;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.article.ArticleMapper;
import com.art1001.supply.mapper.fans.AttentionMapper;
import com.art1001.supply.mapper.fans.FansMapper;
import com.art1001.supply.mapper.fans.MutualFansMapper;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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
    private RedisUtil redisUtil;

    @Resource
    private FollowUtil followUtil;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MutualFansMapper mutualFansMapper;

    @Resource
    private AttentionMapper attentionMapper;

    @Resource
    private FansMapper fansMapper;

    // 我关注的
    private static final String FOLLOWING = "FOLLOWING_:";
    // 我的粉丝
    private static final String FANS = "FANS_:";
    // 我的互粉
    private static final String MUTUAL_FANS = "MUTUAL_FANS_:";
    // 我关注的人的文章
    private static final String FOLLOWED_ARTICLE = "FOLLOWED_ARTICLE_:";
    // 我写的文章
    private static final String MY_ARTICLE = "MY_ARTICLE_:";


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
    public void addArticle(String articleTitle, String articleContent, String articlePureContent, Integer acId, String headlineContent, List<String> headlineImages, String videoName, List<String> videoAddress, String videoCover, Integer coverShow, List<String> coverImages) {
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
        article.setState(1);
        article.setCreateTime(System.currentTimeMillis());
        article.setUpdateTime(System.currentTimeMillis());
        save(article);
        Set<String> zrevrange = redisUtil.zrevrange(FANS + ShiroAuthenticationManager.getUserId(), 0, -1);
        for (String set : zrevrange) {
            redisUtil.zSet(FOLLOWED_ARTICLE + set, article.getArticleId(), System.currentTimeMillis());
        }
        redisUtil.zSet(FOLLOWED_ARTICLE + ShiroAuthenticationManager.getUserId(), article.getArticleId(), System.currentTimeMillis());
        redisUtil.zSet(MY_ARTICLE + ShiroAuthenticationManager.getUserId(), article.getArticleId(), System.currentTimeMillis());
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
    public void editArticle(String articleTitle, String articleContent, String articlePureContent, String articleId, String headlineContent, List<String> headlineImages, String videoName, List<String> videoAddress, String videoCover, Integer coverShow, List<String> coverImages) {
        Article article = Article.builder().articleTitle(articleTitle).articleContent(articleContent)
                .articlePureContent(articlePureContent)
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
        // 0 = 取消关注 1 = 关注
        int isFollow = 0;
        Long rank = redisUtil.zRank(FOLLOWING + ShiroAuthenticationManager.getUserId(), memberId);
        // 关注操作
        if (rank == null) {
            // 两人是否是互粉关系
            Long aLong = redisUtil.zRank(FANS + ShiroAuthenticationManager.getUserId(), memberId);
            if (aLong != null) {
                // 存储互粉关系
                mutualFansMapper.insert(MutualFans.builder().mutualMemberId(ShiroAuthenticationManager.getUserId())
                        .mutualFansId(memberId).createTime(System.currentTimeMillis())
                        .updateTime(System.currentTimeMillis()).build());
                redisUtil.zSet(MUTUAL_FANS + ShiroAuthenticationManager.getUserId(), memberId, System.currentTimeMillis());
                redisUtil.zSet(MUTUAL_FANS + memberId, ShiroAuthenticationManager.getUserId(), System.currentTimeMillis());
            }
            // 存储关注关系
            attentionMapper.insert(Attention.builder().memberId(ShiroAuthenticationManager.getUserId())
                    .followedId(memberId).createTime(System.currentTimeMillis())
                    .updateTime(System.currentTimeMillis()).build());
            fansMapper.insert(Fans.builder().followedId(memberId)
                    .fansId(ShiroAuthenticationManager.getUserId())
                    .createTime(System.currentTimeMillis())
                    .updateTime(System.currentTimeMillis()).build());
            redisUtil.zSet(FOLLOWING + ShiroAuthenticationManager.getUserId(), memberId, System.currentTimeMillis());
            redisUtil.zSet(FANS + memberId, ShiroAuthenticationManager.getUserId(), System.currentTimeMillis());
            isFollow = 1;
        }
        // 取关操作
        else {
            Long aLong = redisUtil.zRank(MUTUAL_FANS + ShiroAuthenticationManager.getUserId(), memberId);
            // 删除互粉关系
            if (aLong != null) {
                redisUtil.zRem(MUTUAL_FANS + ShiroAuthenticationManager.getUserId(), memberId);
                redisUtil.zRem(MUTUAL_FANS + memberId, ShiroAuthenticationManager.getUserId());
                mutualFansMapper.delete(new QueryWrapper<MutualFans>().eq("mutual_member_id", ShiroAuthenticationManager.getUserId())
                        .eq("mutual_fans_id", memberId));
            }
            redisUtil.zRem(FOLLOWING + ShiroAuthenticationManager.getUserId(), memberId);
            redisUtil.zRem(FANS + memberId, ShiroAuthenticationManager.getUserId());
            attentionMapper.delete(new QueryWrapper<Attention>().eq("member_id", ShiroAuthenticationManager.getUserId()).eq("followed_id", memberId));
            fansMapper.delete(new QueryWrapper<Fans>().eq("followed_id", memberId).eq("fans_id", ShiroAuthenticationManager.getUserId()));
        }
        return isFollow;
    }

    @Override
    public List<Article> attentionListArticle(Integer pageNum) {
        Set<String> articleIds = Sets.newHashSet();
        if (pageNum.equals(Constants.B_ONE)) {
            articleIds = redisUtil.zrevrange(FOLLOWED_ARTICLE + ShiroAuthenticationManager.getUserId(), 0, 9);
        } else {
            redisUtil.zrevrange(FOLLOWED_ARTICLE + ShiroAuthenticationManager.getUserId(), (pageNum - 1) * 10, (pageNum * 10) - 1);
        }
        return list(new QueryWrapper<Article>().in("article_id", articleIds)).stream().sorted(Comparator.comparing(Article::getCreateTime).reversed()).collect(Collectors.toList());
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
        records.sort(Comparator.comparing(Article::getCreateTime));
        return articleIPage;
    }

    @Override
    public List<UserEntity> allConnectionUser(Integer pageNum, Integer type) {


        List<UserEntity> list = Lists.newArrayList();
        if (type.equals(Constants.B_ONE)) {
            list = getUserEntities(pageNum,  list, FANS);
        }
        if (type.equals(Constants.B_TWO)) {
            list = getUserEntities(pageNum,  list, FOLLOWING);
        }
        if (type.equals(Constants.B_THREE)) {
            list = getUserEntities(pageNum,  list, MUTUAL_FANS);
        }
        return list;
    }

    private List<UserEntity> getUserEntities(Integer pageNum, List<UserEntity> list, String prefix) {
        String userId = ShiroAuthenticationManager.getUserId();
        Set<String> memberIds=Sets.newHashSet();
        if (pageNum.equals(Constants.B_ONE)) {
            memberIds = redisUtil.zrevrange(prefix + userId, 0, 9);
        } else {
            memberIds = redisUtil.zrevrange(prefix + userId, pageNum * (pageNum - 1) * 10, (pageNum * 10) - 1);
        }
        if (CollectionUtils.isNotEmpty(memberIds)) {
            list = userMapper.selectList(new QueryWrapper<UserEntity>().in("user_id", memberIds));
        }
        return list;
    }

    @Override
    public List<Article> myArticle(Integer pageNum) {
        Set<String> articleIds = Sets.newHashSet();
        if (pageNum.equals(Constants.B_ONE)) {
            articleIds = redisUtil.zrevrange(MY_ARTICLE + ShiroAuthenticationManager.getUserId(), 0, 9);
        } else {
            articleIds = redisUtil.zrevrange(MY_ARTICLE + ShiroAuthenticationManager.getUserId(), (pageNum - 1) * 10, (pageNum * 10) - 1);
        }
        return list(new QueryWrapper<Article>().in("article_id", articleIds));
    }
}
