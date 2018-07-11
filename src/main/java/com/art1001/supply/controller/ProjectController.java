package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.collect.ProjectCollect;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectFunc;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.collect.ProjectCollectService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.project.ProjectFuncService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
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

    @Resource
    private UserService userService;

    @Resource
    private TaskService taskService;

    @Resource
    private FileService fileService;

    @Resource
    private ProjectFuncService funcService;

    @Resource
    private TagService tagService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;
    @RequestMapping("/project.html")
    public String home(Model model){

        try {
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            //我创建的任务
            List<Project> projects = projectService.findProjectByMemberId(userEntity.getId());
            model.addAttribute("projects",projects);
            //我参与的任务
            List<Project> joinInproject = projectMemberService.findProjectByMemberId(userEntity.getId(), 0);
            model.addAttribute("joinInproject",joinInproject);
            //我收藏的项目
            List<Project> collectrojects = projectCollectService.findProjectByMemberId(userEntity.getId());
            model.addAttribute("collectrojects",collectrojects);
            //项目回收站
            List<Project> delProjects = projectMemberService.findProjectByMemberId(userEntity.getId(),1);
            model.addAttribute("delProjects",delProjects);

            model.addAttribute("user",userEntity);
            return "project";
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
            project.setProjectCover("upload/project/bj.png");
            project.setProjectDel(0);
            project.setCreateTime(System.currentTimeMillis());
            project.setIsPublic(0);
            project.setProjectRemind(0);
            project.setMemberId(userEntity.getId());
            project.setProjectStatus(0);
            projectService.saveProject(project);

            //初始化项目功能菜单
            String[] funcs = new String[]{"任务","分享","文件","日程","统计","群聊"};
            for (int i=0;i<funcs.length;i++) {
                ProjectFunc projectFunc = new ProjectFunc();
                projectFunc.setFuncName(funcs[i]);
                projectFunc.setIsOpen(1);
                projectFunc.setFuncOrder(i);
                projectFunc.setProjectId(project.getProjectId());
                funcService.saveProjectFunc(projectFunc);
            }

            //初始化分组
            Relation relation = new Relation();
            relation.setRelationName("任务");
            relation.setProjectId(project.getProjectId());
            relation.setLable(0);
            relation.setRelationDel(0);
            relation.setCreateTime(System.currentTimeMillis());
            relation.setUpdateTime(System.currentTimeMillis());
            relationService.saveRelation(relation);

            //初始化菜单
            String[] menus  = new String[]{"待处理","进行中","已完成"};
            for (String menu:menus) {
                Relation relation1 = new Relation();
                relation1.setProjectId(project.getProjectId());
                relation1.setRelationName(menu);
                relation1.setParentId(relation.getRelationId());
                relation1.setLable(1);
                relation1.setRelationDel(0);
                relation1.setCreateTime(System.currentTimeMillis());
                relation1.setUpdateTime(System.currentTimeMillis());
                relationService.saveRelation(relation1);
            }

            //往项目用户关联表插入数据
            ProjectMember projectMember = new ProjectMember();
            projectMember.setProjectId(project.getProjectId());
            projectMember.setMemberId(userEntity.getId());
            projectMember.setMemberName(userEntity.getUserName());
            projectMember.setMemberPhone(userEntity.getUserInfo().getTelephone());
            projectMember.setMemberEmail(userEntity.getUserInfo().getEmail());
            projectMember.setMemberImg(userEntity.getUserInfo().getImage());
            projectMember.setCreateTime(System.currentTimeMillis());
            projectMember.setUpdateTime(System.currentTimeMillis());
            projectMember.setMemberLable(1);
            projectMemberService.saveProjectMember(projectMember);

            //初始化项目文件夹
            fileService.initProjectFolder(project);


            //写资源表


            jsonObject.put("result",1);
            jsonObject.put("msg","项目创建成功");
            jsonObject.put("projectId",project.getProjectId());
            messagingTemplate.convertAndSend("/topic/subscribe", new ServerMessage(JSON.toJSON(project).toString()));
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
            jsonObject.put("msg","项目删除成功");
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }

    /**
     * 给项目添加成员
     * @param projectId
     * @param memberIds
     * @return
     */
    @PostMapping("/addProjectMember")
    @ResponseBody
    public JSONObject addProjectMember(@RequestParam String projectId,@RequestParam String memberIds){
        JSONObject jsonObject = new JSONObject();
        try{

            if(StringUtils.isEmpty(memberIds)){
                jsonObject.put("result",0);
                jsonObject.put("msg","请选择成员");
            }else{
                String[] memberId = memberIds.split(",");
                for (int i=0;i<memberId.length;i++){
                    UserEntity userEntity = userService.findById(memberId[i]);
                    ProjectMember projectMember = new ProjectMember();
                    projectMember.setProjectId(projectId);
                    projectMember.setMemberId(memberId[i]);
                    projectMember.setMemberName(userEntity.getAccountName());
                    projectMember.setMemberPhone(userEntity.getUserInfo().getTelephone());
                    projectMember.setMemberEmail(userEntity.getUserInfo().getEmail());
                    projectMember.setMemberImg(userEntity.getUserInfo().getImage());
                    projectMember.setCreateTime(System.currentTimeMillis());
                    projectMember.setUpdateTime(System.currentTimeMillis());
                    projectMember.setMemberLable(0);
                    projectMemberService.saveProjectMember(projectMember);
                }

                jsonObject.put("result",1);
                jsonObject.put("msg","添加成功");
            }
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
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
            int collect = projectCollectService.findCollectByProjectId(projectId,userEntity.getId());
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

    /**
     * 移除项目成员 支持单独删除和批量删除
     */
    @PostMapping("/delProjectMember")
    @ResponseBody
    public JSONObject delProjectMember(@RequestParam String id){
        JSONObject jsonObject = new JSONObject();
        try {
            if(StringUtils.isEmpty(id)){
                jsonObject.put("result",0);
                jsonObject.put("msg","请选择组员");
            }else{
                String[] ids = id.split(",");
                for (int i=0;i<ids.length;i++){
                    projectMemberService.deleteProjectMemberById(ids[i]);
                }
                jsonObject.put("result",0);
                jsonObject.put("msg","删除成功");
            }
        }catch (Exception e){
            throw  new AjaxException(e);
        }
        return jsonObject;
    }


    @GetMapping("/projectSetting")
    public String projectSetting(@RequestParam String projectId, Model model){
        String userId = ShiroAuthenticationManager.getUserId();
        Project project = projectService.findProjectByProjectId(projectId);
        //获取项目拥有着信息
        UserEntity userEntity = userService.findById(project.getMemberId());

        model.addAttribute("project",project);
        model.addAttribute("user",userEntity);
        if(userId.equals(project.getMemberId())){
            model.addAttribute("hasPermission",1);
        }else{
            model.addAttribute("hasPermission",0);
        }
        return "objsetting";
    }


    @PostMapping("/upload")
    @ResponseBody
    public JSONObject uploadCover(@RequestParam String projectId,
                                  MultipartFile file){
        JSONObject jsonObject = new JSONObject();
        try {
            //先删除阿里云上项目的图片然后再上传
            Project project = projectService.findProjectByProjectId(projectId);
            //不删除项目的默认图片
            if(!"upload/project/bj.png".equals(project.getProjectCover())){
                AliyunOss.deleteFile(project.getProjectCover());
            }

            String filename = System.currentTimeMillis()+".jpg";
            AliyunOss.uploadInputStream(Constants.PROJECT_IMG + filename,file.getInputStream());
            Project project1 = new Project();
            project1.setProjectId(projectId);
            project1.setProjectCover(Constants.PROJECT_IMG + filename);
            projectService.updateProject(project1);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "上传成功");
            jsonObject.put("data",Constants.PROJECT_IMG + filename);
        }catch (Exception e){
            throw new SystemException(e);
        }
        return jsonObject;
    }

    @PostMapping("/updateFunc")
    @ResponseBody
    public JSONObject updateFunc(@RequestParam Integer[] funcIds){
        JSONObject jsonObject = new JSONObject();
        try {

            for (int i=0;i<funcIds.length;i++){
                ProjectFunc projectFunc = new ProjectFunc();
                projectFunc.setFuncOrder(i);
                projectFunc.setFuncId(funcIds[i]);
                funcService.updateProjectFunc(projectFunc);
            }
            jsonObject.put("result", 1);
            jsonObject.put("msg", "更新成功");
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }






    @GetMapping("/task.html")
    public String mainpage(@RequestParam String projectId,Model model){
        try {
            //查询项目任务分组
            Relation relation = new Relation();
            relation.setProjectId(projectId);
            relation.setLable(0);
            List<Relation> taskGroups = relationService.findRelationAllList(relation);

            //取第0个任务分组的菜单
            Relation relation1 = new Relation();
            relation1.setParentId(taskGroups.get(0).getRelationId());
            relation1.setLable(1);
            List<Relation> taskMenu = relationService.findRelationAllList(relation1);


            Project project = projectService.findProjectByProjectId(projectId);
            List<File> fileList = fileService.findChildFile(projectId, "0", 0);
            model.addAttribute("fileList", fileList);
            model.addAttribute("project",project);
            model.addAttribute("taskGroups",taskGroups);
            model.addAttribute("currentGroup",taskGroups.get(0).getRelationId());
            model.addAttribute("taskMenus",taskMenu);
            model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        }catch (Exception e){
            throw new SystemException(e);
        }

        return "mainpage";
    }


    @GetMapping("/addtask.html")
    public String addtask(@RequestParam String projectId,@RequestParam String taskMenuId, Model model){
        try {
            List<Tag> tagList = tagService.findByProjectId(projectId);
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            model.addAttribute("user",userEntity);
            model.addAttribute("tagList",tagList);
            model.addAttribute("projectId",projectId);
            model.addAttribute("taskMenuId",taskMenuId);
        }catch (Exception e){
            throw new SystemException(e);
        }

        return "addtask";
    }


    @GetMapping("/menuList.html")
    public String menuList(){
        return "tk-caidanliebiao";
    }


    @PostMapping("/searchMember")
    public JSONObject searchMember(@RequestParam String keyword){
        JSONObject jsonObject = new JSONObject();
        try{
            UserEntity userEntity = userService.findByKey(keyword);
            jsonObject.put("data",userEntity);
            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }



}