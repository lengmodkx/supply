package com.art1001.supply.service.content.impl;

import com.art1001.supply.entity.content.Reply;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.content.ReplyMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.content.ReplyService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.EsUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Optional;

/**
 * @ClassName ReplyServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/13 10:27
 * @Discription
 */
@Service
public class ReplyServiceImpl extends ServiceImpl<ReplyMapper, Reply>implements ReplyService {

    @Resource
    private EsUtil<Reply> esUtil;

    @Resource
    private ReplyMapper replyMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public String add(String questionId, String replyContent, Integer isIncognito, Integer isDraft) {

        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("user_id", ShiroAuthenticationManager.getUserId()));

        Reply reply = Reply.builder().questionId(questionId).replyMemberId(ShiroAuthenticationManager.getUserId()).replyContent(replyContent)
                .isIncognito(isIncognito).isDraft(isDraft).createTime(System.currentTimeMillis())
                .updateTime(System.currentTimeMillis()).replyMemberImage(userEntity.getImage())
                .replyMemberName(userEntity.getUserName()).build();

        save(reply);
        return reply.getReplyId();
    }

    @Override
    public void edit(String replyId, String replyContent, Integer isIncognito, Integer isDraft) {
        Reply reply = Reply.builder().replyId(replyId).isIncognito(isIncognito).isDraft(isDraft)
                .updateTime(System.currentTimeMillis()).build();
        if (StringUtils.isNotEmpty(replyContent)) {
            reply.setReplyContent(replyContent);
        }
        updateById(reply);
    }

    @Override
    public void delete(String replyId) {
        update(new UpdateWrapper<Reply>().set("is_del",1).eq("reply_id",replyId));
    }

    @Override
    public Page<Reply> listAll(Integer isIncognito, Integer isDraft, Integer isDel, Integer pageNum) {
        Page<Reply> page;
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("isIncognito",isIncognito))
                .must(QueryBuilders.matchQuery("isDraft",isDraft))
                .must(QueryBuilders.matchQuery("isDel",isDel));
        sourceBuilder.query(boolQueryBuilder);
        page = esUtil.searchListByPage(Reply.class, sourceBuilder, REPLY, pageNum);

        if (CollectionUtils.isEmpty(page.getRecords())) {
            page.setCurrent(pageNum);
            page.setSize(20);
            QueryWrapper<Reply> query = new QueryWrapper<Reply>().eq("is_incognito", isIncognito).eq("is_draft", isDraft).eq("is_del", isDel);
            Page<Reply> replyPage = replyMapper.queryByExample(page, query);
            Optional.ofNullable(replyPage.getRecords()).ifPresent(list->list.forEach(r->esUtil.save(REPLY,DOCS,r,"replyId")));
            return replyPage;
        }
        page.setCurrent(pageNum);
        page.setSize(20);
        return page;

    }

    @Override
    public Reply getReplyById(String replyId) {
        Reply byId = getById(replyId);
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("user_id", byId.getReplyMemberId()));
        byId.setReplyMemberName(userEntity.getUserName());
        byId.setReplyMemberImage(userEntity.getImage());
        return byId;
    }
}
