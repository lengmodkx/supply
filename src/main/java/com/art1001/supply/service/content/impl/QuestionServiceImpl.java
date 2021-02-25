package com.art1001.supply.service.content.impl;

import com.art1001.supply.entity.content.Question;
import com.art1001.supply.entity.content.Reply;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.content.QuestionMapper;
import com.art1001.supply.mapper.content.ReplyMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.content.QuestionService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.EsUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName QuestionServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/13 10:25
 * @Discription
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private EsUtil<Question> esUtil;

    @Resource
    private ReplyMapper replyMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RestHighLevelClient esClient;


    @Override
    public Question addQuestion(String questionContent, String questionDepict, List<String> questionDepictImages, Integer isIncognito, Integer isDraft) {
        Question question = new Question();
        question.setQuestionContent(questionContent);
        question.setIsDraft(isDraft);
        question.setIsDraft(isDraft);
        question.setQuestionMemberId(ShiroAuthenticationManager.getUserId());
        question.setCreateTime(System.currentTimeMillis());
        question.setIsDel(0);
        if (StringUtils.isNotEmpty(questionDepict)) question.setQuestionDepict(questionDepict);
        if (CollectionUtils.isNotEmpty(questionDepictImages)) question.setQuestionDepictImages(questionDepictImages);
        save(question);
        return question;
    }

    @Override
    public Question removeQuestion(String questionId) {
        Question byId = getById(questionId);
        byId.setIsDel(1);
        updateById(byId);
        return byId;
    }

    @Override
    public Page<Question> listByPage(String memberId, Integer isIncognito, Integer isDraft, Integer isDel, Integer pageNum) {
        Page<Question> page;
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("questionMemberId", memberId))
                .must(QueryBuilders.matchQuery("isIncognito", isIncognito))
                .must(QueryBuilders.matchQuery("isDraft", isDraft))
                .must(QueryBuilders.matchQuery("isDel", isDel));
        sourceBuilder.query(boolQueryBuilder);
        page = esUtil.searchListByPage(Question.class, sourceBuilder, QUESTION, pageNum);
        // 按照时间排序排序
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            page.setRecords(page.getRecords().stream().sorted(Comparator.comparing(Question::getCreateTime)
                    .reversed()).collect(Collectors.toList()));
        }

        // es没有从mysql查
        if (CollectionUtils.isEmpty(page.getRecords())) {
            page.setCurrent(pageNum);
            page.setSize(20);
            QueryWrapper<Question> query = new QueryWrapper<>();
            query.eq("question_member_id", memberId).eq("is_incognito", isIncognito).eq("is_draft", isDraft).eq("is_del", isDel).orderByDesc("create_time");
            Page<Question> listByPage = questionMapper.listByPage(page, query);
            Optional.ofNullable(listByPage.getRecords()).ifPresent(list -> list.forEach(r -> esUtil.save(QUESTION, DOCS, r, "questionId")));
            return listByPage;
        }
        page.setCurrent(pageNum);
        page.setSize(20);
        return page;
    }

    @Override
    public Page<Question> listAll(Integer isIncognito, Integer isDraft, Integer isDel, Integer pageNum) {
        Page<Question> page;
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("isIncognito", isIncognito).operator(Operator.AND))
                .must(QueryBuilders.matchQuery("isDraft", isDraft).operator(Operator.AND))
                .must(QueryBuilders.matchQuery("isDel", isDel).operator(Operator.AND));
        sourceBuilder.size(20);
        sourceBuilder.query(boolQueryBuilder);
        page = esUtil.searchListByPage(Question.class, sourceBuilder, QUESTION, pageNum);
        // 按照时间排序排序
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            page.getRecords().forEach(r -> {
                r.setReplyCount(replyMapper.selectCount(new QueryWrapper<Reply>().eq("question_id", r.getQuestionId())));
                UserEntity userEntity = userMapper.selectById(r.getQuestionMemberId());
                r.setQuestionMemberName(userEntity.getUserName());
                r.setQuestionMemberImage(userEntity.getImage());
            });

            page.setRecords(page.getRecords().stream()
                    .sorted(Comparator.comparing(Question::getCreateTime).reversed())
                    .collect(Collectors.toList()));
        }

        // es没有从mysql查
        if (CollectionUtils.isEmpty(page.getRecords())) {
            page.setCurrent(pageNum);
            page.setSize(20);
            QueryWrapper<Question> query = new QueryWrapper<>();
            query.eq("is_incognito", isIncognito).eq("is_draft", isDraft).eq("is_del", isDel).orderByDesc("create_time").groupBy("question_id");
            List<Question> list1 = list(query);
            Optional.ofNullable(list1).ifPresent(list -> list.forEach(r -> {
                UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("user_id", r.getQuestionMemberId()));
                r.setQuestionMemberName(userEntity.getUserName());
                r.setQuestionMemberImage(userEntity.getImage());
                r.setReplyCount(replyMapper.selectCount(new QueryWrapper<Reply>().eq("question_id", r.getQuestionId())));
            }));
            page.setRecords(list1);
            return page;
        }
        page.setCurrent(pageNum);
        page.setSize(20);
        return page;
    }

    @Override
    public void dateToEs() {
       esUtil.createIndex(QUESTION);
        List<Question> list = list(new QueryWrapper<Question>());
        if (CollectionUtils.isNotEmpty(list)) {
            for (Question question : list) {
                esUtil.save(QUESTION, DOCS, question, "questionId");
            }
        }


    }
}
