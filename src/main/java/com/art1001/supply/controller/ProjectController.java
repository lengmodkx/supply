package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.collect.ProjectCollect;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.collect.ProjectCollectService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 项目控制器
 */
@Controller
@Slf4j
@RequestMapping("/project")
public class ProjectController {

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private ProjectCollectService projectCollectService;

    @Resource
    private RelationService relationService;

    @RequestMapping("/home.html")
    public String home(Model model){

        try {
            String userId = ShiroAuthenticationManager.getUserId();
            //我创建的任务
            List<Project> projects = projectService.findProjectByMemberId(userId);
            model.addAttribute("projects",projects);
            //我参与的任务
            List<Project> joinInproject = projectMemberService.findProjectByMemberId(userId, 0);
            model.addAttribute("joinInproject",joinInproject);
            //我收藏的项目
            List<Project> collectrojects = projectCollectService.findProjectByMemberId(userId);
            model.addAttribute("collectrojects",collectrojects);
            //项目回收站
            List<Project> delProjects = projectMemberService.findProjectByMemberId(userId,1);
            model.addAttribute("delProjects",delProjects);
            return "home";
        }catch (Exception e){
            throw  new SystemException(e);
        }
    }


    @RequestMapping("/projectList")
    @ResponseBody
    public JSONObject projectList(@RequestParam Integer label){
        JSONObject jsonObject = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();

            List<Project> projects;
            if(label==1){
                //获取我拥有的项目
                projects = projectService.findProjectByMemberId(userId);
            }else if (label==2){
                //我参与的项目
               projects = projectMemberService.findProjectByMemberId(userId,0);
            }else if (label==3) {
                //我收藏的项目
                projects = projectCollectService.findProjectByMemberId(userId);
            } else{
                //项目回收站
                projects = projectMemberService.findProjectByMemberId(userId,1);
            }

            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
            jsonObject.put("data",JSON.toJSON(projects));

        }catch (Exception e){
            log.error("请求项目列表异常：",e);
            throw new AjaxException(e);
        }

        return jsonObject;
    }

    /**
     * 添加项目
     * @param projectName 项目名称
     * @param projectDes 项目描述
     * @return
     */
    @PostMapping("/addProject")
    @ResponseBody
    public JSONObject addProject(@RequestParam String projectName, @RequestParam String projectDes){
        JSONObject jsonObject = new JSONObject();
        if(StringUtils.isEmpty(projectName)){
            jsonObject.put("result",0);
            jsonObject.put("msg","项目名称不能为空");
            return jsonObject;
        }

        if(StringUtils.isEmpty(projectDes)){
            jsonObject.put("result",0);
            jsonObject.put("msg","项目描述不能为空");
            return jsonObject;
        }

        try {

            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            Project project = new Project();
            project.setProjectName(projectName);
            project.setProjectDes(projectDes);
            project.setProjectCover("");
            project.setProjectDel(0);
            project.setCreateTime(System.currentTimeMillis());
            project.setIsPublic(0);
            project.setProjectRemind(0);
            project.setProjectMenu("[{任务},{分享},{文件},{日程},{统计},{群聊}]");
            project.setMemberId(userEntity.getId());
            project.setProjectStatus(0);
            projectService.saveProject(project);

            //初始化分组
            Relation relation = new Relation();
            relation.setRelationName("任务");
            relation.setProjectId(project.getProjectId());
            relationService.saveRelation(relation);

            //初始化菜单
            String[] menus  = new String[]{"待处理","进行中","已完成"};
            for (String menu:menus) {
                Relation relation1 = new Relation();
                relation1.setProjectId(project.getProjectId());
                relation1.setRelationName(menu);
                relation1.setParentId(relation.getRelationId());
                relationService.saveRelation(relation1);
            }

            //往项目用户关联表插入数据
            ProjectMember projectMember = new ProjectMember();
            projectMember.setProjectId(project.getProjectId());
            projectMember.setMemberId(userEntity.getId());
            projectMember.setMemberName(userEntity.getAccountName());
            projectMember.setMemberPhone(userEntity.getUserInfo().getTelephone());
            projectMember.setMemberEmail(userEntity.getUserInfo().getEmail());
            projectMember.setMemberImg(userEntity.getUserInfo().getImage());
            projectMemberService.saveProjectMember(projectMember);
            jsonObject.put("result",1);
            jsonObject.put("msg","项目创建成功");
            jsonObject.put("data",project);
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }

    /**
     * 更新项目
     * @param project 项目实体
     * @return
     */
    @PostMapping("/updateProject")
    @ResponseBody
    public JSONObject updateProject(Project project){
        JSONObject jsonObject = new JSONObject();

        try {
            projectService.updateProject(project);
            jsonObject.put("result",1);
            jsonObject.put("msg","项目更新成功");
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }

    /**
     * 删除项目
     * @param projectId 项目id
     * @return
     */
    @PostMapping("/delProject")
    @ResponseBody
    public JSONObject delProject(@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();

        try {
            projectService.deleteProjectByProjectId(projectId);
            jsonObject.put("result",1);
            jsonObject.put("msg","项目更新成功");
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }

    @PostMapping("/updateProjectMember")
    public void updateProjectMember(@RequestParam String memberIds){

    }


    /**
     * 项目收藏/取消收藏
     * @param projectId
     */
    @PostMapping("/collectProject")
    @ResponseBody
    public JSONObject collectProject(@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            int collect = projectCollectService.findCollectByProjectId(projectId);
            //如果等于0，说明收藏表不存在项目的收藏，此时插入
            if(collect==0){
                ProjectCollect projectCollect = new ProjectCollect();
                projectCollect.setProjectId(projectId);
                projectCollect.setMemberId(userEntity.getId());
                projectCollect.setMemberImg(userEntity.getUserInfo().getImage());
                projectCollect.setCreateTime(System.currentTimeMillis());
                projectCollectService.saveProjectCollect(projectCollect);
                jsonObject.put("result",1);
                jsonObject.put("msg","收藏成功");
            }else{
                //收藏表存在该项目，则取消收藏，删除收藏表的项目
                projectCollectService.deleteCollectByProjectId(projectId);
                jsonObject.put("result",1);
                jsonObject.put("msg","取消收藏成功");
            }
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

}
