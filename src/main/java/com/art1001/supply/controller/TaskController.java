package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.art1001.supply.common.Constants;
import com.art1001.supply.common.Push;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.binding.BindingVo;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.*;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserNews;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.FileUtils;
import com.art1001.supply.util.IdGen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 任务控制器，关于任务的操作
 */
@Controller
@Slf4j
@RequestMapping("task")
public class TaskController {

    /** 任务逻辑层接口 */
    @Resource
    private TaskService taskService;

    /** 标签逻辑层接口 */
    @Resource
    private TagService tagService;

    /** 文件逻辑层接口 */
    @Resource
    private FileService fileService;

    /** 用户逻辑层接口 */
    @Resource
    private UserService userService;

    /** 任务 分组、菜单 逻辑层接口   */
    @Resource
    private RelationService relationService;

    /** 项目的逻辑层接口 */
    @Resource
    private ProjectService projectService;

    /** 关联绑定的逻辑层接口 */
    @Resource
    private BindingService bindingService;

    /** 用于订阅推送消息 */
    @Resource
    private SimpMessagingTemplate messagingTemplate;

    /** 日志逻辑层接口 */
    @Resource
    private LogService logService;

    @Resource
    private ProjectMemberService projectMemberService;

    /** 用户消息逻辑层接口 */
    @Resource
    private UserNewsService userNewsService;

    @GetMapping("fileModal.html")
    public String taskFileModal(String windowName,Model model){
        model.addAttribute("windowName",windowName);
        return "task-file-modal";
    }


    /**
     * 在日历上创建任务
     * @param model
     * @return
     */
    @GetMapping("createCalendarTasktk.html")
    public String createCalendarTasktk(Model model,String rq){
        try {
            Date date = DateUtils.parseDate(rq);
            model.addAttribute("date",date.getTime());
            model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
            //查询出该用户所参与的项目
            model.addAttribute("projectList",projectService.findProjectByMemberId(ShiroAuthenticationManager.getUserId()));
        } catch (Exception e){
            log.error("系统异常,{}",e);
            throw new SystemException(e);
        }
        return "tk-calendar-create-task";
    }

    /**
     * 在日历上创建任务时获取项目的人员信息
     * @param projectId 项目id
     * @return
     */
    @GetMapping("addPeople.html")
    public String addPeople(String projectId,String executorId,String type,Model model){
        try {
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            model.addAttribute("user",userEntity);
            model.addAttribute("project",projectService.findProjectByProjectId(projectId));
            ProjectMember projectMember = new ProjectMember();
            projectMember.setProjectId(projectId);
            List<ProjectMember> memberAllList = projectMemberService.findProjectMemberAllList(projectMember);
           // memberAllList = memberAllList.stream().filter(projectMember1->!userEntity.getId().equals(projectMember1.getMemberId())).collect(Collectors.toList());
            model.addAttribute("members",memberAllList);

        } catch (Exception e){
            log.error("系统异常,数据拉取失败!");
            throw new SystemException(e);
        }
        if(type.equals("1")){
            return "tk-search-executor";
        } else{
            return "tk-search-people";
        }
    }

    /**
     * 执行者弹窗--汪亚锋
     * @param projectId 项目id
     * @param taskId 任务id
     * @param executorId 执行者id
     * @return
     */
    @GetMapping("executor.html")
    public String showExecutor(@RequestParam String projectId,@RequestParam String taskId,@RequestParam String executorId, Model model){


        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(projectId);
        List<ProjectMember> memberAllList = projectMemberService.findProjectMemberAllList(projectMember);

        if(StringUtils.isNotEmpty(executorId)){
            memberAllList = memberAllList.stream().filter(member->!executorId.equals(member.getMemberId())).collect(Collectors.toList());
            UserEntity userEntity = userService.findUserInfoById(executorId);
            model.addAttribute("user",userEntity);
        }
        model.addAttribute("members",memberAllList);
        model.addAttribute("taskId",taskId);
        return "tk-search-executor";
    }


    @GetMapping("invite.html")
    public String showInvite(@RequestParam String projectId,@RequestParam String taskId,Model model){

        List<UserEntity> members = userService.findProjectAllMember(projectId);
        Task task = taskService.findTaskByTaskId(taskId);
        members.removeAll(task.getJoinInfo());
        model.addAttribute("members",members);
        model.addAttribute("users",task.getJoinInfo());
        model.addAttribute("taskId",taskId);
        return "tk-search-people";
    }


