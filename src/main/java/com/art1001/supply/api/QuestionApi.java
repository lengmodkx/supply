package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.EsRule;
import com.art1001.supply.annotation.EsRuleType;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.content.Question;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.content.QuestionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName QuestionApi
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/13 15:28
 * @Discription 问题api
 */
@RestController
@RequestMapping(value = "question")
public class QuestionApi {

    @Resource
    private QuestionService questionService;



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
    @EsRule(sort = 1, type = EsRuleType.QUESTION)
    @PostMapping("/add")
    public JSONObject addQuestion(@RequestParam(value = "questionContent") String questionContent,
                                  @RequestParam(value = "questionDepict", required = false) String questionDepict,
                                  @RequestParam(value = "questionDepictImages", required = false) List<String> questionDepictImages,
                                  @RequestParam(value = "isIncognito", defaultValue = "0") Integer isIncognito,
                                  @RequestParam(value = "isDraft", defaultValue = "0") Integer isDraft) {
        JSONObject jsonObject = new JSONObject();
        try {
            Question question = questionService.addQuestion(questionContent, questionDepict, questionDepictImages, isIncognito, isDraft);
            jsonObject.put("result", 1);
            jsonObject.put("data", question);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 移除提问
     *
     * @param questionId
     * @return
     */
    @EsRule(sort = 2, type = EsRuleType.QUESTION)
    @GetMapping("/removeQuestion")
    public JSONObject removeQuestion(@RequestParam(value = "questionId") String questionId) {
        JSONObject jsonObject = new JSONObject();
        try {
            Question question = questionService.removeQuestion(questionId);
            jsonObject.put("result", 1);
            jsonObject.put("data", question);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 根据用户id分页查询提问列表
     *
     * @param memberId    提问人id
     * @param isIncognito 是否匿名
     * @param isDraft     是否是草稿
     * @param isDel       是否删除
     * @param pageNum
     * @return
     */
    @GetMapping("/listPageByMemberId")
    public Result listPageByMemberId(@RequestParam(value = "memberId") String memberId,
                                     @RequestParam(value = "isIncognito", defaultValue = "0") Integer isIncognito,
                                     @RequestParam(value = "isDraft", defaultValue = "0") Integer isDraft,
                                     @RequestParam(value = "isDel", defaultValue = "0") Integer isDel,
                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        try {
            Page<Question> page = questionService.listByPage(memberId, isIncognito, isDraft, isDel, pageNum);
            return Result.success(page);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 分页查询所有提问列表
     *
     * @param isIncognito 是否匿名
     * @param isDraft     是否是草稿
     * @param isDel       是否删除
     * @param pageNum
     * @return
     */
    @GetMapping("/listAll")
    public Result listAll(@RequestParam(value = "isIncognito", defaultValue = "0") Integer isIncognito,
                          @RequestParam(value = "isDraft", defaultValue = "0") Integer isDraft,
                          @RequestParam(value = "isDel", defaultValue = "0") Integer isDel,
                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        try {
            Page<Question> page = questionService.listAll(isIncognito, isDraft, isDel, pageNum);
            return Result.success(page);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    @GetMapping("/dateToEs")
    public Result dateToEs() throws IOException {
        questionService.dateToEs();
        return Result.success();
    }


}
