package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 项目
 * @author 汪亚锋
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
    private RelationService relationService;

    @Resource
    private ProjectMemberService projectMemberService;

    /**
     * 创建项目
     *
     * @param projectName 项目名称
     * @param projectDes  项目描述
     * @param orgId 企业id
     * @return
     */
    @PostMapping
    public JSONObject createProject(@RequestParam(value = "orgId",defaultValue = "0",required = false) String orgId,
                                    @RequestParam(value = "projectName") String projectName,
                                    @RequestParam(value = "projectDes") String projectDes) {
        JSONObject object = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            Project project = new Project();
            project.setProjectName(projectName);
            project.setProjectDes(projectDes);
            project.setMemberId(userId);
            projectService.saveProject(project);
            //写资源表
            object.put("result", 1);
            object.put("data", project.getProjectId());
            object.put("msg", "创建成功");
        } catch (Exception e) {
            log.error("系统异常,项目创建失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }


    /**
     * 局部更新项目
     *
     * @param projectId     项目id
     * @param projectName   项目名称
     * @param projectDes    项目简介
     * @param isPublic      是否公开
     * @param projectCover  项目封面
     * @param projectDel    是否移入回收站
     * @param projectStatus 是否归档
     * @return json
     */
    @PutMapping("/{projectId}")
    public JSONObject projectUpadte(@PathVariable(value = "projectId") String projectId,
                                    @RequestParam(value = "projectName", required = false) String projectName,
                                    @RequestParam(value = "projectDes", required = false) String projectDes,
                                    @RequestParam(value = "isPublic", required = false) Integer isPublic,
                                    @RequestParam(value = "projectCover", required = false) String projectCover,
                                    @RequestParam(value = "projectDel", required = false) Integer projectDel,
                                    @RequestParam(value = "projectStatus", required = false) Integer projectStatus) {
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
            object.put("result", 1);
            object.put("msg", "更新成功");
        } catch (Exception e) {
            log.error("保存失败:", e);
            throw new AjaxException(e);
        }

        return object;
    }

    /**
     * 获取 我创建的项目，我参与的项目，我收藏的项目，项目回收站
     *
     * @return
     */
    @GetMapping
    public JSONObject projects() {
        JSONObject object = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            List<Project> projectList = projectService.findProjectByUserId(userId);
            object.put("result", 1);
            object.put("data", projectList);
            object.put("msg", "获取成功");
        } catch (Exception e) {
            log.error("系统异常,信息获取失败:", e);
            throw new SystemException(e);
        }
        return object;
    }

    /**
     * 获取项目详情
     *
     * @param projectId 项目id
     * @return
     */
    @GetMapping("/{projectId}")
    public JSONObject projectDetail(@PathVariable String projectId) {
        JSONObject object = new JSONObject();
        try {
            Project project = projectService.findProjectByProjectId(projectId);
            object.put("result", 1);
            object.put("data", project);
            object.put("msg", "获取成功");
        } catch (Exception e) {
            log.error("系统异常,信息获取失败:", e);
            throw new SystemException(e);
        }
        return object;
    }

    /**
     * 删除项目
     *
     * @param projectId 项目id
     * @return
     */
    @DeleteMapping("/{projectId}")
    public JSONObject deleteProject(@PathVariable String projectId) {
        JSONObject object = new JSONObject();
        try {
            projectService.removeById(projectId);
            object.put("result", 1);
            object.put("msg", "删除成功");
        } catch (Exception e) {
            log.error("系统异常,项目删除失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    //任务界面初始化
    @GetMapping("/{projectId}/tasks")
    public JSONObject mainpage(@PathVariable String projectId) {
        JSONObject object = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            String groupId = projectMemberService.findDefaultGroup(projectId, userId);

            //查询项目默认分组
            Relation relation = new Relation();
            relation.setParentId(groupId);
            relation.setLable(1);
            List<Relation> taskMenu = relationService.findRelationAllList(relation);
            Project project = projectService.findProjectByProjectId(projectId);

            object.put("result", 1);
            object.put("msg", "删除成功");
            object.put("menu",taskMenu);
            object.put("project",project);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
      return object;
    }

    /**
     * 项目收藏/取消收藏
     * @param projectId 项目id
     */
    @PutMapping("/{projectId}/collect")
    public JSONObject collectProject(@PathVariable(value = "projectId") String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            ProjectMember projectMember = projectMemberService.getOne(new QueryWrapper<ProjectMember>().eq("project_id",projectId).eq("member_id",userId));
            //如果等于0，说明收藏表不存在项目的收藏，此时插入
            if(projectMember.getCollect()==0){
                projectMember.setCollect(1);
                projectMemberService.updateById(projectMember);
                jsonObject.put("result",1);
                jsonObject.put("msg","收藏成功");
            }else{
                //收藏表存在该项目，则取消收藏，删除收藏表的项目
                projectMember.setCollect(0);
                projectMemberService.updateById(projectMember);
                jsonObject.put("result",1);
                jsonObject.put("msg","取消收藏成功");
            }
            jsonObject.put("data",projectService.findProjectByUserId(userId));
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }

    /**
     * 项目归档/取消归档
     * @param projectId 项目id
     * @param status 要操作的 标识
     */
    @PutMapping("/{projectId}/status")
    public JSONObject updateStatus(@PathVariable String projectId,@RequestParam(value = "status") Integer status){
        JSONObject object = new JSONObject();
        try{
            String userId = ShiroAuthenticationManager.getUserId();
            Project project = new Project();
            project.setProjectId(projectId);
            if(status == 1){
                project.setProjectStatus(0);
                object.put("msg","取消项目归档成功!");
            } else{
                project.setProjectStatus(1);
                object.put("msg","项目已归档");
            }
            projectService.updateById(project);
            object.put("result",1);
            object.put("data",projectService.findProjectByUserId(userId));
        }catch(Exception e){
            throw new AjaxException("项目归档操作异常!",e);
        }
        return object;
    }
}
