package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.user.UserNews;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * 初始化用户消息的列表
     * @return
     */
    @GetMapping("userNewsList.html")
    public String userNewsList(Model model){
        //获取该用户的消息信息
        List<UserNews> newsList = userNewsService.findAllUserNewsByUserId(ShiroAuthenticationManager.getUserId());
        model.addAttribute("newsList",newsList);

        //获取当前登录用户的消息总数
        int userNewsCount = userNewsService.findUserNewsCount(ShiroAuthenticationManager.getUserId());
        model.addAttribute("newsCount",userNewsCount);
        return "notice";
    }

}
