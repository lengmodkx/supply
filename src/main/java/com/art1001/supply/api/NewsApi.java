package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.user.UserNews;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.CommonUtils;
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
@RequestMapping("userNews")
@RestController
public class NewsApi {

    @Resource
    private UserNewsService userNewsService;

    /**
     * 初始化用户消息的列表
     * @return
     */
    @GetMapping
    public JSONObject userNewsList(){
        JSONObject jsonObject = new JSONObject();
        try {

            //获取该用户的消息信息
            List<UserNews> newsList = userNewsService.findAllUserNewsByUserId(ShiroAuthenticationManager.getUserId());
            if(CommonUtils.listIsEmpty(newsList)){
                jsonObject.put("data","无数据");
                jsonObject.put("result",1);
                return jsonObject;
            }
            jsonObject.put("newsList",newsList);
            //获取当前登录用户的消息总数
            jsonObject.put("newsCount",getNewsCount());
        } catch (Exception e){
            log.error("系统异常:",e);
            throw new SystemException(e);
        }
        return jsonObject;
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
     * 获取当前用户的未读消息条数
     * @return 消息条数
     */
    public int getNewsCount() {
        //获取当前登录用户的消息总数
        return userNewsService.findUserNewsCount(ShiroAuthenticationManager.getUserId());
    }
}
