package com.art1001.supply.service.article.impl;

import com.art1001.supply.entity.article.Comment;
import com.art1001.supply.mapper.article.CommentMapper;
import com.art1001.supply.service.article.CommentService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
    @Override
    public String addComment(String commentName, String articleId) {
        Comment comment = Comment.builder().articleId(articleId).commentName(commentName).memberId(ShiroAuthenticationManager.getUserId())
                .createTime(System.currentTimeMillis()).updateTime(System.currentTimeMillis()).build();
        save(comment);
        return comment.getCommentId();
    }

    @Override
    public void removeComment(List<String> commentIds) {
        update(new UpdateWrapper<Comment>().set("is_del", 1).in("comment_id", commentIds));
    }

    @Override
    public IPage<Comment> listComment(Integer pageNum, Integer pageSize,Integer commentState, Integer isDel) {
        IPage<Comment> page = new Page<>(pageNum, pageSize);
        return commentMapper.selectPage(page,new QueryWrapper<Comment>().eq("comment_state",commentState).eq("is_del",isDel).orderByDesc("create_time"));
    }

    @Override
    public void updateCommentState(Integer commentState, String commentId, String checkFailReason) {
        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<Comment>().set("comment_state", commentState);
        if (StringUtils.isNotEmpty(checkFailReason)) {
            updateWrapper.set("check_fail_reason",checkFailReason);
        }
        update(updateWrapper.eq("comment_id",commentId));
    }
}
