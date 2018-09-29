package com.art1001.supply.controller;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.controller.base.BaseController;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.collect.ProjectCollect;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.template.TemplateData;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.collect.ProjectCollectService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.project.ProjectAppsService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.template.TemplateDataService;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目控制器
 */
@Controller
@Slf4j
@RequestMapping("/project")
public class ProjectController extends BaseController {

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private ProjectCollectService projectCollectService;

    @Resource
    private RelationService relationService;

    @Resource
    private TaskService taskService;

    @Resource
    private FileService fileService;

    @Resource
    private ProjectAppsService funcService;

    @Resource
    private TagService tagService;

    @Resource
    private UserNewsService userNewsService;

    @Resource
    private TemplateDataService templateDataService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    @Resource
    private UserService userService;

    @RequestMapping("/project.html")
    public String home(Model model, HttpServletResponse response){

        try {
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            //我创建的任务
            List<Project> projects = projectService.findProjectByMemberId(userEntity.getId(),0);
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

            //获取当前登录用户的消息总数
            int userNewsCount = userNewsService.findUserNewsCount(ShiroAuthenticationManager.getUserId());
            model.addAttribute("newsCount",userNewsCount);

            model.addAttribute("user",userEntity);
            response.setHeader("Cache-Control","no-store");
            response.setDateHeader("Expires", 0);
            response.setHeader("Pragma","no-cache");
            return "project";
        }catch (Exception e){
            e.printStackTrace();
            throw  new SystemException(e);
        }
    }


    @GetMapping("/projectTemplate.html")
    public String projectTemplate(){
        return "tk-select-tpl";
    }

    @GetMapping("/createProject.html")
    public String createProject(){
        return "tk-proejct-create";
    }

    @GetMapping("/template.html")
    public String template(){
        return "tk-project-tempelate-create";
    }


