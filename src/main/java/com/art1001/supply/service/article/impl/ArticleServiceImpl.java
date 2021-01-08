package com.art1001.supply.service.article.impl;

import com.art1001.supply.entity.article.Article;
import com.art1001.supply.entity.article.Comment;
import com.art1001.supply.entity.fans.Attention;
import com.art1001.supply.entity.fans.Fans;
import com.art1001.supply.entity.fans.MutualFans;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.article.ArticleMapper;
import com.art1001.supply.mapper.article.CommentMapper;
import com.art1001.supply.mapper.fans.AttentionMapper;
import com.art1001.supply.mapper.fans.FansMapper;
import com.art1001.supply.mapper.fans.MutualFansMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.article.ArticleService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.CommonUtils;
import com.art1001.supply.util.EsUtil;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private ArticleMapper articleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MutualFansMapper mutualFansMapper;

    @Resource
    private AttentionMapper attentionMapper;

    @Resource
    private FansMapper fansMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private RestHighLevelClient esClient;

    @Resource
    private EsUtil<Article> esUtil;


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
    public String addArticle(String articleTitle, String articleContent, String articlePureContent, Integer acId, String headlineContent, List<String> headlineImages, String videoName, List<String> videoAddress, String videoCover, Integer coverShow, List<String> coverImages) {
        Article article = new Article();
        if (acId.equals(ONE)) {
            article.setArticleTitle(articleTitle);
            article.setArticleContent(articleContent);
            article.setArticlePureContent(articlePureContent);
            article.setCoverShow(coverShow);
            if (CollectionUtils.isNotEmpty(coverImages)) {
                article.setCoverImages(CommonUtils.listToString(coverImages));
            }
        }
        if (acId.equals(TWO)) {
            article.setHeadlineContent(headlineContent);
            article.setHeadlineImages(CommonUtils.listToString(headlineImages));
        }
        if (acId.equals(THREE)) {
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
        if (CollectionUtils.isNotEmpty(zrevrange)) {
            for (String set : zrevrange) {
                redisUtil.zSet(FOLLOWED_ARTICLE + set, article.getArticleId(), System.currentTimeMillis());
            }
        }
        redisUtil.zSet(FOLLOWED_ARTICLE + ShiroAuthenticationManager.getUserId(), article.getArticleId(), System.currentTimeMillis());
        return article.getArticleId();
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
                .articleId(articleId).headlineContent(headlineContent)
                .videoName(videoName).videoCover(videoCover)
                .coverShow(coverShow).updateTime(System.currentTimeMillis())
                .build();
        article.setArticlePureContent(articlePureContent);
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
            // 存储关注关系
            redisUtil.zSet(FOLLOWING + ShiroAuthenticationManager.getUserId(), memberId, System.currentTimeMillis());
            redisUtil.zSet(FANS + memberId, ShiroAuthenticationManager.getUserId(), System.currentTimeMillis());
            attentionMapper.insert(Attention.builder().memberId(ShiroAuthenticationManager.getUserId())
                    .followedId(memberId).createTime(System.currentTimeMillis())
                    .updateTime(System.currentTimeMillis()).build());
            fansMapper.insert(Fans.builder().followedId(memberId)
                    .fansId(ShiroAuthenticationManager.getUserId())
                    .createTime(System.currentTimeMillis())
                    .updateTime(System.currentTimeMillis()).build());

            // 两人是否是互粉关系
            Long aLong = redisUtil.zRank(FANS + ShiroAuthenticationManager.getUserId(), memberId);
            if (aLong != null) {
                // 存储互粉关系

                redisUtil.zSet(MUTUAL_FANS + ShiroAuthenticationManager.getUserId(), memberId, System.currentTimeMillis());
                redisUtil.zSet(MUTUAL_FANS + memberId, ShiroAuthenticationManager.getUserId(), System.currentTimeMillis());

                mutualFansMapper.insert(MutualFans.builder().mutualMemberId(ShiroAuthenticationManager.getUserId())
                        .mutualFansId(memberId).createTime(System.currentTimeMillis())
                        .updateTime(System.currentTimeMillis()).build());
            }
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
    public Page<Article> attentionListArticle(Integer pageNum, String acId) {

        Page<Article> page = new Page<>(pageNum, 20);
        Set<String> articleIds = redisUtil.zrange(FOLLOWED_ARTICLE + ShiroAuthenticationManager.getUserId(), (pageNum - 1) * 20, (pageNum * 20) - 1);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        List<Article> list = Lists.newArrayList();
        if (StringUtils.isNotEmpty(acId)) {
            sourceBuilder.query(QueryBuilders.matchQuery("acId", acId));
        }
        if (CollectionUtils.isNotEmpty(articleIds)) {
            for (String articleId : articleIds) {
                sourceBuilder.query(QueryBuilders.matchQuery("articleId", articleId));
                Article article = esUtil.search(Article.class, sourceBuilder, ARTICLE);
                if (article!=null)list.add(article);
            }
        }


        // es没有就从数据库查
        if (CollectionUtils.isEmpty(list)) {
            // todo 后续根据内容状态查询
            QueryWrapper<Article> query = new QueryWrapper<Article>().in("article_id", articleIds).eq("is_del", 0);
            if (StringUtils.isNotEmpty(acId)) {
                query.eq("ac_id", acId);
            }
            Page<Article> articlePage = articleMapper.selectPage(page, query);
            if (CollectionUtils.isNotEmpty(articlePage.getRecords())) {
                articlePage.getRecords().forEach(r -> esUtil.save(ARTICLE, DOCS, r, "articleId"));
            }
            setComment(articlePage.getRecords());
            return articlePage;
        }
        setComment(page.getRecords());
        Long aLong = redisUtil.setCount(FOLLOWED_ARTICLE + ShiroAuthenticationManager.getUserId());
        page.setRecords(list);
        page.setTotal(aLong);
        return page;

    }

    /**
     * 查询文章评论信息
     *
     * @param articles
     */
    private void setComment(List<Article> articles) {
        articles.forEach(r -> {
            List<Comment> list = commentMapper.selectList(new QueryWrapper<Comment>().eq("article_id", r.getArticleId()).eq("comment_state", 1).eq("is_del", 0));
            r.setCommentCount(list.size());
            r.setComments(list);
        });
    }

    @Override
    public IPage<Article> allArtile(Integer pageNum, Integer pageSize, String acId) {
        IPage<Article> page = new Page<>(pageNum, pageSize);

        List<Article> list = Lists.newArrayList();

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.query(QueryBuilders.matchQuery("isDel", 0)).from(pageNum).size(pageSize);

        list = esUtil.searchList(Article.class, source, ARTICLE);

        if (CollectionUtils.isEmpty(list)) {
            list = articleMapper.selectByExample(page, acId);
        }
        setComment(page.getRecords());
        page.setRecords(list);
        return page;
    }

    @Override
    public List<UserEntity> allConnectionUser(Integer pageNum, Integer type) {

        List<UserEntity> list = Lists.newArrayList();
        switch (type) {
            case 1:
                return getUserEntities(pageNum, list, FANS);
            case 2:
                return getUserEntities(pageNum, list, FOLLOWING);
            case 3:
                return getUserEntities(pageNum, list, MUTUAL_FANS);
            default:
                return null;
        }
        /*if (type.equals(Constants.B_ONE)) {

        }
        if (type.equals(Constants.B_TWO)) {
            list = getUserEntities(pageNum, list, FOLLOWING);
        }
        if (type.equals(Constants.B_THREE)) {
            list = getUserEntities(pageNum, list, MUTUAL_FANS);
        }*/
//        return list;
    }

    private List<UserEntity> getUserEntities(Integer pageNum, List<UserEntity> list, String prefix) {
        String userId = ShiroAuthenticationManager.getUserId();
        Set<String> memberIds = redisUtil.zrevrange(prefix + userId, pageNum * (pageNum - 1) * 20, (pageNum * 20) - 1);
        if (CollectionUtils.isNotEmpty(memberIds)) {
            for (String memberId : memberIds) {
                Integer isAttention = 0;
                Long aLong = redisUtil.zRank(MUTUAL_FANS + userId, memberId);
                UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("user_id", memberId));
                if (aLong != null) isAttention = 1;
                userEntity.setIsAttention(isAttention);
                list.add(userEntity);
            }
        }
        return list;
    }

    @Override
    public Page<Article> myArticle(Integer pageNum, String acId, String keyword, Long startTime, Long endTime) {
        Page<Article> page = new Page<>();
        // es查询
        try {
//            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (StringUtils.isNotEmpty(keyword)) {
                // 分词查询
                boolQueryBuilder.should(QueryBuilders.matchQuery("articleTitle", keyword).boost(2f));
                // 模糊查询
                boolQueryBuilder.should(QueryBuilders.wildcardQuery("articleTitle", "*" + keyword + "*").boost(2f));
            }
            boolQueryBuilder.should(QueryBuilders.typeQuery(DOCS));
            // 时间范围查询
            if (startTime != null && endTime != null) {
                boolQueryBuilder.should(QueryBuilders.rangeQuery("createTime").from(startTime).to(endTime));
            }
            // 根据分类查询
            if (StringUtils.isNotEmpty(acId)) {
                boolQueryBuilder.should(QueryBuilders.termQuery("acId", acId));
            }
            boolQueryBuilder.should(QueryBuilders.multiMatchQuery("memberId", ShiroAuthenticationManager.getUserId()));
            sourceBuilder.query(boolQueryBuilder);
            // 分页设置
            sourceBuilder.from(pageNum);
            sourceBuilder.size(pageNum * 20);

            List<Article> list = esUtil.searchList(Article.class, sourceBuilder, ARTICLE);
            if (CollectionUtils.isNotEmpty(list)) {
                setComment(list);
            }
            page.setRecords(list);
            page.setCurrent(pageNum);
            page.setSize(pageNum * 20);
            page.setTotal(list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // es没有就查询数据库
        if (CollectionUtils.isEmpty(page.getRecords())) {
            page.setCurrent(pageNum);
            page.setSize(pageNum * 20);
            QueryWrapper<Article> wrapper = new QueryWrapper<Article>().eq(StringUtils.isNotEmpty(acId), "ac_Id", acId)
                    .gt(startTime != null, "create_time", startTime)
                    .lt(endTime != null, "create_time", endTime)
                    .eq("member_Id", ShiroAuthenticationManager.getUserId())
                    .like(StringUtils.isNotEmpty(keyword), "article_title", keyword);
            Page<Article> articlePage = articleMapper.selectArticlePage(page, wrapper);
            //es 没有就存进去
            Optional.ofNullable(articlePage.getRecords()).ifPresent(l -> l.forEach(r -> esUtil.save(ARTICLE, DOCS, r, "articleId")));
            if (CollectionUtils.isNotEmpty(page.getRecords())) {
                setComment(page.getRecords());
            }
            return articlePage;
        }

        return page;
    }

    @Override
    public void deleteArticle(String articleId) {
        update(new UpdateWrapper<Article>().set("is_del", 1).eq("article_id", articleId));
    }

    @Override
    public void dateToEs() {
        List<Article> list = list(new QueryWrapper<Article>());
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(r -> {
                try {
                    // 构建查询
                    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
                    // 索引查询
                    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                    boolQueryBuilder.should(QueryBuilders.multiMatchQuery("articleId", r.getArticleId()));
                    sourceBuilder.query(boolQueryBuilder);
                    SearchRequest request = new SearchRequest();
                    request.source(sourceBuilder);
                    SearchResponse search = esClient.search(request, RequestOptions.DEFAULT);
                    if (search.getHits().getHits().length == 0) {
                        try {
                            IndexRequest indexRequest = new IndexRequest("article", "docs");
                            Map<String, String> article1 = BeanUtils.describe(r);
                            indexRequest.source(article1);
                            esClient.index(indexRequest, RequestOptions.DEFAULT);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
