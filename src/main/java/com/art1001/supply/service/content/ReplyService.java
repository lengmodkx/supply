package com.art1001.supply.service.content;


import com.art1001.supply.entity.content.Reply;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ReplyService extends IService<Reply> {

    String REPLY ="reply";

    String DOCS ="docs";
    /**
     * 新增回答
     * @param questionId       问题id
     * @param replyContent     回答内容
     * @param isIncognito      是否匿名0否1是
     * @param isDraft          是否草稿0否1是
     * @return
     */
    Reply add(String questionId, String replyContent, Integer isIncognito, Integer isDraft);

    /**
     * 编辑回答
     * @param replyId          回答id
     * @param replyContent     回答内容
     * @param isIncognito      是否匿名0否1是
     * @param isDraft          是否草稿0否1是
     * @return
     */
    Reply edit(String replyId, String replyContent, Integer isIncognito, Integer isDraft);

    /**
     * 移除回答
     * @param replyId
     */
    Reply delete(String replyId);

    /**
     * 所有回答列表
     * @param isIncognito  是否匿名 0否 1是
     * @param isDraft      是否草稿 0否 1是
     * @param isDel        是否删除 0否 1是
     * @return
     */
    Page<Reply> listAll(Integer isIncognito, Integer isDraft, Integer isDel, Integer pageNum);


    /**
     * 根据id获取回答详情
     * @param replyId 回答id
     * @return Reply
     */
    Reply getReplyById(String replyId);

    /**
     * 根据问题id获取回答列表
     * @param questionId 问题id
     * @return Page page
     */
    Page  getReplyListByQuetionId(String questionId,Integer pageNum);

    void dateToEs();
}