    /**
     * 添加新任务
     * @param task 任务实体信息
     * @return
     */
    @PostMapping("saveTask")
    public JSONObject saveTask(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            //保存任务信息到数据库
            taskService.saveTask(task);
            task = taskService.findTaskByTaskId(task.getTaskId());
            jsonObject.put("result",1);
            jsonObject.put("msg","保存成功");
            JSONObject object = new JSONObject();
            object.put("task",task);
            object.put("type","创建了任务");
            messagingTemplate.convertAndSend("/topic/"+task.getProjectId(), new ServerMessage(JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect)));
        } catch (Exception e){
            log.error("当前任务保存失败! ,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 添加/移除参与者
     */
    @PostMapping("/addAndRemoveTaskMember")
    @ResponseBody
    public JSONObject addAndRemoveTaskMember(String taskId,String memberIds){
        JSONObject jsonObject = new JSONObject();
        try {
            Log log = taskService.addAndRemoveTaskMember(taskId,memberIds);
            Task task = taskService.findTaskByTaskId(taskId);
            jsonObject.put("members",task.getJoinInfo());
            jsonObject.put("msg","更新成功!");
            jsonObject.put("result",1);
            jsonObject.put("type",TaskLogFunction.A19.getName());
            if(log!=null){
                jsonObject.put("taskLog",log);
                TaskPushType taskPushType = new TaskPushType(TaskLogFunction.A19.getName());
                taskPushType.setObject(jsonObject);

                //新的消息推送方式
                Push.pushMessage(taskId,jsonObject);
            }
        } catch (Exception e){
            log.error("系统异常,成员添加失败! 当前任务id: ,{},{}",taskId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 任务移动
     * @param task 包含该任务的id
     * @param newTaskMenuVO 要移动到的 项目id,名称 分组id,名称 菜单id,名称
     * @param oldTaskMenuVO 移动之前的 项目id,名称 分组id,名称 菜单id,名称
     * @return
     */
    @PostMapping("mobileTask")
    @ResponseBody
    public JSONObject mobileTask(Task task, @RequestParam TaskMenuVO oldTaskMenuVO, @RequestParam TaskMenuVO newTaskMenuVO){
        JSONObject jsonObject = new JSONObject();
        try {
            //修改该任务的任务组编号
            Log taskLogVO = taskService.mobileTask(task,oldTaskMenuVO,newTaskMenuVO);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("result", 1);
                jsonObject.put("msg","任务移动成功！");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("result", 0);
                jsonObject.put("msg","任务移动失败！");
            }
        } catch (Exception e){
            log.error("当前任务移动失败!{}",e);
            jsonObject.put("result", 0);
            jsonObject.put("msg","系统异常,移动失败！");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     *  将任务 移入回收站
     * @param taskId 任务的id
     * @return
     */
    @PostMapping("moveToRecycleBin")
    @ResponseBody
    public JSONObject moveToRecycleBin(@RequestParam String taskId, @RequestParam String projectId) {
        JSONObject jsonObject = new JSONObject();
        JSONObject pushData = new JSONObject();
        try {
            //将任务移入回收站
            Log taskLogVO = taskService.moveToRecycleBin(taskId);
            if (taskLogVO.getResult() > 0) {
                jsonObject.put("result",1);
                jsonObject.put("taskId",taskId);

                //推送的数据
                pushData.put("type",TaskLogFunction.P.getName());
                pushData.put("taskId",taskId);
                messagingTemplate.convertAndSend("/topic/"+taskId,new ServerMessage(JSON.toJSONString(pushData)));
                messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(pushData)));
            } else {
                jsonObject.put("msg", "操作失败！");
                jsonObject.put("result","0");
            }
        } catch (Exception e) {
            log.error("操作失败!", e);
            jsonObject.put("msg", "系统异常,操作失败！");
            jsonObject.put("result","0");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 任务恢复
     * @param taskId 任务id
     * @param menuId 要恢复到的菜单的id
     * @return
     */
    @PostMapping("recoveryTask")
    @ResponseBody
    public JSONObject recoveryTask(String taskId, String menuId, String projectId){
        JSONObject jsonObject = new JSONObject();
        JSONObject pushData = new JSONObject();
        try {
            taskService.recoveryTask(taskId,menuId);
            jsonObject.put("msg","恢复任务成功!");
            jsonObject.put("result",1);

            //封装推送数据
            pushData.put("task",taskService.findTaskByTaskId(taskId));
            pushData.put("type","恢复了任务");
            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(pushData)));
            pushData.put("type","恢复了信息");
            pushData.remove("task");
            pushData.put("id",taskId);
            //推送到项目的回收站的频道
            messagingTemplate.convertAndSend("/topic/"+projectId+"recycleBin",new ServerMessage(JSON.toJSONString(pushData)));
        } catch (Exception e){
            log.error("系统异常,任务恢复失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更改任务的备注信息
     * @param task 任务的信息
     * @return
     */
    @PostMapping("upateTaskRemarks")
    @ResponseBody
    public JSONObject upateTaskRemarks(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            Log taskLogVO = taskService.updateTask(task);
            jsonObject.put("msg","更新成功!");
            jsonObject.put("result","1");
            jsonObject.put("type","更新备注");
            jsonObject.put("task",task);
            jsonObject.put("taskLog",taskLogVO);
            messagingTemplate.convertAndSend("/topic/"+task.getTaskId(), new ServerMessage(jsonObject.toString()));
        } catch (Exception e){
            log.error("系统异常,更新任务备注失败! 当前任务:{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更改任务的优先级
     * @param task 任务实体信息
     * @return
     */
    @PostMapping("updateTaskPriority")
    @ResponseBody
    public JSONObject updateTaskPriority(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            Log taskLogVO = taskService.updateTask(task);
            jsonObject.put("msg","设置成功!");
            jsonObject.put("result",1);
            jsonObject.put("taskLog",taskLogVO);
            jsonObject.put("priority",task.getPriority());
            jsonObject.put("taskId",task.getTaskId());
            //推送至主页面
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.F.getName());
            taskPushType.setObject(jsonObject);
            messagingTemplate.convertAndSend("/topic/"+task.getTaskId(),new ServerMessage(JSON.toJSONString(taskPushType)));
            messagingTemplate.convertAndSend("/topic/"+task.getProjectId(),new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("系统异常,更新任务优先级失败! 当前任务,{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 永久的删除任务
     * @param taskId 任务id
     * @return
     */
    @PostMapping("delTask")
    @ResponseBody
    public JSONObject delTask(@RequestParam String taskId, @RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        JSONObject pushData = new JSONObject();
        try {
            //永久删除任务
            taskService.deleteTask(taskId);
            jsonObject.put("msg","以将该任务清除!");
            jsonObject.put("result",1);

            //封装推送数据
            pushData.put("id",taskId);
            pushData.put("type","删除回收站信息");

            //推送到项目的回收站的频道
            messagingTemplate.convertAndSend("/topic/"+projectId+"recycleBin",new ServerMessage(JSON.toJSONString(pushData)));

        } catch (Exception e){
            log.error("系统异常,移除失败! 任务: ,{},{}",taskId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 完成任务 和重做任务
     * @param task 当前任务信息
     */
    @PostMapping("resetAndCompleteTask")
    @ResponseBody
    public JSONObject resetAndCompleteTask(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            //根据任务id 查询出该任务的实体信息
            Task taskInfo = taskService.findTaskByTaskId(task.getTaskId());
            //改变任务状态
            Log taskLogVO = taskService.resetAndCompleteTask(taskInfo);
            jsonObject.put("msg","修改成功!");
            jsonObject.put("result",1);
            jsonObject.put("taskLog",taskLogVO);
            jsonObject.put("taskId",task.getTaskId());
            jsonObject.put("type",TaskLogFunction.S.getName());
            jsonObject.put("status",taskInfo.getTaskStatus());
            messagingTemplate.convertAndSend("/topic/"+taskInfo.getProjectId(),new ServerMessage(JSON.toJSONString(jsonObject)));
        } catch (ServiceException e){
            jsonObject.put("result",0);
            jsonObject.put("msg",e.getMessage());
            return jsonObject;
        } catch (Exception e){
            log.error("任务状态修改失败! 任务id: ,{}, \t 修改前状态:{},{}",task.getTaskId(),task.getTaskStatus(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新任务的重复规则
     * @param task 任务的实体信息
     * @param object 时间重复周期的具体信息 (未设定)
     * @return
     */
    @PostMapping("updateTaskRepeat")
    @ResponseBody
    public JSONObject updateTaskRepeat(Task task,Object object){
        JSONObject jsonObject = new JSONObject();
        try {
            Log taskLogVO = taskService.updateTaskRepeat(task,object);
            jsonObject.put("msg","重复规则设置成功");
            jsonObject.put("result",1);
            jsonObject.put("taskLog",taskLogVO);
            jsonObject.put("task",task);
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.A21.getName());
            taskPushType.setObject(jsonObject);
            messagingTemplate.convertAndSend("/topic/"+task.getTaskId(),new ServerMessage(JSON.toJSONString(taskPushType)));
            messagingTemplate.convertAndSend("/topic/"+task.getProjectId(),new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("系统异常,重复规则更新失败! 当前任务id:{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新任务( 开始 / 结束 ) 时间
     * @param task 包含时间的实体信息
     */
    @PostMapping("updateTaskStartAndEndTime")
    @ResponseBody
    public JSONObject updateTaskStartAndEndTime(Task task){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        try {
            //更新任务时间信息
            Log taskLogVO = taskService.updateTaskStartAndEndTime(task);
            if(taskLogVO.getResult() > 0){
                if(task.getStartTime() != null){
                    jsonObject.put("msg","开始时间更新成功!");
                } else{
                    jsonObject.put("msg","结束时间更新成功!");
                }
                jsonObject.put("result",1);
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","时间更新失败!");
                jsonObject.put("result",0);
            }
            jsonObject.put("task",task);
            if(task.getStartTime() != null){
                jsonObject.put("startTime",format.format(new Date(task.getStartTime())));
            } else{
                jsonObject.put("endTime",format.format(new Date(task.getEndTime())));
            }
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.A25.getName());
            taskPushType.setObject(jsonObject);
            messagingTemplate.convertAndSend("/topic/" + task.getTaskId(),new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("任务时间信息更新失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 清除任务的开始时间
     * @param task 任务的实体信息
     * @return
     */
    @PostMapping("removeTaskStartTime")
    @ResponseBody
    public JSONObject removeTaskStartTime(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            Log taskLogVO = taskService.removeTaskStartTime(task);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","清空成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","清空失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常,清除失败! 当前任务id:,{},{} ",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 清除任务的截止时间
     * @param task 任务的实体信息
     * @return
     */
    @PostMapping("removeTaskEndTime")
    @ResponseBody
    public JSONObject removeTaskEndTime(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            Log taskLogVO = taskService.removeTaskEndTime(task);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","清空成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","清空失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常,清除失败! 当前任务id:,{},{} ",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新任务的提醒时间
     * @param task 任务实体信息
     * @param userEntity 用户实体信息
     * @return
     */
    @PostMapping("updateTaskRemindTime")
    @ResponseBody
    public JSONObject updateTaskRemindTime(Task task,UserEntity userEntity){
        JSONObject jsonObject = new JSONObject();
        try {
            Log taskLogVO = taskService.updateTaskRemindTime(task,userEntity);
            jsonObject.put("msg","设置成功！");
            jsonObject.put("result",1);
            jsonObject.put("taskLog",taskLogVO);
            jsonObject.put("task",task);
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.A13.getName());
            taskPushType.setObject(jsonObject);

            messagingTemplate.convertAndSend("/topic/"+task.getTaskId(),new ServerMessage(JSON.toJSONString(taskPushType)));
            messagingTemplate.convertAndSend("/topic/"+task.getProjectId(),new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("系统异常,设置提醒时间失败! 任务id: ,{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }
    /**
     * 根据菜单id 查询该菜单下有没有任务
     * @param taskMenuId 分组id
     * @return
     */
    @PostMapping("findTaskByMenuId")
    @ResponseBody
    public JSONObject findTaskByMenuId(@RequestParam String taskMenuId){
        JSONObject jsonObject = new JSONObject();
        try{
            int result = taskService.findTaskByMenuId(taskMenuId);
            jsonObject.put("result",result);
        } catch (Exception e){
            log.error("查询失败! 当前菜单id:,{},{}",taskMenuId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 从任务详情界面添加标签
     * @param tag 标签名称
     * @param taskId 当前任务id
     * @param projectId 当前任务的id
     * @return
     */
    @PostMapping("addTagsToTask")
    @ResponseBody
    public JSONObject addTagsToTask(Tag tag,String taskId,String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            //根据标签名称查询 当前存不存在数据库 如果存在直接绑定到当前任务,如果不存在则先插入标签 在绑定到当前任务
            int countByTagName = tagService.findCountByTagName(projectId, tag.getTagName());
            if(countByTagName == 0){
                tag.setTagId(tagService.saveTag(tag).getTagId());
            } else{
                jsonObject.put("result",0);
                jsonObject.put("msg","标签已存在!");
                return jsonObject;
            }
            //更新当前任务的标签信息
            Log taskLogVO = taskService.addTaskTags(tag, taskId,countByTagName);
            jsonObject.put("result",taskLogVO.getResult());
            jsonObject.put("msg","标签添加成功!");
            jsonObject.put("taskId",taskId);
            jsonObject.put("tag",tagService.findById(tag.getTagId().intValue()));
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.A20.getName());
            taskPushType.setObject(jsonObject);
            messagingTemplate.convertAndSend("/topic/"+taskId,new ServerMessage(JSON.toJSONString(taskPushType)));
            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("保存失败,标签名称为:,{},{}",tag.getTagName(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 在任务详情界面点击标签时附加
     * @param tag 标签名称
     * @param taskId 当前任务id
     * @param projectId 当前任务的id
     * @return
     */
    @PostMapping("addTaskTag")
    @ResponseBody
    public JSONObject addTags(Tag tag,String taskId,String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            //更新当前任务的标签信息
            Log taskLogVO = taskService.addTaskTags(tag, taskId,1);
            jsonObject.put("result",taskLogVO.getResult());
            jsonObject.put("msg","标签添加成功!");
            jsonObject.put("tagId",tag.getTagId());
            jsonObject.put("tag",tagService.findById(tag.getTagId().intValue()));
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.A20.getName());
            taskPushType.setObject(jsonObject);
            messagingTemplate.convertAndSend("/topic/"+taskId,new ServerMessage(JSON.toJSONString(taskPushType)));
            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("保存失败,标签名称为:,{},{}",tag.getTagName(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取项目下的所有标签
     * @param projectId 项目id
     * @return
     */
    @PostMapping("findAllTags")
    @ResponseBody
    public JSONObject findAllTags(@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Tag> byProjectId = tagService.findByProjectId(projectId);
            jsonObject.put("data",byProjectId);
            jsonObject.put("result",1);

        } catch (Exception e){
            log.error("系统异常,标签获取失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 转子任务为顶级任务
     * @param task 包含当前任务的 id、name
     * @return
     */
    @PostMapping("turnToFatherLevel")
    @ResponseBody
    public JSONObject turnToFatherLevel(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            Log taskLogVO = taskService.turnToFatherLevel(task);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","转换成功");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","转换失败");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统错误,任务转换失败! 当前任务id: ,{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 给当前任务点赞
     * @param task 任务的实体信息
     * @return
     */
    @PostMapping("clickFabulous")
    @ResponseBody
    public JSONObject clickFabulous(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            int result = taskService.clickFabulous(task);
            if(result > 0){
                jsonObject.put("msg","成功!");
                jsonObject.put("result",result);
            } else{
                jsonObject.put("msg","失败!");
                jsonObject.put("result",result);
            }
        } catch (Exception e){
            log.error("系统异常! 点赞失败! 当前任务id: ,{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 对当前任务取消赞
     * @param task 当前任务信息
     * @return
     */
    @PostMapping("cancelFabulous")
    @ResponseBody
    public JSONObject cancelFabulous(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            int result = taskService.cancelFabulous(task);
            if(result > 0){
                jsonObject.put("msg","赞已取消!");
                jsonObject.put("result",1);
            } else{
                jsonObject.put("msg","取消失败!");
                jsonObject.put("result",0);
            }
        } catch (Exception e){
            log.error("系统异常,取消赞失败! 当前任务id:,{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 添加子任务
     * @param parentTaskId 父任务id
     * @param subLevel 子任务信息
     * @param projectId 当前项目id
     * @return
     */
    @PostMapping("addSubLevelTask")
    @ResponseBody
    public JSONObject addSubLevelTask(String parentTaskId,Task subLevel,String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            //保存子任务信息至数据库
            subLevel.setTaskId(IdGen.uuid());
            subLevel.setProjectId(projectId);
            Log taskLogVO = taskService.addSubLevelTasks(parentTaskId,subLevel);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","添加成功!");
                jsonObject.put("result",1);
                jsonObject.put("taskLog",taskLogVO);
                jsonObject.put("subTaskId",subLevel.getTaskId());
            } else{
                jsonObject.put("msg","添加失败!");
                jsonObject.put("result",0);
            }
        } catch (Exception e){
            log.error("系统异常! 添加子级任务失败! 当前任务id: ,{},{}",parentTaskId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 重做和完成子任务
     * @param task
     */
    @PostMapping("resetAndCompleteSubLevelTask")
    @ResponseBody
    public JSONObject resetAndCompleteSubLevelTask(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            Log taskLogVO = taskService.resetAndCompleteSubLevelTask(task);
            jsonObject.put("msg","状态更新成功!");
            jsonObject.put("result",1);
            jsonObject.put("taskLog",taskLogVO);
        } catch (ServiceException e){
            jsonObject.put("result",0);
            jsonObject.put("msg","父任务已经完成,不能操作子任务!");
            return jsonObject;
        } catch (Exception e){
            log.error("系统异常! 状态更新失败 当前任务id: {},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 复制任务 及 子任务
     * @param taskId 任务的id
     * @param projectId 项目id
     * @param menuId 菜单id
     * @param old_new 原任务接受新任务的更新提醒  是否勾选
     * @param new_old 新任务接受原任务的更新提醒  是否勾选
     */
    @PostMapping("copyTask")
    @ResponseBody
    public JSONObject copyTask(@RequestParam String taskId,@RequestParam String projectId,@RequestParam String menuId, boolean old_new, boolean new_old){
        JSONObject jsonObject = new JSONObject();
        try {
            String copyTaskId = taskService.copyTask(taskId,projectId,menuId,old_new,new_old);
            jsonObject.put("taskId",copyTaskId);
            jsonObject.put("msg","复制成功!");
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,复制任务失败! ",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 收藏任务
     * @param task
     * @return
     */
    @PostMapping("collectTask")
    @ResponseBody
    public JSONObject collectTask(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.collectTask(task);
            jsonObject.put("msg","收藏成功!");
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,任务收藏失败! 当前任务id: ,{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 取消任务收藏
     * @param taskId
     * @return
     */
    @PostMapping("cancleCollectTask")
    @ResponseBody
    public JSONObject cancleCollectTask(String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.cancleCollectTask(taskId);
        } catch (Exception e){
            log.error("系统异常,任务收藏失败! 当前任务id: ,{},{}",taskId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 修改任务的隐私模式
     * @param task 任务的实体信息
     */
    @PostMapping("settingUpPrivacyPatterns")
    @ResponseBody
    public JSONObject settingUpPrivacyPatterns(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.settingUpPrivacyPatterns(task);
            jsonObject.put("msg","修改成功!");
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("更改隐私模式失败! 当前任务id,{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 查询出已经是该任务的参与者的信息和非参与者的信息
     * @return
     */
    @PostMapping("findTaskMemberInfo")
    @ResponseBody
    public JSONObject findTaskMemberInfo(String taskId,String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            Task task = taskService.findTaskByTaskId(taskId);
            List<UserEntity> projectMembers = userService.findProjectAllMember(projectId);

            projectMembers = projectMembers.stream().filter(item -> !task.getJoinInfo().contains(item)).collect(Collectors.toList());

            jsonObject.put("projectMembers",projectMembers);
            jsonObject.put("joinInfo",task.getJoinInfo());
            jsonObject.put("result",1);

        } catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 初始化打开任务的界面
     * @param task 任务的信息
     * @return
     */
    @GetMapping("initTask.html")
    public String initTask(Task task,Model model){
        try {
            //获取当前用户信息
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            model.addAttribute("user",userEntity);
            //查询出此条任务的具体信息
            Task taskById = taskService.findTaskByTaskId(task.getTaskId());
            model.addAttribute("task",taskById);
            model.addAttribute("taskId",task.getTaskId());
            //当前项目Id
            model.addAttribute("projectId",taskById.getProjectId());
            //判断当前用户有没有对该任务点赞
            boolean isFabulous = taskService.judgeFabulous(task.getTaskId());
            model.addAttribute("isFabulous",isFabulous);

            //查询出当前的项目信息详情
            model.addAttribute("projectInfo",projectService.findProjectByProjectId(task.getProjectId()));

            //判断当前用户有没有收藏该任务
            boolean isCollect = taskService.judgeCollectTask(task);
            model.addAttribute("isCollect",isCollect);

            //查询出该任务所在的菜单信息
            Relation menuRelation = relationService.findMenuInfoByTaskId(task.getTaskId());
            model.addAttribute("menuRelation",menuRelation);

            //根据该任务的菜单查询出任务的分组信息
            Relation taskGroup = taskService.findTaskGroupInfoByTaskMenuId(menuRelation.getParentId());
            model.addAttribute("taskGroup",taskGroup);

            //查询出任务的关联信息
            BindingVo bindingVo = bindingService.listBindingInfoByPublicId(task.getTaskId());
            model.addAttribute("bindingVo",bindingVo);

            if(taskById.getParentId() == null){
                //根据菜单信息查询出该任务的所在的分组 和 项目信息
                TaskMenuVO taskMenuVO = relationService.findProjectAndGroupInfoByMenuId(menuRelation.getRelationId());
                model.addAttribute("menuRelation",menuRelation);
                model.addAttribute("taskMenuVo",taskMenuVO);
            }

            //查询出我参与的所有项目信息
            List<Project> projectByMemberId = projectService.findProjectByMemberId(ShiroAuthenticationManager.getUserId());
            model.addAttribute("projectByMemberId",projectByMemberId);

            //查询出该任务的日志信息
            List<Log> logList = logService.initLog(task.getTaskId());
            Collections.reverse(logList);
            model.addAttribute("taskLogs",logList);
            //返回数据
            model.addAttribute("data",taskById);
            model.addAttribute("isFabulous",isFabulous);
            model.addAttribute("isCollect",isCollect);
        } catch (Exception e){
            log.error("系统异常,获取数据失败! 当前任务id ,{},{}",task.getTaskId(),e);
            throw new SystemException(e);
        }
        return "revisetask";
    }

    /**
     * 根据项目id 查询该项目下( 当前任务的执行者除外 )的其他所有成员信息
     * @param projectId 项目id
     * @return
     */
    @PostMapping("findProjectAllMember")
    @ResponseBody
    public JSONObject findProjectAllMember(@RequestParam String projectId,@RequestParam(required = false) String executorId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<UserEntity> list = userService.findProjectAllMember(projectId);

            if(StringUtils.isEmpty(executorId)){
                jsonObject.put("data",list);

            }else{
                UserEntity userEntity = userService.findById(executorId);
                list = list.stream().filter(user->!user.getId().equals(userEntity.getId())).collect(Collectors.toList());
                jsonObject.put("user",userEntity);
                jsonObject.put("data",list);
            }
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,数据获取失败! 当前项目id: ,{},{}",projectId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新任务的其他
     * @param task
     * @return
     */
    @PostMapping("updateOther")
    @ResponseBody
    public JSONObject updateOther(@RequestParam Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            Log taskLogVO = taskService.updateTask(task);
            jsonObject.put("msg","更新成功!");
            jsonObject.put("result","1");
            jsonObject.put("taskLog",taskLogVO);
        } catch (ServiceException e){
            jsonObject.put("result",0);
        } catch (Exception e){
            log.error("系统异常,更新任务其他失败! 当前任务id: ,{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * @param id 任务的分组id  也可能是要按照智能分组的方式查询
     * @param projectId 当前选中项目的id
     * 在添加任务关联的时候查询任务
     * 1. 可以按照任务的分组查询任务
     * 2. 可以根据 智能分组的方式查询任务  智能分组包括(今天的任务,完成的任务,未完成的任务)
     * @return 结果集
     */
    @PostMapping("findRelationTask")
    @ResponseBody
    public JSONObject findRelationTask(String id,String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            //如果id为 智能分组的选项 则按照智能分组的方式查询任务
            if(TaskStatusConstant.COMPLETE_TASK.equals(id) || TaskStatusConstant.CURRENT_DAY_TASK.equals(id) || TaskStatusConstant.HANG_IN_THE_AIR_TASK.equals(id)){
                List<Task> task = taskService.intelligenceGroup(id, projectId);
                jsonObject.put("data",task);
                jsonObject.put("result",1);
                return jsonObject;
            } else{
                //查询出该分组下的所有任务信息
                List<Relation> relations = relationService.findGroupAllTask(id);
                jsonObject.put("data",relations);
                jsonObject.put("result",1);
            }
        } catch (Exception e){
            log.error("系统异常,数据拉取失败,{}",e);
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,数据获取失败!");
        }
        return jsonObject;
    }

    /**
     * 查询当前菜单下的所有任务信息
     * @param menuId
     * @return
     */
    @PostMapping("taskMenu")
    @ResponseBody
    public JSONObject taskMenu(@RequestParam String menuId){
        JSONObject jsonObject = new JSONObject();
        try{
            if(StringUtils.isEmpty(menuId)){
                jsonObject.put("msg","请选择菜单!");
                return jsonObject;
            }
            List<Task> taskList = taskService.taskMenu(menuId);
            if(taskList.size() > 0){
                jsonObject.put("data",taskList);
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","没有数据!");
            }
        } catch (Exception e){
            log.error("系统异常,数据拉取失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 查询某个用户执行的所有任务信息
     * @param uId 用户id
     * @return
     */
    @PostMapping("findTaskByExecutor")
    @ResponseBody
    public JSONObject findTaskByExecutor(@RequestParam String uId,@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(StringUtils.isEmpty(uId)){
                jsonObject.put("msg","请选择一个用户!");
                return jsonObject;
            }
            //获取数据信息
            List<Task> taskList = taskService.findTaskByExecutor(uId,null);
            if(taskList.size() > 0){
                jsonObject.put("data",taskList);
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","没有数据");
            }
        } catch (Exception e){
            log.error("系统异常,获取数据失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 查询该项目下待认领的任务
     * @param projectId 项目id
     * @return
     */
    @PostMapping("waitClaimTask")
    @ResponseBody
    public JSONObject waitClaimTask(@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Task> taskList = taskService.waitClaimTask(projectId);
            if(taskList.size() > 0){
                jsonObject.put("data",taskList);
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","没有数据!");
            }
        } catch (Exception e){
            log.error("系统异常,获取数据失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 移除当前任务的执行者  把当前任务的改为 待认领 状态
     * @param taskId 任务id
     * @return
     */
    @PostMapping("removeExecutor")
    @ResponseBody
    public JSONObject removeExecutor(String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            Log log = taskService.removeExecutor(taskId);
            jsonObject.put("taskLog",log);
            jsonObject.put("taskId",taskId);
            jsonObject.put("msg","移除成功!");
            jsonObject.put("result",1);
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.A.getName());
            taskPushType.setObject(jsonObject);
            messagingTemplate.convertAndSend("/topic/"+taskService.findTaskByTaskId(taskId).getProjectId(),new ServerMessage(JSON.toJSONString(taskPushType)));
            messagingTemplate.convertAndSend("/topic/"+taskId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("系统异常,操作失败,{}",e);
        }
        return jsonObject;
    }

    /**
     * 更新任务的执行者
     * @param taskId 任务id
     * @param uName 新的执行者的名字
     * @param executor 新的执行者的id
     * @return0
     */
    @PostMapping("updateTaskExecutor")
    @ResponseBody
    public JSONObject updateTaskExecutor(String taskId,String executor,String uName){
        JSONObject jsonObject = new JSONObject();
        try {
            Log log = taskService.updateTaskExecutor(taskId, executor, uName);
            jsonObject.put("msg","修改成功");
            jsonObject.put("result",1);
            jsonObject.put("taskLog",log);
            jsonObject.put("executorInfo",userService.findById(executor));
            jsonObject.put("taskId",taskId);
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.U.getName());
            taskPushType.setObject(jsonObject);
            messagingTemplate.convertAndSend("/topic/"+taskService.findTaskByTaskId(taskId).getProjectId(),new ServerMessage(JSON.toJSONString(taskPushType)));
            messagingTemplate.convertAndSend("/topic/"+taskId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("系统异常,修改失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 反向选取用户信息
     * @param projectId 项目id
     * @return
     */
    @PostMapping("reverseFindUser")
    @ResponseBody
    public JSONObject reverseFindUser(String projectId,String[] uId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<UserEntity> list = userService.reverseFindUser(projectId,uId);
            jsonObject.put("reversUser",list);
        } catch (Exception e){
            log.error("系统异常,操作失败!{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     *
     * @param taskId 当前移动的任务的id
     * @param menuId 任务移动后的菜单Id
     * @param taskIds 任务移动之后的菜单组下面的全部任务id
     * @return
     */
    @PostMapping("taskOrder")
    @ResponseBody
    public JSONObject taskOrder(@RequestParam(required = false) String taskId,
                                @RequestParam(required = false) String menuId,
                                @RequestParam String[] taskIds){
        JSONObject jsonObject = new JSONObject();
        try {
            //先更新任务菜单id
            if(StringUtils.isNotEmpty(taskId)&&StringUtils.isNotEmpty(menuId)){
                Task task = new Task();
                task.setUpdateTime(System.currentTimeMillis());
                task.setTaskId(taskId);
                task.setTaskMenuId(menuId);
                taskService.updateTask(task);
            }

            //排序菜单中的任务
            for(int i=taskIds.length-1;i>=0;i--){
                Task task1 = new Task();
                task1.setUpdateTime(System.currentTimeMillis());
                task1.setTaskId(taskIds[i]);
                task1.setOrder(i);
                taskService.updateTask(task1);
            }

            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("任务排序失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 查询出该任务的所有子任务信息
     * @param taskId 任务的id
     * @return 任务实体信息集合
     */
    @PostMapping("findTaskByFatherTask")
    @ResponseBody
    public JSONObject findTaskByFatherTask(String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Task> taskByFatherTask = taskService.findTaskByFatherTask(taskId);
            jsonObject.put("data",taskByFatherTask);
            jsonObject.put("result",1);
        } catch (Exception e){
            jsonObject.put("msg","数据获取失败!");
            jsonObject.put("result",0);
        }
        return jsonObject;
    }

    /**
     * 更新任务的名称
     * @param taskId 任务的id
     * @param projectId 项目id
     * @param taskName 任务的名称
     * @return
     */
    @PostMapping("updateTaskName")
    @ResponseBody
    public JSONObject updateTaskName(String taskId,String projectId,String taskName){
        JSONObject jsonObject = new JSONObject();
        try {
            //设置任务实体信息
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskName(taskName);
            task.setUpdateTime(System.currentTimeMillis());
            //更新任务
            Log taskLogVO = taskService.updateTask(task);
            //推送数据
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.A18.getName());
            Map<String,Object> map = new HashMap<>();
            map.put("taskLog",taskLogVO);
            map.put("taskId",taskId);
            map.put("taskName",taskName);
            taskPushType.setObject(map);
            //推送至任务的详情界面
            messagingTemplate.convertAndSend("/topic/"+taskId,new ServerMessage(JSON.toJSONString(taskPushType)));
            //推送至主页面
            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(taskPushType)));
            jsonObject.put("result",1);
        } catch (Exception e){
            jsonObject.put("result",0);
            log.error("系统异常,更新失败,{}",e);
        }
        return jsonObject;
    }

    //上传文件
    @PostMapping("/upload")
    @ResponseBody
    public JSONObject uploadFile(
            @RequestParam String taskId,@RequestParam String projectId,
            @RequestParam(required = false,defaultValue = "") String content,
            @RequestParam(value = "files",required = false) String files,
            @RequestParam(required = false) String[] fileId){
        JSONObject jsonObject = new JSONObject();
        try {
            //文件和内容都为空则不发送推送消息
            if(files==null&&fileId==null&&StringUtils.isEmpty(content)){
                return jsonObject;
            }
            List<String> fileIds = new ArrayList<>();
            if(StringUtils.isNotEmpty(files)){
                JSONArray array = JSON.parseArray(files);
                for (int i=0;i<array.size();i++) {
                    JSONObject object = array.getJSONObject(i);
                    String fileName = object.getString("fileName");
                    String fileUrl = object.getString("fileUrl");
                    String size = object.getString("size");
                    String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                    // 写库
                    File myFile = new File();
                    // 用原本的文件名
                    myFile.setFileName(fileName);
                    myFile.setExt(ext);
                    myFile.setProjectId(projectId);
                    myFile.setFileUrl(fileUrl);
                    // 得到上传文件的大小
                    myFile.setSize(size);
                    myFile.setCatalog(0);
                    myFile.setParentId("0");
                    myFile.setFileLabel(1);
                    myFile.setFileUids(ShiroAuthenticationManager.getUserId());
                    fileService.saveFile(myFile);
                    fileIds.add(myFile.getFileId());
                }
            }


            if(fileId!=null&&fileId.length>0){
                fileIds.addAll(Arrays.asList(fileId));
            }

            //保存聊天信息
            Log log = new Log();
            log.setId(IdGen.uuid());
            if(StringUtils.isEmpty(content)){
                log.setContent("");
            }else{
                log.setContent(content);
                //查询出该任务的所有成员id
                Task taskByTaskId = taskService.findTaskByTaskId(taskId);
                String[] users = taskByTaskId.getTaskUIds().split(",");
                //保存消息信息
                userNewsService.saveUserNews(users,taskId,BindingConstants.BINDING_TASK_NAME,content,1);
            }

            log.setLogType(1);
            log.setMemberId(ShiroAuthenticationManager.getUserId());
            log.setPublicId(taskId);
            log.setLogFlag(1);
            log.setCreateTime(System.currentTimeMillis());
            log.setFileIds(StringUtils.join(fileIds,","));
            Log log1 = logService.saveLog(log);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "上传成功");
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.A14.getName());
            Map<String,Object> map = new HashMap<>();
            map.put("taskLog",log1);
            taskPushType.setObject(map);
            messagingTemplate.convertAndSend("/topic/"+taskId,new ServerMessage(JSON.toJSONString(taskPushType)));
        }catch (Exception e){
            throw new SystemException(e);
        }
        return jsonObject;
    }




    /**
     * 下载
     *
     * @param fileId
     */
    @RequestMapping(value = "/download",method = RequestMethod.GET)
    @ResponseBody
    public void downloadFile(@RequestParam String fileId,HttpServletResponse response) throws IOException{
        // 通过response对象获取OutputStream流
        OutputStream os = response.getOutputStream();
        try {
            File file = fileService.findFileById(fileId);
            InputStream inputStream  = AliyunOss.downloadInputStream(file.getFileUrl());

                //设置content-disposition响应头控制浏览器弹出保存框，若没有此句则浏览器会直接打开并显示文件。
                //中文名要经过URLEncoder.encode编码，否则虽然客户端能下载但显示的名字是乱码
                // 设置响应类型
                response.setContentType("application/x-msdownload");
                response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(file.getFileName(), "UTF-8"));
                int byteCount;
                //1M逐个读取
                byte[] bytes = new byte[1024*1024];
                while ((byteCount = inputStream.read(bytes)) != -1){
                    os.write(bytes, 0, byteCount);
                }
                inputStream.close();
                os.close();
        }catch (Exception e) {
                e.printStackTrace();
        }
    }
}
