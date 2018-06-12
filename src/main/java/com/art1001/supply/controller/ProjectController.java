package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.user.UserEntity;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            List<Project> projects = projectService.findProjectByMemberId(userId);
            model.addAttribute("projects",projects);
            return "home";
        }catch (Exception e){
            throw  new SystemException(e);
        }
    }


    @RequestMapping("/projectList")
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

            String userId = ShiroAuthenticationManager.getUserId();
//            Project project = new Project();
//            project.setProjectName(projectName);
//            project.setProjectDes(projectDes);
//            project.setProjectCover("");
//            project.setProjectDel(0);
//            project.setCreateTime(System.currentTimeMillis());
//            project.setIsPublic(0);
//            project.setProjectRemind(0);
//            project.setProjectMenu("[{任务},{分享},{文件},{日程},{统计},{群聊}]");
//            project.setMemberId(userId);
//            project.setProjectStatus(0);
//            projectService.saveProject(project);
//
//            //初始化分组
//            Relation relation = new Relation();
//            relation.setRelationName("任务");
//            relation.setProjectId(project.getProjectId());
//            relationService.saveRelation(relation);
//
//            //初始化菜单
//            String[] menus  = new String[]{"待处理","进行中","已完成"};
//            for (String menu:menus) {
//                Relation relation1 = new Relation();
//                relation1.setProjectId(project.getProjectId());
//                relation1.setRelationName(menu);
//                relation1.setParentId(relation.getRelationId());
//                relationService.saveRelation(relation1);
//            }

            jsonObject.put("result",1);
            jsonObject.put("msg","项目创建成功");
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
}
