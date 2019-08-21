package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.user.UserNews;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heshaohua
 * @Title: NewsApi
 * @Description: TODO 用户消息api
 * @date 2018/9/14 10:09
 **/
@Slf4j
@RequestMapping("news")
@RestController
public class NewsApi {

    @Resource
    private UserNewsService userNewsService;

    /**
     * 初始化用户消息的列表
     * @return
     */
    @RequestMapping
    public JSONObject userNewsList(@RequestParam(required = false) Boolean isRead){
        JSONObject jsonObject = new JSONObject();
        try {
            //获取该用户的消息信息
            List<UserNews> newsList = userNewsService.findAllUserNewsByUserId(ShiroAuthenticationManager.getUserId(),isRead);
            jsonObject.put("data",newsList);
            jsonObject.put("newsCount",getNewsCount());
            jsonObject.put("result",1);
            return jsonObject;
            //获取当前登录用户的消息总数
        } catch (Exception e){
            log.error("系统异常:",e);
            throw new SystemException(e);
        }
    }

    /**
     * 当用户点击一条未读的消息时 将该消息状态设为已读  并且 消息条数归0
     * @param id 消息id
     * @param isread 点击的消息是否是已读的
     * @return
     */
    @PutMapping("/{id}/read")
    public JSONObject updateIsRead(@PathVariable(value = "id") String id,
                                   @RequestParam(value = "isRead") int isread){
        JSONObject jsonObject = new JSONObject();
        try {
            if(isread == 0){
                //修改消息状态
                userNewsService.updateIsRead(id);
            }
            //获取当前登录用户的消息总数
            jsonObject.put("newsCount",getNewsCount());
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,操作失败:",e);
            throw  new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 删除一条消息
     * @param id 消息id
     * @return
     */
    @DeleteMapping("/{id}")
    public JSONObject removeNews(@PathVariable(value = "id") String id){
        JSONObject jsonObject = new JSONObject();
        try {
            userNewsService.deleteUserNewsById(id);
            jsonObject.put("result",1);
            //获取当前登录用户的消息总数
            jsonObject.put("data",getNewsCount());
        } catch (Exception e){
            log.error("系统异常,操作失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 删除所有的已读消息
     * @return
     */
    @DeleteMapping("/read")
    public JSONObject deleteAllRead(){
        JSONObject jsonObject = new JSONObject();
        try {
            userNewsService.remove(new QueryWrapper<UserNews>().eq("news_to_user", ShiroAuthenticationManager.getUserId()).eq("news_handle", "1"));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException(e);
        }
    }

    /**
     * 标记当前所有的消息为已读消息
     * @return
     */
    @PutMapping("/read")
    public JSONObject allIsRead(){
        JSONObject  jsonObject = new JSONObject();
        try {
            UserNews userNews = new UserNews();
            userNews.setNewsHandle(1);
            userNews.setNewsCount(0);
            userNewsService.update(userNews,new QueryWrapper<UserNews>().eq("news_to_user", ShiroAuthenticationManager.getUserId()));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException(e);
        }
    }

    /**
     * 获取当前用户的未读消息条数
     * @return 消息条数
     */
    public int getNewsCount() {
        //获取当前登录用户的消息总数
        return userNewsService.findUserNewsCount(ShiroAuthenticationManager.getUserId());
    }

    /**
     * 获取用户未读的消息数
     * @return
     */
    @GetMapping("/count")
    public JSONObject getCount(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", this.getNewsCount());
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException(e);
        }
    }

}
