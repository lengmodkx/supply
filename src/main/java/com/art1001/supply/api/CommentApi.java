package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.EsRule;
import com.art1001.supply.annotation.EsRuleType;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.content.Comment;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.content.CommentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName CommentApi
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/5 14:23
 * @Discription 评论api
 */
@RestController
@RequestMapping(value = "comment")
public class CommentApi {

    @Resource
    private CommentService commentService;

    /**
     * 新增评论
     * @param commentName
     * @param articleId
     * @return
     */
    @EsRule(sort = 1,type = EsRuleType.COMMEMT)
    @GetMapping("/add")
    public JSONObject addComment(@RequestParam(value ="commentName" ) String commentName,
                                 @RequestParam(value = "articleId") String articleId){
        try {
            JSONObject jsonObject = new JSONObject();
            String commentId = commentService.addComment(commentName, articleId);
            jsonObject.put("result",1);
            jsonObject.put("data",commentId);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 移除评论
     * @param commentIds
     * @return
     */
    @EsRule(sort = 2, type = EsRuleType.ARTICLE)
    @GetMapping("/remove")
    public Result removeComment(@RequestParam(value ="commentIds" ) List<String> commentIds){
        try {
            commentService.removeComment(commentIds);
            return Result.success("已删除");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 根据文章id查询评论列表
     * @param articleId
     * @return
     */
    @GetMapping("/commentListByArticleId")
    public Result commentListByArticleId(@RequestParam("articleId") String articleId,
                                         @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum){
        try {
            Page<Comment> page=commentService.commentListByArticleId(articleId,pageNum);
            return Result.success(page);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 分页查询评论列表
     * @param pageNum
     * @param pageSize
     * @param commentState  评论状态 0未审核 1审核已通过 2审核未通过
     * @param isDel         是否删除 0未删除 1已删除
     * @return
     */
    @GetMapping("/list")
    public Result list(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                       @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize,
                       @RequestParam(value = "commentState",defaultValue = "0") Integer commentState,
                       @RequestParam(value = "isDel",defaultValue = "0") Integer isDel){
        try {
            IPage<Comment>list=commentService.listComment(pageNum,pageSize,commentState,isDel);
            return Result.success(list);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 审核评论
     * @param commentState     审核状态 0未审核 1审核已通过 2审核未通过
     * @param commentId
     * @param checkFailReason  未通过原因
     * @return
     */
    @GetMapping("/updateCommentState")
    public Result updateCommentState(@RequestParam(value = "commentState") Integer commentState,
                                     @RequestParam(value = "commentId") String commentId,
                                     @RequestParam(value = "checkFailReason",required = false) String checkFailReason){
        try {
            commentService.updateCommentState(commentState,commentId,checkFailReason);
            return Result.success("已完成");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    @GetMapping("/dateToEs")
    public Result dateToEs(){
        commentService.dateToEs();
        return Result.success();
    }


}
