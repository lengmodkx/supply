package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.ProjectFuncService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@RestController
@RequestMapping("projects")
public class ProjectApi {

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectFuncService projectFuncService;



    /**
     * 局部更新项目
     * @param projectId 项目id
     * @param projectName 项目名称
     * @param projectDes 项目简介
     * @param isPublic 是否公开
     * @param projectCover 项目封面
     * @param projectDel 是否移入回收站
     * @param projectStatus 是否归档
     * @return json
     */
    @PutMapping("/{projectId}")
    public JSONObject projectUpadte(@PathVariable(value = "projectId")String projectId,
                               @RequestParam(value = "projectName",required = false)String projectName,
                               @RequestParam(value = "projectDes",required = false)String projectDes,
                               @RequestParam(value = "isPublic",required = false)Integer isPublic,
                               @RequestParam(value = "projectCover",required = false)String projectCover,
                               @RequestParam(value = "projectDel",required = false)Integer projectDel,
                               @RequestParam(value = "projectStatus",required = false)Integer projectStatus){
        JSONObject object = new JSONObject();
        try {
            Project project = new Project();
            project.setProjectId(projectId);
            project.setProjectName(projectName);
            project.setProjectDes(projectDes);
            project.setIsPublic(isPublic);
            project.setProjectCover(projectCover);
            project.setProjectDel(projectDel);
            project.setProjectStatus(projectStatus);
            projectService.updateProject(project);
            object.put("result",1);
            object.put("msg","保存成功");
        }catch (Exception e){
            log.error("保存失败",e);
            throw new AjaxException(e);
        }

        return object;
    }

















    /**
     * 获取 我创建的项目，我参与的项目，我收藏的项目，项目回收站
     * @return
     */
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

    /**
     * 获取项目详情
     * @param projectId 项目id
     * @return
     */
    @GetMapping("/{projectId}")
    public JSONObject projectDetail(@PathVariable String projectId){

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
