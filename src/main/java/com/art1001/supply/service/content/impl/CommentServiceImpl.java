package com.art1001.supply.service.content.impl;

import com.art1001.supply.entity.content.Comment;
import com.art1001.supply.mapper.content.CommentMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.content.CommentService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.EsUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName CommentServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/5 14:22
 * @Discription
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private EsUtil<Comment> esUtil;

    @Resource
    private UserMapper userMapper;

    @Override
    public Comment addComment(String commentName, String articleId) {
        Comment comment = Comment.builder().commentClassId(articleId).commentName(commentName).memberId(ShiroAuthenticationManager.getUserId())
                .createTime(System.currentTimeMillis()).isDel(0).commentClass(1).commentState(0).updateTime(System.currentTimeMillis()).build();
        save(comment);
        return comment;
    }

    @Override
    public void removeComment(List<String> commentIds) {
        update(new UpdateWrapper<Comment>().set("is_del", 1).in("comment_id", commentIds));
    }

    @Override
    public Page<Comment> listComment(Integer pageNum, Integer pageSize, Integer commentState, Integer isDel) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Comment> query = new QueryWrapper<Comment>().eq("comment_state", commentState).eq("is_del", isDel).orderByDesc("create_time");
        return commentMapper.listCommentByPage(page, query);
    }

    @Override
    public void updateCommentState(Integer commentState, String commentId, String checkFailReason) {
        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<Comment>().set("comment_state", commentState);
        if (StringUtils.isNotEmpty(checkFailReason)) {
            updateWrapper.set("check_fail_reason", checkFailReason);
        }
        update(updateWrapper.eq("comment_id", commentId));
    }

    @Override
    public Page<Comment> commentListByArticleId(String articleId, Integer pageNum, Integer state) {

        Page<Comment> page;
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("comment_class_id", articleId).operator(Operator.AND));
        boolQueryBuilder.must(QueryBuilders.matchQuery("commentState", state).operator(Operator.AND));
        boolQueryBuilder.must(QueryBuilders.matchQuery("isDel", 0).operator(Operator.AND));
        boolQueryBuilder.must(QueryBuilders.matchQuery("comment_class", 1).operator(Operator.AND));

        sourceBuilder.query(boolQueryBuilder);
        page = esUtil.searchListByPage(Comment.class, sourceBuilder, COMMENT, pageNum);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            List<Comment> collect = page.getRecords().stream().sorted(Comparator.comparing(Comment::getCreateTime).reversed()).collect(Collectors.toList());
            page.setRecords(collect);
        }

        // es没有从数据库查
        if (CollectionUtils.isEmpty(page.getRecords())) {
            page.setCurrent(pageNum);
            page.setSize(20);
            QueryWrapper<Comment> query = new QueryWrapper<Comment>().eq("comment_class_id", articleId).eq("is_del", 0).eq("comment_state", state).eq("comment_class",1).orderByDesc("create_time");
            page = commentMapper.listCommentByPage(page, query);
        }
        return page;
    }

    @Override
    public void dateToEs() {
        esUtil.createIndex(COMMENT);
        List<Comment> list = list(new QueryWrapper<Comment>());
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(r -> {
                esUtil.save(COMMENT, DOCS, r, "commentId");
            });
        }
    }
}
