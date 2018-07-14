package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.*;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskLogService;
import com.art1001.supply.service.task.TaskMemberService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.IdGen;
import io.netty.handler.codec.json.JsonObjectDecoder;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.transaction.xa.EhcacheXAException;
import net.sf.jsqlparser.expression.operators.relational.OldOracleJoinBinaryExpression;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.hibernate.validator.constraints.pl.REGON;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ObjectHelper;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.xml.ws.RequestWrapper;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.SimpleFormatter;

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

    /** 任务关系表逻辑层接口 */
    @Resource
    private TaskMemberService taskMemberService;

    /** 用户逻辑层接口 */
    @Resource
    private UserService userService;

    /** 任务日志逻辑层接口  */
    @Resource
    private TaskLogService taskLogService;

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
            List<UserEntity> userList = userService.findProjectAllMember(projectId);
            for (int i = 0;i < userList.size();i++){
                //如果没有执行者跳出循环
                if(StringUtils.isEmpty(executorId)){
                    break;
                }
                //查询完所有的成员信息后 过滤掉当前的执行者信息
                if(userList.get(i).getId().equals(executorId)){
                    userList.remove(i);
                }
            }
            model.addAttribute("data",userList);
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
     * 添加新任务
     * @param project 得到当前项目的实体信息
     * @param task 任务实体信息
     * @return
     */
    @PostMapping("saveTask")
    @ResponseBody
    public JSONObject saveTask(Project project,Task task,String[] members){
        JSONObject jsonObject = new JSONObject();
        try {
            //保存任务信息到数据库
            task.setTaskId(IdGen.uuid());
            TaskLogVO taskLogVO = taskService.saveTask(members,project,task);
            jsonObject.put("msg","添加任务成功!");
            jsonObject.put("result",1);
            Task taskByTaskId = taskService.findTaskByTaskId(task.getTaskId());
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.R.getName());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("task",taskByTaskId);
            taskPushType.setObject(map);
            jsonObject.put("task",taskByTaskId);
            jsonObject.put("taskLog",taskLogVO);
            messagingTemplate.convertAndSend("/topic/subscribe", new ServerMessage(JSONObject.toJSONString(taskPushType)));
        } catch (Exception e){
            jsonObject.put("msg","任务添加失败!");
            jsonObject.put("result","0");
            log.error("当前任务保存失败! ,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 添加任务成员
     * @param task
     * @param addUserEntity 要添加项目成员
     * @param removeUserEntity 要移除的项目成员
     */
    @PostMapping("addAndRemoveTaskMember")
    @ResponseBody
    public JSONObject addAndRemoveTaskMember(Task task,String[] addUserEntity,String[] removeUserEntity){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.addAndRemoveTaskMember(task,addUserEntity,removeUserEntity);
            jsonObject.put("msg","更新成功!");
            jsonObject.put("result",1);
            jsonObject.put("taskLog",taskLogVO);
        } catch (Exception e){
            log.error("系统异常,成员添加失败! 当前任务id: ,{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 移除任务-成员关系
     * @param task 当前项目实体信息
     * @param uId 被移除的用户的id
     * @return
     */
    @PostMapping("removeTaskMember")
    @ResponseBody
    public JSONObject removeTaskMember(Task task,String uId){
        JSONObject jsonObject = new JSONObject();
        try {
            UserEntity userEntity = userService.findUserById(uId);
            TaskLogVO taskLogVO = taskService.removeTaskMember(task,userEntity);
            jsonObject.put("msg","任务参与者移除成功!");
            jsonObject.put("taskLog",taskLogVO);
        } catch (Exception e){
            log.error("移除任务成员失败! 当前任务id: ,{},{}",task.getTaskId(),e);
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
    public JSONObject mobileTask(@RequestParam Task task, @RequestParam TaskMenuVO oldTaskMenuVO, @RequestParam TaskMenuVO newTaskMenuVO){
        JSONObject jsonObject = new JSONObject();
        try {
            //修改该任务的任务组编号
            TaskLogVO taskLogVO = taskService.mobileTask(task,oldTaskMenuVO,newTaskMenuVO);
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
    public JSONObject moveToRecycleBin(@RequestParam String taskId) {
        JSONObject jsonObject = new JSONObject();
        try {
            //将任务移入回收站
            TaskLogVO taskLogVO = taskService.moveToRecycleBin(taskId);
            if (taskLogVO.getResult() > 0) {
                jsonObject.put("msg", "操作成功！");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
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
     * @param projectId 要恢复到的任务的id
     * @return
     */
    @PostMapping("recoveryTask")
    @ResponseBody
    public JSONObject recoveryTask(String taskId,String menuId,String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.recoveryTask(taskId,menuId,projectId);
            jsonObject.put("msg","恢复任务成功!");
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,任务恢复失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 修改任务内容
     * @param task 任务的实体信息
     */
    @PostMapping("upateTaskContent")
    @ResponseBody
    public JSONObject upateTaskContent(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.updateTask(task);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","更新成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","更新失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常,更新任务内容失败! 当前任务: ,{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更改任务的备注信息
     * @param task
     * @return
     */
    @PostMapping("upateTaskRemarks")
    @ResponseBody
    public JSONObject upateTaskRemarks(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.updateTask(task);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","更新成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","更新失败!");
                jsonObject.put("result","0");
            }
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
            TaskLogVO taskLogVO = taskService.updateTask(task);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","设置成功!");
                jsonObject.put("result",1);
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","更新失败!");
                jsonObject.put("result","0");
            }
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
    public JSONObject delTask(@RequestParam String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            //永久删除任务
            int result = taskService.deleteTaskByTaskId(taskId);
            if(result >1){
                jsonObject.put("msg","以将该任务清除!");
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","移除任务失败!");
                jsonObject.put("result","0");
            }
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
            //改变任务状态
            TaskLogVO taskLogVO = taskService.resetAndCompleteTask(task);
            jsonObject.put("msg","修改成功!");
            jsonObject.put("result",1);
            jsonObject.put("taskLog",taskLogVO);
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.S.getName());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("Status",task.getTaskStatus());
            taskPushType.setObject(map);
            messagingTemplate.convertAndSend("/topic/"+task.getTaskId(),new ServerMessage(JSON.toJSONString(taskPushType)));
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
            TaskLogVO taskLogVO = taskService.updateTaskRepeat(task,object);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","重复规则设置成功");
                jsonObject.put("result",1);
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","重复规则设置失败!");
                jsonObject.put("result","0");
            }
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
        JSONObject jsonObject = new JSONObject();
        try {
            //更新任务时间信息
            TaskLogVO taskLogVO = taskService.updateTaskStartAndEndTime(task);
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
            TaskLogVO taskLogVO = taskService.removeTaskStartTime(task);
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
            TaskLogVO taskLogVO = taskService.removeTaskEndTime(task);
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
            TaskLogVO taskLogVO = taskService.updateTaskRemindTime(task,userEntity);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","设置成功！");
                jsonObject.put("result",1);
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","设置失败！");
                jsonObject.put("result","0");
            }
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
                tag.setMemberId(ShiroAuthenticationManager.getUserId());
                tag.setTagId(tagService.saveTag(tag).getTagId());
            } else{
                jsonObject.put("result",0);
                jsonObject.put("msg","标签已存在!");
                return jsonObject;
            }
            //更新当前任务的标签信息
            TaskLogVO taskLogVO = taskService.addTaskTags(tag, taskId,countByTagName);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("result",taskLogVO.getResult());
                jsonObject.put("msg","标签添加成功!");
                jsonObject.put("data",tag.getTagId());
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("data",tag.getTagId());
                jsonObject.put("msg","标签添加失败!");
                jsonObject.put("result",taskLogVO.getResult());
            }
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
            TaskLogVO taskLogVO = taskService.addTaskTags(tag, taskId,1);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("result",taskLogVO.getResult());
                jsonObject.put("msg","标签添加成功!");
                jsonObject.put("data",tag.getTagId());
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("data",tag.getTagId());
                jsonObject.put("msg","标签添加失败!");
                jsonObject.put("result",taskLogVO.getResult());
            }
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
     * 移除该任务上的标签
     * @param tags 当前任务上绑定的所有标签对象数组
     * @param tag 当前要被移除的标签对象
     * @param taskId 当前任务id
     * @return
     */
    @PostMapping("removeTaskTag")
    @ResponseBody
    public JSONObject removeTaskTag(String[] tags,Tag tag,String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            //更新该任务的标签信息
            int result = taskService.removeTaskTag(tags,tag,taskId);
            if(result > 0){
                jsonObject.put("msg","标签移除成功!");
                jsonObject.put("result",1);
            } else{
                jsonObject.put("msg","标签移除失败!");
                jsonObject.put("result",0);
            }
        } catch (Exception e){
            log.error("系统异常，标签移除失败！ 当前任务id： ,{},{}",taskId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * 给当前任务添加依赖关系
     * @param task 关联任务的实体信息
     * @param file 关联文件的实体信息
     * @param share 关联分享的实体信息
     * @param schedule 关联日程的实体信息
     * @param taskMember 关联关系信息
     * @param taskId 当前被操作的任务uid
     * @return
     */
    @PostMapping("addTaskRely")
    @ResponseBody
    public JSONObject addTaskRely(@RequestParam Task task,
                                  @RequestParam File file,
                                  @RequestParam Share share,
                                  @RequestParam Schedule schedule,
                                  @RequestParam TaskMember taskMember,
                                  @RequestParam String taskId
    ){
        JSONObject jsonObject = new JSONObject();
        try{
            TaskLogVO taskLogVO = taskMemberService.saveTaskMember(task,file,share,schedule,taskMember,taskId);
            if(taskLogVO.getResult() >0){
                jsonObject.put("msg","关联成功!");
                jsonObject.put("result",taskLogVO.getResult());
                jsonObject.put("taskLog",taskLogVO);
            }
        } catch (Exception e){
            log.error("系统异常,关联失败! 当前任务id ,{},{}",taskId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * 移除依赖关系
     * @param task 关联的任务
     * @param file 关联的文件
     * @param share 关联的分享
     * @param schedule 关联的日程
     * @param taskId 当前任务的id
     * @param taskRelyId  当前依赖的id
     * @return
     */
    @PostMapping("removeTaskRely")
    @ResponseBody
    public JSONObject removeTaskRely(@RequestParam Task task,
                                     @RequestParam File file,
                                     @RequestParam Share share,
                                     @RequestParam Schedule schedule,
                                     @RequestParam String taskId,
                                     @RequestParam String taskRelyId
    ){
        JSONObject jsonObject = new JSONObject();
        try {
            //删除关联关系
            TaskLogVO taskLogVO = taskMemberService.deleteTaskMemberById(task, file, share, schedule, taskId, taskRelyId);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","删除成功");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","删除失败");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常,关联关系删除失败!  任务关联id:,{},{}",taskRelyId,e);
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
            TaskLogVO taskLogVO = taskService.turnToFatherLevel(task);
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
            TaskLogVO taskLogVO = taskService.addSubLevelTasks(parentTaskId,subLevel);
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
            TaskLogVO taskLogVO = taskService.resetAndCompleteSubLevelTask(task);
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
     * @param task 任务的实体信息
     */
    @PostMapping("copyTask")
    @ResponseBody
    public JSONObject copyTask(@RequestParam Task task,@RequestParam String projectId,@RequestParam TaskMenuVO newTaskMenuVO){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.copyTask(task,projectId,newTaskMenuVO);
            jsonObject.put("msg","复制成功!");
            jsonObject.put("result","1");
            jsonObject.put("taskLog",taskLogVO);
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
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,任务收藏失败! 当前任务id: ,{},{}",task.getTaskId(),e);
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
     * @param task
     * @return
     */
    @PostMapping("findTaskMemberInfo")
    @ResponseBody
    public JSONObject findTaskMemberInfo(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            //查询出项目下不存在与该任务中的成员信息
            Map<String, List<UserEntity>> userByIsExistTask = userService.findUserByIsExistTask(task);
            if(userByIsExistTask.get("existList") != null && userByIsExistTask.get("existList").size() > 0){
                jsonObject.put("userExistTask",userByIsExistTask.get("existList"));
            } else{

            }
            if(userByIsExistTask.get("notExistList") != null && userByIsExistTask.get("notExistList").size() > 0){
                jsonObject.put("userNotExistTask",userByIsExistTask.get("notExistList"));
            } else{

            }
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
    public String initTask(Task task,String projectId,Model model){
        JSONObject jsonObject = new JSONObject();
        try {
            //获取当前用户信息
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            model.addAttribute("user",userEntity);
            //查询出此条任务的具体信息
            Task taskById = taskService.findTaskByTaskId(task.getTaskId());
            model.addAttribute("task",taskById);
            //当前项目信息
            model.addAttribute("projectId",taskById.getProjectId());
            //判断当前用户有没有对该任务点赞
            boolean isFabulous = taskService.judgeFabulous(task);
            model.addAttribute("isFabulous",isFabulous);
            //判断当前用户有没有收藏该任务
            boolean isCollect = taskService.judgeCollectTask(task);
            model.addAttribute("isCollect",isCollect);
            //查询任务的标签
            List<Tag> tagList = taskService.findTaskTag(task.getTaskId());
            if(tagList != null && tagList.size() > 0){
                Collections.reverse(tagList);
                model.addAttribute("tagList",tagList);
            }
            //查询出任务的关联信息
            List bindings = bindingService.listBindingInfoByPublicId(task.getTaskId());
            model.addAttribute("bindings",bindings);
            //查询出该任务的创建者信息
            UserEntity taskCreate = userService.findTaskCreate(task.getTaskId());
            model.addAttribute("taskCreate",taskCreate);
            //返回当前任务的所有子任务信息
            List<Task> subLevelTask = taskService.findTaskByFatherTask(task.getTaskId());
            jsonObject.put("subLevelTask",subLevelTask);
            model.addAttribute("subLevelTask",subLevelTask);
            //拿到该任务的执行者信息
            UserEntity executorInfo = userService.findExecutorByTask(task.getTaskId());
            if(executorInfo != null){
                model.addAttribute("executor",executorInfo);
            } else{
                executorInfo = new UserEntity();
                executorInfo.setUserName("待认领");
                executorInfo.setId("");
                model.addAttribute("executor",executorInfo);
            }
            //查询出该任务的参与者信息
            List<UserEntity> participantList = taskMemberService.findTaskMemberInfo(task.getTaskId(),"参与者",executorInfo.getId());
            model.addAttribute("participantList",participantList);
            //查询出项目下的成员
            List<UserEntity> projectAllMember = userService.findProjectAllMember(projectId);
            if(projectAllMember != null && projectAllMember.size() > 0){
                model.addAttribute("members",projectAllMember);
            } else{
                if(projectAllMember == null){
                    projectAllMember = new ArrayList<UserEntity>();
                }
                UserEntity userEntity1 = new UserEntity();
                userEntity1.setUserName("该项目下没有成员");
                projectAllMember.add(userEntity1);
                model.addAttribute("members",projectAllMember);
            }
            if(taskById.getParentId() == null){
                //查询出该任务所在的位置信息
                Relation menuRelation = relationService.findMenuInfoByTaskId(task.getTaskId());
                //根据菜单信息查询出该任务的所在的分组 和 项目信息
                TaskMenuVO taskMenuVO = relationService.findProjectAndGroupInfoByMenuId(menuRelation.getRelationId());
                model.addAttribute("menuRelation",menuRelation);
                model.addAttribute("taskMenuVo",taskMenuVO);
            }
            //查询出该任务的日志信息
            List<TaskLog> logList = taskLogService.initTaskLog(task.getTaskId());
            Collections.reverse(logList);
            if(!logList.isEmpty()){
                model.addAttribute("taskLog",logList);
            } else{
                model.addAttribute("taskLog","没有操作日志记录!");
            }
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
     * @param executor 任务的执行者信息
     * @return
     */
    @PostMapping("findProjectAllMember")
    @ResponseBody
    public JSONObject findProjectAllMember(String executor,@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<UserEntity> list = taskService.findProjectAllMember(projectId,executor);
            if(list != null && list.size() > 0){
                jsonObject.put("data",list);
            } else{
                jsonObject.put("data",null);
                jsonObject.put("msg","无数据");
            }
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
            TaskLogVO taskLogVO = taskService.updateTask(task);
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
     * 智能分组  查询出 三个组的数据 分别为
     * 1.今天的任务
     * 2.未完成的任务
     * 3.已完成的任务
     *
     * @param status 需要查询的任务状态
     * @param projectId 任务的id
     * @return
     */
    @PostMapping("intelligenceGroup")
    @ResponseBody
    public JSONObject intelligenceGroup(@RequestParam String status,@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(StringUtils.isEmpty(status)){
                jsonObject.put("msg","请选择状态信息!");
                return  jsonObject;
            }
            //获取分组数据
            List<Task> taskList = taskService.intelligenceGroup(status,projectId);
            if(taskList.size() > 0){
                jsonObject.put("data",taskList);
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","没有数据!");
            }
        } catch (Exception e){
            log.error("系统异常! 数据获取失败,{}",e);
            throw new AjaxException(e);
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
            TaskLogVO taskLogVO = taskService.removeExecutor(taskId);
            jsonObject.put("taskLog",taskLogVO);
            jsonObject.put("msg","移除成功!");
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,操作失败,{}",e);
        }
        return jsonObject;
    }

    /**
     * 更新任务的执行者
     * @param taskId 任务id
     * @param uName 新的执行者的名字
     * @param userInfoEntity 新的执行者的信息
     * @return0
     */
    @PostMapping("updateTaskExecutor")
    @ResponseBody
    public JSONObject updateTaskExecutor(String taskId,UserInfoEntity userInfoEntity,String uName){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.updateTaskExecutor(taskId, userInfoEntity, uName);
            jsonObject.put("msg","修改成功");
            jsonObject.put("result",1);
            jsonObject.put("taskLog",taskLogVO);
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("taskLog",taskLogVO);
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.U.getName());
            messagingTemplate.convertAndSend("/topic/subscribe",new ServerMessage(JSON.toJSONString(taskPushType).substring(0,JSON.toJSONString(taskPushType).length()-1)+",\"taskId\":"+JSON.toJSONString(taskId)+",\"userInfo\":"+JSON.toJSONString(userInfoEntity)+",\"uName\":"+JSON.toJSONString(uName) +"}"));
            messagingTemplate.convertAndSend("/topic/"+taskId,new ServerMessage(JSON.toJSONString(taskPushType).substring(0,JSON.toJSONString(taskPushType).length()-1)+",\"taskId\":"+JSON.toJSONString(taskId)+",\"userInfo\":"+JSON.toJSONString(userInfoEntity)+",\"uName\":"+JSON.toJSONString(uName)+",\"taskLog\":"+JSON.toJSONString(taskLogVO) +"}"));
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
     * 任务拖住
     * @param oldMenuTaskId 旧任务菜单的所有任务id
     * @param newMenuTaskId 新任务菜单的所有任务id
     * @param oldMenuId 旧菜单id
     * @param newMenuId 新菜单id
     * @param taskId 被移动的任务id
     * @return
     */
    @PostMapping("taskOrder")
    @ResponseBody
    public JSONObject taskOrder(String[]oldMenuTaskId,String[] newMenuTaskId,String oldMenuId,String newMenuId,String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.orderOneTaskMenu(oldMenuTaskId,newMenuTaskId,oldMenuId,newMenuId,taskId);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("任务排序失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 任务的聊天室
     * @param taskLog 任务聊天的信息
     * @return
     */
    @PostMapping("chat")
    @ResponseBody
    public JSONObject chat(TaskLog taskLog){
        JSONObject jsonObject = new JSONObject();
        try {
            taskLog.setId(IdGen.uuid());
            taskLog.setMemberName(ShiroAuthenticationManager.getUserEntity().getUserName());
            taskLog.setMemberImg(ShiroAuthenticationManager.getUserEntity().getUserInfo().getImage());
            taskLog.setCreateTime(System.currentTimeMillis());
            taskLogService.saveTaskLog(taskLog);
            jsonObject.put("result",1);
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.A14.getName());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("taskLog",taskLog);
            taskPushType.setObject(map);
            messagingTemplate.convertAndSend("/topic/"+taskLog.getTaskId(),new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("操作失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }
}
