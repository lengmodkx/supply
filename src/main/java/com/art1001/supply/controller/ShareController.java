package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

@Controller
@Slf4j
@RequestMapping("/share")
public class ShareController {
    @Resource
    private ProjectService projectService;


    //导航到分享界面
    @RequestMapping("/share.html")
    public String share(@RequestParam String projectId, Model model){

        model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        return "share";
    }

    //添加一个分享
    @RequestMapping("/addShare")
    public JSONObject addShare(@RequestParam String projectId,@RequestParam String title,
                               @RequestParam String text,@RequestParam String privacy){
        JSONObject jsonObject = new JSONObject();
        try {

        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }



}
