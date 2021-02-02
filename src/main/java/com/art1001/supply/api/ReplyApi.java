package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.EsRule;
import com.art1001.supply.annotation.EsRuleType;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.content.Reply;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.content.ReplyService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * @ClassName ReplyApi
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/14 10:11
 * @Discription
 */
@RestController
@RequestMapping(value = "reply")
public class ReplyApi {

    @Resource
    private ReplyService replyService;

    /**
     * 新增回答
     * @param questionId       问题id
     * @param replyContent     回答内容
     * @param isIncognito      是否匿名0否1是
     * @param isDraft          是否草稿0否1是
     * @return
     */
    @EsRule(sort = 1,type = EsRuleType.REPLY)
    @PostMapping("/add")
    public JSONObject add(@RequestParam(value = "questionId") String questionId,
                          @RequestParam(value = "replyContent") String replyContent,
                          @RequestParam(value = "isIncognito",defaultValue = "0") Integer isIncognito,
                          @RequestParam(value = "isDraft",defaultValue = "0") Integer isDraft){
        JSONObject jsonObject = new JSONObject();
        try {
            String replyId=replyService.add(questionId,replyContent,isIncognito,isDraft);
            jsonObject.put("result",1);
            jsonObject.put("data",replyId);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 编辑回答
     * @param replyId          回答id
     * @param replyContent     回答内容
     * @param isIncognito      是否匿名0否1是
     * @param isDraft          是否草稿0否1是
     * @return
     */
    @EsRule(sort = 2,type = EsRuleType.REPLY)
    @PostMapping("/edit")
    public JSONObject edit(@RequestParam(value = "replyId") String replyId,
                           @RequestParam(value = "replyContent",required = false) String replyContent,
                           @RequestParam(value = "isIncognito",defaultValue = "0") Integer isIncognito,
                           @RequestParam(value = "isDraft",defaultValue = "0") Integer isDraft){
        JSONObject jsonObject = new JSONObject();
        try {
            replyService.edit(replyId,replyContent,isIncognito,isDraft);
            jsonObject.put("result",1);
            jsonObject.put("data",replyId);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 移除回答
     * @param replyId
     * @return
     */
    @EsRule(sort = 2,type = EsRuleType.REPLY)
    @GetMapping("/delete")
    public JSONObject delete(@RequestParam(value = "replyId") String replyId){
        JSONObject jsonObject = new JSONObject();
        try {
            replyService.delete(replyId);
            jsonObject.put("result",1);
            jsonObject.put("data",replyId);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }


    /**
     * 根据问题id获取回答列表
     * @param questionId 问题id
     * @return Page page
     */
    @GetMapping("/getReplyListByQuetionId")
    public Result getReplyListByQuetionId(@RequestParam(value = "questionId") String questionId,
                                          @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum){
        try {
            Page page=replyService.getReplyListByQuetionId(questionId,pageNum);
            return Result.success(page);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }
    /**
     * 根据id获取回答详情
     * @param replyId 回答id
     * @return Reply
     */
    @GetMapping("/getReplyById")
    public Result getReplyById(@RequestParam(value = "replyId") String replyId){
        try {
            Reply byId = replyService.getReplyById(replyId);
            return Result.success(byId);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }


    /**
     * 所有回答列表
     * @param isIncognito  是否匿名 0否 1是
     * @param isDraft      是否草稿 0否 1是
     * @param isDel        是否删除 0否 1是
     * @return
     */
    @GetMapping("/listAll")
    public Result listAll(@RequestParam(value = "isIncognito",defaultValue = "0") Integer isIncognito,
                          @RequestParam(value = "isDraft",defaultValue = "0") Integer isDraft,
                          @RequestParam(value = "isDel",defaultValue = "0")Integer isDel,
                          @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum){
        try {
            Page<Reply>page= replyService.listAll(isIncognito,isDraft,isDel,pageNum);
            return Result.success(page);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }
}
