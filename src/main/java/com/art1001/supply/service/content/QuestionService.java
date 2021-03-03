package com.art1001.supply.service.content;

import com.art1001.supply.entity.content.Question;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface QuestionService extends IService<Question> {

    String QUESTION="question";
    String DOCS="docs";

    /**
     * 发布提问
     *
     * @param questionContent      提出的问题
     * @param questionDepict       问题描述
     * @param questionDepictImages 问题描述图片
     * @param isIncognito          是否匿名（0否 1是）
     * @param isDraft              是否草稿（0否1是）
     * @return
     */
    Question addQuestion(String questionContent, String questionDepict, List<String> questionDepictImages, Integer isIncognito, Integer isDraft);

    /**
     * 移除提问
     * @param questionId
     */
    Question removeQuestion(String questionId);

    /**
     * 根据用户id分页查询提问列表
     * @param memberId
     * @param isIncognito
     * @param isDraft
     * @param isDel
     * @param pageNum
     * @return
     */
    Page<Question> listByPage(String memberId, Integer isIncognito, Integer isDraft, Integer isDel, Integer pageNum);

    /**
     * 分页查询所有提问列表
     * @param isIncognito    是否匿名
     * @param isDraft        是否是草稿
     * @param isDel          是否删除
     * @param pageNum
     * @return
     */
    Page<Question> listAll(Integer isIncognito, Integer isDraft, Integer isDel, Integer pageNum);

    /**
     * 数据放到es
     */
    void dateToEs();
}
