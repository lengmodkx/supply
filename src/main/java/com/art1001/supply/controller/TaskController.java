package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskLogService;
import com.art1001.supply.service.task.TaskMemberService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import io.netty.handler.codec.json.JsonObjectDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.xml.ws.RequestWrapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    /**
     * 添加新任务
     * @param project 得到当前项目的实体信息
     * @param task 任务实体信息
     * @return
     */
    @PostMapping("saveTask")
    @ResponseBody
    public JSONObject saveTask(Project project,Task task,String members){
        JSONArray objects = JSONObject.parseArray(members);
        List<UserEntity> list = objects.toJavaList(UserEntity.class);
        UserEntity[] userEntity = list.toArray(new UserEntity[0]);
        JSONObject jsonObject = new JSONObject();
        try {
            //保存任务信息到数据库
            TaskLogVO taskLogVO = taskService.saveTask(userEntity,project,task);
            jsonObject.put("msg","添加任务成功!");
            jsonObject.put("result",1);
            jsonObject.put("taskLog",taskLogVO);
        } catch (Exception e){
            jsonObject.put("msg","任务添加失败!");
            jsonObject.put("result","0");
            log.error("当前任务保存失败! ,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 添加项目成员
     * @param task
     * @param addUserEntity 要添加项目成员
     * @param removeUserEntity 要移除的项目成员
     */
    @PostMapping("addAndRemoveTaskMember")
    @ResponseBody
    public JSONObject addAndRemoveTaskMember(@RequestParam Task task,@RequestParam UserEntity[] addUserEntity,@RequestParam UserEntity[] removeUserEntity){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.addAndRemoveTaskMember(task,addUserEntity,removeUserEntity);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","添加成功!");
                jsonObject.put("result",taskLogVO.getResult());
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","添加失败!");
                jsonObject.put("result",taskLogVO.getResult());
            }
        } catch (Exception e){
            log.error("系统异常,成员添加失败! 当前任务id: ,{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 移除任务-成员关系
     * @param task 当前项目实体信息
     * @param userEntity 被移除的用户的信息
     * @return
     */
    @PostMapping("removeTaskMember")
    @ResponseBody
    public JSONObject removeTaskMember(@RequestParam Task task,@RequestParam UserEntity userEntity){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.removeTaskMember(task,userEntity);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","任务参与者移除成功!");
                jsonObject.put("result",taskLogVO.getResult());
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","任务参与者移除失败!");
                jsonObject.put("result",taskLogVO.getResult());
            }
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
    public JSONObject resetAndCompleteTask(@RequestParam Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            //改变任务状态
            TaskLogVO taskLogVO = taskService.resetAndCompleteTask(task);
            if(taskLogVO.getResult() >1){
                jsonObject.put("msg","修改成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","修改失败!");
                jsonObject.put("result","0");
            }
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
     * 清除任务的开始时间和结束时间
     * @param task 任务的实体信息
     * @return
     */
    @PostMapping("removeTaskStartAndEndTime")
    @ResponseBody
    public JSONObject removeTaskStartAndEndTime(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.removeTaskStartAndEndTime(task);
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
     * 给当前任务设置标签
     * @param tag 标签名称
     * @param taskId 当前任务id
     * @param projectId 当前任务的id
     * @return
     */
    @PostMapping("addTaskTags")
    @ResponseBody
    public JSONObject addTags(Tag tag,@RequestParam String taskId,@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            //根据标签名称查询 当前存不存在数据库 如果存在直接绑定到当前任务,如果不存在则先插入标签 在绑定到当前任务
            int countByTagName = tagService.findCountByTagName(projectId, tag.getTagName());
            if(countByTagName == 0){
                tag.setTagId(tagService.saveTag(tag));
            }
            //更新当前任务的标签信息
            TaskLogVO taskLogVO = taskService.addTaskTags(tag, taskId,countByTagName);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","标签添加成功!");
                jsonObject.put("result",taskLogVO.getResult());
                jsonObject.put("taskLog",taskLogVO);
            } else{
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
            jsonObject.put("result","1");
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
    public JSONObject removeTaskTag(@RequestParam Tag[] tags,@RequestParam Tag tag,@RequestParam String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            //更新该任务的标签信息
            int result = taskService.removeTaskTag(tags,tag,taskId);
            if(result > 0){
                jsonObject.put("msg","标签移除成功!");
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","标签移除失败!");
                jsonObject.put("result","0");
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
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","失败!");
                jsonObject.put("result","0");
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
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","取消失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常,取消赞失败! 当前任务id:,{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 添加子任务
     * @param currentTask 父任务信息
     * @param subLevel 子任务信息
     * @param projectId 当前项目id
     * @return
     */
    @PostMapping("addSubLevelTask")
    @ResponseBody
    public JSONObject addSubLevelTask(Task currentTask,Task subLevel,String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            //保存子任务信息至数据库
            TaskLogVO taskLogVO = taskService.addSubLevelTasks(currentTask,subLevel);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","添加成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","添加失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常! 添加子级任务失败! 当前任务id: ,{},{}",currentTask.getTaskId(),e);
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
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常! 状态更新失败 当前任务id: ,{}{}",task.getTaskId(),e);
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
     * 取消收藏任务
     * @param task 任务的信息
     * @return
     */
    @PostMapping("cancelCollectTask")
    @ResponseBody
    public JSONObject cancelCollectTask(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.cancelCollectTask(task);
            jsonObject.put("msg","取消收藏成功");
            jsonObject.put("result","1");
        } catch (Exception  e){
            log.error("系统异常,取消收藏失败! 当前任务id: ,{},{}",task.getTaskId(),e);
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
            taskService.SettingUpPrivacyPatterns(task);
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
            jsonObject.put("userExistTask",userByIsExistTask.get("existList"));
            jsonObject.put("userNotExistTask",userByIsExistTask.get("notExistList"));
            jsonObject.put("result","1");
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
    public String initTask(Task task,String projectId,String type,Model model){
        JSONObject jsonObject = new JSONObject();
        //时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //获取当前用户信息
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            model.addAttribute("user",userEntity);
            //当前项目信息
            model.addAttribute("projectId",projectId);
            //是创建任务还是更新任务
            model.addAttribute("type",type);
            //查询出此条任务的具体信息
            Task taskById = taskService.findTaskByTaskId(task.getTaskId());
            //format
            model.addAttribute("task",taskById);
            //判断当前用户有没有对该任务点赞
            boolean isFabulous = taskService.judgeFabulous(task);
            //判断当前用户有没有收藏该任务
            boolean isCollect = taskService.judgeCollectTask(task);
            //返回该任务的关联信息
            Map<String, List> taskRelation = taskService.findTaskRelation(task.getTaskId());
            //该任务关联的任务
            List<Task> taskList = taskRelation.get("relationTask");
            //该任务关联的文件
            List<File> fileList = taskRelation.get("relationFile");
            if(taskList != null && taskList.size() > 0){
                model.addAttribute("relationTask",taskList);
            } else{
                model.addAttribute("relationTask","无关联任务数据");
            }
            if(fileList != null && fileList.size() > 0){
                jsonObject.put("relationFile",fileList);
                model.addAttribute("relationFile",fileList);
            } else{
                model.addAttribute("relationFile","无关联文件数据");
            }
            //返回当前任务的所有子任务信息
            List<Task> subLevelTask = taskService.findTaskByFatherTask(task.getTaskId());
            if(subLevelTask != null & subLevelTask.size() > 0){
                jsonObject.put("subLevelTask",subLevelTask);
                model.addAttribute("subLevelTask",subLevelTask);
            }
            //查询出该任务的参与者信息
            List<UserInfoEntity> participantList = taskMemberService.findTaskMemberInfo(task.getTaskId(),"参与者");
            if(!participantList.isEmpty()){
                model.addAttribute("participantList",participantList);
            } else{
                model.addAttribute("participantList","无数据!");
            }
            //拿到该任务的执行者信息
            UserEntity executorInfo = userService.findExecutorByTask(task.getTaskId());
            if(executorInfo != null){
                model.addAttribute("executor",executorInfo);
            } else{
                executorInfo = new UserEntity();
                executorInfo.setUserName("待认领");
                model.addAttribute("executor",executorInfo);
            }
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
            //查询出该任务所在的位置信息
            Relation menuRelation = relationService.findMenuInfoByTaskId(task.getTaskId());
            //根据菜单信息查询出该任务的所在的分组 和 项目信息
            TaskMenuVO taskMenuVO = relationService.findProjectAndGroupInfoByMenuId(menuRelation.getRelationId());
            model.addAttribute("menuRelation",menuRelation);
            model.addAttribute("taskMenuVo",taskMenuVO);
            //查询出该任务的日志信息
            List<TaskLog> logList = taskLogService.initTaskLog(task.getTaskId());
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
    public JSONObject findProjectAllMember(@RequestParam String executor,@RequestParam String projectId){
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
            jsonObject.put("msg","必须完成子级任务,才能完成父级任务!");
            jsonObject.put("result","0");
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
            List<Task> taskList = taskService.findTaskByExecutor(uId,projectId);
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
    public JSONObject removeExecutor(@RequestParam String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.removeExecutor(taskId);
            jsonObject.put("msg","移除成功!");
            jsonObject.put("result","1");
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
     * @return
     */
    @PostMapping("updateTaskExecutor")
    @ResponseBody
    public JSONObject updateTaskExecutor(@RequestParam String taskId,@RequestParam UserInfoEntity userInfoEntity,@RequestParam String uName){
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.updateTaskExecutor(taskId,userInfoEntity,uName);
            jsonObject.put("msg","修改成功");
            jsonObject.put("result","1");
        } catch (Exception e){
            log.error("系统异常,修改失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }
}
