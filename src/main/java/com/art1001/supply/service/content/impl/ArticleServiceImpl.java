package com.art1001.supply.service.content.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.content.Article;
import com.art1001.supply.entity.content.Comment;
import com.art1001.supply.entity.content.Topic;
import com.art1001.supply.entity.fans.Attention;
import com.art1001.supply.entity.fans.Fans;
import com.art1001.supply.entity.fans.MutualFans;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.content.ArticleMapper;
import com.art1001.supply.mapper.content.CommentMapper;
import com.art1001.supply.mapper.content.TopicMapper;
import com.art1001.supply.mapper.fans.AttentionMapper;
import com.art1001.supply.mapper.fans.FansMapper;
import com.art1001.supply.mapper.fans.MutualFansMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.content.ArticleService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.CommonUtils;
import com.art1001.supply.util.EsUtil;
import com.art1001.supply.util.PatternUtils;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
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

    @Resource
    private TopicMapper topicMapper;


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
            if (CollectionUtils.isNotEmpty(headlineImages)) {
                article.setHeadlineImages(CommonUtils.listToString(headlineImages));
            }

        }
        if (acId.equals(THREE)) {
            article.setVideoName(videoName);
            if (CollectionUtils.isNotEmpty(videoAddress)) {
                article.setVideoAddress(CommonUtils.listToString(videoAddress));
            }
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
                .videoName(videoName).videoCover(videoCover).state(1)
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
        updateById(article);

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
        BoolQueryBuilder bool = new BoolQueryBuilder();

        bool.must(QueryBuilders.matchQuery("isDel", 0).operator(Operator.AND));
        bool.must(QueryBuilders.matchQuery("state", 2).operator(Operator.AND));
        List<Article> list = Lists.newArrayList();
        if (StringUtils.isNotEmpty(acId)) {
            bool.must(QueryBuilders.matchQuery("acId", acId).operator(Operator.AND));
        }
        if (CollectionUtils.isNotEmpty(articleIds)) {
            for (String articleId : articleIds) {
                bool.must(QueryBuilders.matchQuery("articleId", articleId).operator(Operator.AND));
                Article article = esUtil.search(Article.class, sourceBuilder, ARTICLE);
                if (article != null) list.add(article);
            }

            // es没有就从数据库查
            if (CollectionUtils.isEmpty(list)) {
                // todo 后续根据内容状态查询
                QueryWrapper<Article> query = new QueryWrapper<Article>().eq("is_del", 0).in("article_id", articleIds);
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
        }
        return page;
    }

    /**
     * 查询文章评论信息
     *
     * @param articles
     */
    private void setComment(List<Article> articles) {
        articles.forEach(r -> {
            List<Comment>list = commentMapper.selectList(new QueryWrapper<Comment>().eq("article_id", r.getArticleId()).eq("is_del", 0));
            int commentNotCheckCount=0;
            int commentIsCheckCount=0;
            int commentFailCheckCount=0;
            if (CollectionUtils.isNotEmpty(list)) {
                commentNotCheckCount=list.stream().filter(f->f.getCommentState().equals(Constants.B_ZERO)).collect(Collectors.toList()).size();
                commentIsCheckCount=list.stream().filter(f->f.getCommentState().equals(Constants.B_ONE)).collect(Collectors.toList()).size();
                commentFailCheckCount=list.stream().filter(f->f.getCommentState().equals(Constants.B_TWO)).collect(Collectors.toList()).size();
            }
            r.setCommentCount(list.size());
            r.setCommentNotCheckCount(commentNotCheckCount);
            r.setCommentIsCheckCount(commentIsCheckCount);
            r.setCommentFailCheckCount(commentFailCheckCount);
        });
        articles.sort(Comparator.comparing(Article::getCreateTime).reversed());
    }

    /**
     * 所有文章列表
     *
     * @param pageNum
     * @param pageSize
     * @param acId
     * @param state    0 未通过审核  1已通过审核
     * @return
     */
    @Override
    public Page<Article> allArtile(Integer pageNum, Integer pageSize, String acId, Integer state) {
        Page<Article> page = new Page<>();
        SearchSourceBuilder source = new SearchSourceBuilder();
        BoolQueryBuilder bool = new BoolQueryBuilder();
        bool.must(QueryBuilders.matchQuery("isDel", 0).operator(Operator.AND));
        if (StringUtils.isNotEmpty(acId)) {
            bool.must(QueryBuilders.matchQuery("acId", acId).operator(Operator.AND));
        }
        if (state.equals(ONE)) {
            bool.must(QueryBuilders.matchQuery("state", 2).operator(Operator.AND));
        } else {
            bool.must(QueryBuilders.matchQuery("state", 1).operator(Operator.AND));
        }
        source.query(bool);
        page = esUtil.searchListByPage(Article.class, source, ARTICLE, pageNum);

        if (CollectionUtils.isEmpty(page.getRecords())) {
            page.setRecords(articleMapper.selectByExample(page, acId, state));
        }
        setComment(page.getRecords());
        page.setCurrent(pageNum);
        page.setSize(20);
        return page;
    }

    @Override
    public List<UserEntity> allConnectionUser(Integer pageNum, Integer type, String memberId) {

        List<UserEntity> list = Lists.newArrayList();
        switch (type) {
            case 1:
                return getUserEntities(pageNum, list, FANS, memberId);
            case 2:
                return getUserEntities(pageNum, list, FOLLOWING, memberId);
            case 3:
                return getUserEntities(pageNum, list, MUTUAL_FANS, memberId);
            default:
                return null;
        }
    }

    private List<UserEntity> getUserEntities(Integer pageNum, List<UserEntity> list, String prefix, String userId) {
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
    public Page<Article> myArticle(Integer pageNum, String acId, String keyword, Long startTime, Long endTime, String memberId,Integer state) {
        Page<Article> page = new Page<>();
        // es查询
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.matchQuery("state",state).operator(Operator.AND));
            boolQueryBuilder.must(QueryBuilders.matchQuery("isDel",0).operator(Operator.AND));
            boolQueryBuilder.must(QueryBuilders.matchQuery("memberId", memberId).operator(Operator.AND));
            if (StringUtils.isNotEmpty(keyword)) {
                // 模糊查询
                boolQueryBuilder.must(QueryBuilders.wildcardQuery("articleTitle", "*" + keyword + "*").boost(2f));
            }

            if (startTime != null && endTime != null) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("createTime").from(startTime).to(endTime));
            }
            // 根据分类查询
            if (StringUtils.isNotEmpty(acId)) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("acId", acId).operator(Operator.AND));
            }

            sourceBuilder.query(boolQueryBuilder);

            page = esUtil.searchListByPage(Article.class, sourceBuilder, ARTICLE, pageNum);

            // 设置文章评论信息
            if (CollectionUtils.isNotEmpty(page.getRecords())) {
                setComment(page.getRecords());
            }
            if (CollectionUtils.isEmpty(page.getRecords())) {
                QueryWrapper<Article> wrapper = new QueryWrapper<Article>().eq(StringUtils.isNotEmpty(acId), "ac_Id", acId)
                        .gt(startTime != null, "create_time", startTime)
                        .lt(endTime != null, "create_time", endTime)
                        .eq("member_Id", memberId)
                        .like(StringUtils.isNotEmpty(keyword), "article_title", keyword).eq("state",state).eq("is_del",0);
                Page<Article> articlePage = articleMapper.selectArticlePage(page, wrapper);
                // 设置文章评论信息
                if (CollectionUtils.isNotEmpty(articlePage.getRecords())) {
                    setComment(articlePage.getRecords());
                }
                //es 没有就存进去
                Optional.ofNullable(articlePage.getRecords()).ifPresent(l -> l.forEach(r -> {
                    esUtil.save(ARTICLE, DOCS, r, "articleId");
                }));
                return articlePage;
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                    UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("user_id", r.getMemberId()));
                    r.setMemberImage(userEntity.getImage());
                    r.setUserName(userEntity.getUserName());
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

    /**
     * 审核文章
     *
     * @param articleId       文章id
     * @param state           1审核通过 2审核不通过
     * @param checkFailReason 审核失败原因
     * @return
     */
    @Override
    public void checkArticle(String articleId, Integer state, String checkFailReason) {
        Article article = getOne(new QueryWrapper<Article>().eq("article_id", articleId));
        if (state.equals(ONE)) {
            article.setState(2);
            List<String> topIds = Lists.newArrayList();
            List<String> memberIds = Lists.newArrayList();
            if (StringUtils.isNotEmpty(article.getArticleContent())) {
                // 解析标签并存储
                Optional.ofNullable(PatternUtils.parsingTags(article.getArticleContent())).ifPresent(topics -> topics.forEach(r -> {
                    if (topicMapper.selectCount(new QueryWrapper<Topic>().eq(TOPIC_NAME, r)).equals(ZERO)) {
                        Topic topic = Topic.builder().topicName(r).createTime(System.currentTimeMillis())
                                .updateTime(System.currentTimeMillis()).build();
                        topicMapper.insert(topic);
                        topIds.add(topic.getTopicId());
                    }
                }));
                // 解析@昵称 并存储
                Optional.ofNullable(PatternUtils.parsingNickName(article.getArticleContent())).ifPresent(nickNames -> nickNames.forEach(r -> {
                    if (!userMapper.selectCount(new QueryWrapper<UserEntity>().eq(NICK_NAME, r)).equals(ZERO)) {
                        String memberId = userMapper.selectOne(new QueryWrapper<UserEntity>().eq(NICK_NAME, r)).getUserId();
                        memberIds.add(memberId);
                    }
                }));
            }
            if (CollectionUtils.isNotEmpty(topIds)) article.setTopicIds(topIds);
            if (CollectionUtils.isNotEmpty(memberIds)) article.setTopicIds(memberIds);
        }
        if (state.equals(TWO)) {
            article.setState(3);
            if (StringUtils.isNotEmpty(checkFailReason)) article.setCheckFailReason(checkFailReason);
        }
        updateById(article);

    }


}
