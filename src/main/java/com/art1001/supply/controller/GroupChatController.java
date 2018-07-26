package com.art1001.supply.controller;

import com.art1001.supply.controller.base.BaseController;
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
@RequestMapping("/chat")
public class GroupChatController extends BaseController {
    @Resource
    private ProjectService projectService;

    @RequestMapping("/chat.html")
    public String share(@RequestParam String projectId, Model model){

        model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        return "chat";
    }

}