    @RequestMapping("/projectList")
    @ResponseBody
    public JSONObject projectList(@RequestParam Integer label){
        JSONObject jsonObject = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            List<Project> projects = new ArrayList<>();
            //我创建的项目
            if(label==1){
                projects = projectService.findProjectByMemberId(userId,0);
            }

            //我参与的项目
            if(label==2){
                projects = projectMemberService.findProjectByMemberId(userId, 0);
            }

            //我收藏的项目
            if(label==3){
                projects = projectCollectService.findProjectByMemberId(userId);
            }

            //项目回收站
            if(label==4){
                projects = projectMemberService.findProjectByMemberId(userId,1);
            }
            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
            jsonObject.put("data",projects);

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
            String userId = ShiroAuthenticationManager.getUserId();
            Project project = new Project();
            project.setProjectName(projectName);
            project.setProjectDes(projectDes);
            project.setProjectCover("upload/project/bj.png");
            project.setCreateTime(System.currentTimeMillis());
            project.setMemberId(userId);
            projectService.saveProject(project);
            //写资源表
            jsonObject.put("result",1);
            jsonObject.put("projectId",project.getProjectId());
        } catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }


    /**
     * 模板创建
     */
    @PostMapping("/projectTemplate")
    @ResponseBody
    public JSONObject projectTemplate(@RequestParam String projectName,@RequestParam String projectDes,@RequestParam(required = false)String templateFlag){
        JSONObject jsonObject = new JSONObject();
        if(StringUtils.isEmpty(projectName)){
            jsonObject.put("result",0);
            jsonObject.put("msg","项目名称不能为空");
            return jsonObject;
        }
        try {
            //项目基本信息
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            Project project = new Project();
            project.setProjectName(projectName);
            project.setProjectDes(projectDes);
            project.setMemberId(userEntity.getId());
            projectService.saveProject(project);
            //初始化项目功能菜单
            String[] funcs = new String[]{"任务","分享","文件","日程","群聊","统计"};
            funcService.saveProjectFunc(Arrays.asList(funcs),project.getProjectId());

            //初始化分组
            Relation relation = new Relation();
            relation.setProjectId(project.getProjectId());
            relation.setRelationName("任务");
            relationService.saveRelation(relation);

            List<TemplateData> menus = templateDataService.findByTemplateId("879fd7e79b9d11e8a601c85b76c405c2");
            relationService.saveRelationBatch2(menus,project.getProjectId(),relation.getRelationId());
            List<Relation> relationList = relationService.findAllMenuInfoByGroupId(relation.getRelationId());
            for(int i=0;i<menus.size();i++){
                String menuId = menus.get(i).getId();
                String relationId = relationList.get(i).getRelationId();
                List<TemplateData> templateDataList = templateDataService.findByParentId(menuId);
                taskService.saveTaskBatch(project.getProjectId(),relationId,templateDataList);
            }

            //往项目用户关联表插入数据
            ProjectMember projectMember = new ProjectMember();
            projectMember.setProjectId(project.getProjectId());
            projectMember.setMemberId(userEntity.getId());
            projectMemberService.saveProjectMember(projectMember);
            //初始化项目文件夹
            fileService.initProjectFolder(project);
            //写资源表
            jsonObject.put("result",1);
            jsonObject.put("msg","项目创建成功");
            jsonObject.put("projectId",project.getProjectId());

        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * 更新菜单序
     * @param ids
     * @return
     */
    @RequestMapping("/updateMenusOrder")
    @ResponseBody
    public JSONObject updateMenusOrder(String[] ids){
           JSONObject jsonObject = new JSONObject();
           try {
                for(int i=0;i<ids.length;i++){
                    Relation relation = new Relation();
                    relation.setRelationId(ids[i]);
                    relation.setOrder(i);
                    relation.setUpdateTime(System.currentTimeMillis());
                    relationService.updateRelation(relation);
                }
                jsonObject.put("result",1);
                jsonObject.put("msg",0);
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




    //上传项目图片
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

    //任务界面初始化
    @GetMapping("/task.html")
    public String mainpage(@RequestParam String projectId, String groupId,Model model){
        try {

            Relation relation1 = new Relation();
            relation1.setParentId(projectMemberService.findDefaultGroup(projectId,ShiroAuthenticationManager.getUserId()));
            relation1.setLable(1);
            List<Relation> taskMenu = relationService.findRelationAllList(relation1);
            model.addAttribute("taskMenus",taskMenu);
            model.addAttribute("currentGroup",relation1);

            //获取当前登录用户的消息总数
            int userNewsCount = userNewsService.findUserNewsCount(ShiroAuthenticationManager.getUserId());
            model.addAttribute("newsCount",userNewsCount);

            Project project = projectService.findProjectByProjectId(projectId);
            model.addAttribute("project",project);
            model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        }catch (Exception e){
            e.printStackTrace();
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
    public String menuList(@RequestParam String menuId,@RequestParam String menuName,@RequestParam String projectId,Model model){
        model.addAttribute("menuId",menuId);
        model.addAttribute("menuName",menuName);
        model.addAttribute("projectId",projectId);
        List<Task> taskList = taskService.findTaskByMemberId(menuId);
        model.addAttribute("taskList",taskList);
        return "tk-menu-list";
    }

    @PostMapping("/updateMenuName")
    @ResponseBody
    public JSONObject updateMenuName(@RequestParam String menuId,@RequestParam String menuName,@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try{
            Relation relation = new Relation();
            relation.setRelationId(menuId);
            relation.setRelationName(menuName);
            relationService.updateRelation(relation);
            jsonObject.put("result",1);
            jsonObject.put("msg","修改成功");
            jsonObject.put("menuId",menuId);
            jsonObject.put("menuName",menuName);
            jsonObject.put("type","更新菜单名称");
            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(jsonObject.toString()));
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }


    @PostMapping("findProjectFile")
    @ResponseBody
    public JSONObject findProjectFile(String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> list = fileService.findFileByProjectId(projectId);
            jsonObject.put("data",list);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,拉取失败,{}",e);
            jsonObject.put("msg","数据拉取失败!");
            jsonObject.put("result",0);
        }
        return jsonObject;
    }

    /**
     * 查询项目成员
     */
    @PostMapping("findProjectMember")
    @ResponseBody
    public JSONObject findProjectMember(String projectId){
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        try{
            List<ProjectMember> projectMembers = projectMemberService.findByProjectId(projectId);
            List<ProjectMember>  memberFilter = projectMembers.stream().filter(projectMember -> !projectMember.getMemberId().equals(userEntity.getId())).distinct().collect(Collectors.toList());
            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
            jsonObject.put("members",memberFilter);
            jsonObject.put("user",userEntity);
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 弹出回收站页面 并且查询出回收站中的任务
     * @param projectId 项目id
     * @param model
     * @return
     */
    @RequestMapping("viewRecycleBin.html")
    public String viewRecycleBin(String projectId,Model model){
        //查询出该项目下 回收站中的任务
        model.addAttribute("tasks",taskService.findRecycleBin(projectId));
        model.addAttribute("projectId",projectId);
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        model.addAttribute("menus",relationService.findMenusByProjectId(projectId));
        return "tk-recycle-bin";
    }

    /**
     * 在回收站页面点击左边选项卡时 查询相应的信息
     * @param projectId 项目id
     * @param type 要查询的类型
     * @return
     */
    @PostMapping("recycleBinInfo")
    @ResponseBody
    public JSONObject recycleBinInfo(String projectId, String type){
        JSONObject jsonObject = new JSONObject();
        try {
            List<RecycleBinVO> recycleBinVOS = projectService.recycleBinInfo(projectId, type);
            jsonObject.put("data",recycleBinVOS);
        } catch (Exception e){
            jsonObject.put("msg","系统异常,数据拉取失败!");
            jsonObject.put("result",0);
            log.error("系统异常,数据拉取失败!,{}",e);
        }
        return jsonObject;
    }

}