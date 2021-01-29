package com.art1001.supply.service.content;

import com.art1001.supply.entity.content.Comment;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CommentService extends IService<Comment> {

    String COMMENT="comment";
    String DOCS="docs";
    /**
     * 添加评论
     * @param commentName
     * @param articleId
     */
    String addComment(String commentName, String articleId);

    /**
     * 移除评论
     * @param commentIds
     */
    void removeComment(List<String> commentIds);


    /**
     * 分页查询评论列表
     * @param pageNum
     * @param pageSize        审核状态 0未审核 1已审核
     * @param commentState    是否删除 0未删除 1已删除
     * @param isDel
     * @return
     */
    IPage<Comment> listComment(Integer pageNum, Integer pageSize, Integer commentState, Integer isDel);

    /**
     * 审核评论
     * @param commentState
     * @param commentId
     * @param checkFailReason
     */
    void updateCommentState(Integer commentState, String commentId, String checkFailReason);

    /**
     * 根据文章id查询评论列表
     * @param articleId
     * @return
     */
    Page<Comment> commentListByArticleId(String articleId,Integer pageNum,Integer state);

    void dateToEs();
}
