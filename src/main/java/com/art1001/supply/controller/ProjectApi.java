package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("projects")
public class ProjectApi {

    @Resource
    private ProjectService projectService;

    @GetMapping
    public JSONObject projects(){
        JSONObject object = new JSONObject();
        try{

            String userId = ShiroAuthenticationManager.getUserId();
            //我创建的项目
            List<Project> projectCreate = projectService.findProjectByMemberId(userId,0);

            //我参与的项目
            List<Project> projectJoin = projectService.findProjectByUserId(userId,0);

            //我收藏的项目
            List<Project> projectCollect = projectService.findProjectByUserId(userId,1);

            //项目回收站
            List<Project> projectDel = projectService.findProjectByMemberId(userId,1);

            object.put("result",1);
            object.put("projectCreate",projectCreate);
            object.put("projectJoin",projectJoin);
            object.put("projectCollect",projectCollect);
            object.put("projectDel",projectDel);
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return object;
    }


    @GetMapping("/{projectId}")
    public JSONObject getProject(@PathVariable String projectId){

        JSONObject object = new JSONObject();
        try{
            Project project = projectService.findProjectByProjectId(projectId);
            object.put("result",1);
            object.put("project",project);
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return object;
    }











}
