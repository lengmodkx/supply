package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserNews;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/news")
public class NewsController {


    @Resource
    private UserNewsService userNewsService;

    @Resource
    private TaskService taskService;

    @Resource
    private ShareService shareService;

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private FileService fileService;

    /**
     * 初始化用户消息的列表
     * @return
     */
    @GetMapping("userNewsList.html")
    public String userNewsList(Model model){
        //当前登录用户信息
        model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        //获取该用户的消息信息
        List<UserNews> newsList = userNewsService.findAllUserNewsByUserId(ShiroAuthenticationManager.getUserId());
        model.addAttribute("newsList",newsList);
        //获取当前登录用户的消息总数
        model.addAttribute("newsCount",getNewsCount());
        return "notice";
    }

    /**
     * 当用户点击一条未读的消息时 将该消息状态设为已读  并且 消息条数归0
     * @param id 消息id
     * @param publicId (任务, 文件, 日程, 分享) id
     * @param publicType 要查询的类型
     * @param isread 点击的消息是否是已读的
     * @return
     */
    @RequestMapping("updateIsRead")
    @ResponseBody
    public JSONObject updateIsRead(String id, int isread,Model model){
        JSONObject jsonObject = new JSONObject();
        try {
            if(isread == 0){
                //修改消息状态
                userNewsService.updateIsRead(id);
            }

            //获取当前登录用户的消息总数
            jsonObject.put("newsCount",getNewsCount());
        } catch (Exception e){
            e.printStackTrace();
            log.error("系统异常,操作失败!");
            jsonObject.put("msg","系统异常,操作失败!");
            jsonObject.put("result",0);
        }
        return jsonObject;
    }


    /**
     * 删除一条消息
     * @param id 消息id
     * @return
     */
    @PostMapping("removeNews")
    @ResponseBody
    public JSONObject removeNews(String id){
        JSONObject jsonObject = new JSONObject();
        try {
            userNewsService.deleteUserNewsById(id);
            jsonObject.put("result",1);
            //获取当前登录用户的消息总数
            jsonObject.put("data",getNewsCount());
        } catch (Exception e){
            jsonObject.put("msg","系统异常,操作失败!");
            jsonObject.put("result",0);
            log.error("系统异常,操作失败!");
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