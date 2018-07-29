package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@Slf4j
@RequestMapping("/news")
public class NewsController {


    @Resource
    private UserNewsService userNewsService;

}
