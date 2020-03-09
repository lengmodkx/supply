package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.AutomationRule;
import com.art1001.supply.annotation.Log;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.automation.constans.AutomationRuleConstans;
import com.art1001.supply.entity.fabulous.Fabulous;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskRemindRule;
import com.art1001.supply.entity.user.UserNews;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.fabulous.FabulousService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.task.TaskRemindRuleService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.wechat.message.service.WeChatAppMessageService;
import com.art1001.supply.wechat.message.service.WeChatAppMessageTemplateDataBuildService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务增删改查，复制，移动
 * @author 汪亚锋
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@RestController
@RequestMapping("tasks")
public class  TaskApi extends BaseController {

    @Resource
    private TaskService taskService;

    @Resource
    private FabulousService fabulousService;

    @Resource
    private UserService userService;

    @Resource
    private RelationService relationService;

    @Resource
    private WeChatAppMessageService weChatAppMessageService;

    @Resource
    private TaskRemindRuleService taskRemindRuleService;

    @Resource
    private FileService fileService;

    @Resource
    private UserNewsService userNewsService;

    @Resource
    private WeChatAppMessageTemplateDataBuildService updateTaskJoinInfo;

    /**
     * 任务页面初始化
     * @return object
     */
    @GetMapping("/{taskId}")
    public JSONObject getTask(@PathVariable(value = "taskId") String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            Task task = taskService.taskInfoShow(taskId);
            jsonObject.put("data",task);
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e){
            e.printStackTrace();
            throw new AjaxException("系统异常,获取任务信息失败!",e);
        }
    }

    /**
     * 创建任务
     * @return object
     */
    @AutomationRule(value = "#task.taskId",trigger = AutomationRuleConstans.ADD_TASK)
    @Push(value = PushType.A1,type = 1)
    @PostMapping
    public JSONObject addTask(Task task){
        try {
            taskService.saveTask(task);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msgId",task.getProjectId());
            jsonObject.put("data",task.getProjectId());
            jsonObject.put("result",1);
            jsonObject.put("task", taskService.getById(task.getTaskId()));

//            //推送微信小程序消息给多个用户
//            WeChatAppMessageTemplate weChatAppMessageTemplate = WeChatAppMessageTemplateBuild.createTask();
//            weChatAppMessageService.pushToMultipleUsers(
//                    Arrays.asList(task.getTaskUIds().split(",")),
//                    weChatAppMessageTemplate,
//                    updateTaskJoinInfo
//            );
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常任务创建失败!",e);
        }
    }

    /**
     * 获取从任务上传的附件信息
     * @param taskId 任务id
     * @return
     */
    @GetMapping("{taskId}/files")
    public JSONObject getFiles(@PathVariable String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",fileService.list(new QueryWrapper<File>().eq("public_id", taskId).eq("public_lable", 1).orderByDesc("create_time")));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,信息获取失败!",e);
        }
    }

    /**
     * 删除任务
     * @param taskId 任务id
     * @return object
     */
    @Push(value = PushType.A2,type = 1)
    @DeleteMapping("/{taskId}")
    public Result deleteTask(@PathVariable(value = "taskId")String taskId){
        try{
            taskService.removeById(taskId);
            return Result.success();
        }catch(Exception e){
            throw new AjaxException("系统异常,删除失败",e);
        }
    }

    @PutMapping("/{taskId}/changeStatus")
    public JSONObject changeStatus(@PathVariable(value = "taskId")String taskId,
                                   @RequestParam(value = "projectId") String projectId,
                                   @RequestParam(value = "status")String status){
        JSONObject object = new JSONObject();




        return  object;
    }




    /**
     * 完成任务
     * @param taskId 任务id
     * @return object
     */
    @AutomationRule(value = "#taskId",trigger = "completed",objectValue = "#label")
    @Push(value = PushType.A3,type = 3)
    @PutMapping("/{taskId}/finish")
    public JSONObject finishTask(@PathVariable(value = "taskId")String taskId,
                                 @RequestParam(value = "projectId") String projectId,
                                 @RequestParam(required = false,defaultValue = "0") Integer label){
        JSONObject object = new JSONObject();
        try{
            taskService.completeTask(taskId);
            String parentId = taskService.getById(taskId).getParentId();
            object.put("status",1);
            object.put("result",1);
            //判断点击的任务是否在父任务页面
            if(label == 1){
                object.put("data",new JSONObject().fluentPut("taskId",parentId).fluentPut("projectId",projectId));
            } else{
                object.put("data",new JSONObject().fluentPut("taskId",taskId).fluentPut("projectId",projectId));
            }
            object.put("msgId",projectId);
            object.put("msg","更新成功");
            object.put("publicType",Constants.TASK);
            object.put("id",taskId);
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch(Exception e){
            log.error("系统异常,状态更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 重做任务
     * @param taskId 任务id
     * @return JSONObject
     */
    @AutomationRule(value = "#taskId",trigger = "redone",objectValue = "#label")
    @Log(PushType.A4)
    @Push(value = PushType.A4,type = 3)
    @PutMapping("/{taskId}/unFinish")
    public JSONObject unFinishTask(@PathVariable(value = "taskId")String taskId,
                                   @RequestParam(value = "projectId") String projectId,
                                   @RequestParam(required = false,defaultValue = "0") Integer label){
        JSONObject object = new JSONObject();
        try{
            //这里判断父任务是否已经完成
            String parentId = taskService.getById(taskId).getParentId();
            if(!parentId.equals("0")){
                Task pTask = taskService.getOne(new QueryWrapper<Task>().eq("task_id", parentId));
                if(pTask.getTaskStatus()){
                    object.put("result",0);
                    object.put("msg", "父任务已经完成不能重做子任务!");
                    return object;
                }
            }
            object.put("msgId",projectId);
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskStatus(false);
            taskService.updateById(task);
            Task pTask = new Task();
            pTask.setTaskId(parentId);
            pTask.setUpdateTime(System.currentTimeMillis());
            pTask.setSubIsAllComplete(false);
            taskService.updateById(pTask);
            object.put("result",1);
            object.put("msg","更新成功");
            if(label == 1){
                object.put("data",new JSONObject().fluentPut("taskId",parentId).fluentPut("projectId",object.getString("msgId")));
            } else{
                object.put("data",new JSONObject().fluentPut("taskId",taskId).fluentPut("projectId",object.getString("msgId")));
            }
            object.put("id",taskId);
            object.put("publicType", Constants.TASK);
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 排列菜单下的任务
     * @param projectId 项目id
     * @param taskIds 任务ids
     * @return
     */
    @Log
    @AutomationRule(value = "#taskId",trigger = AutomationRuleConstans.DRAG_TASK)
    @Push(value = PushType.A27,type = 1)
    @PutMapping("/order")
    public JSONObject order(@RequestParam(value = "projectId") String projectId,
                            @RequestParam(value = "taskId",required = false) String taskId,
                            @RequestParam(value = "taskIds") String taskIds,
                            @RequestParam(value = "newMenu",required = false) String newMenu){
        JSONObject object = new JSONObject();
        try{
            taskService.orderTask(taskIds,taskId,newMenu);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",projectId);
            object.put("data",projectId);
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务名称
     * @param taskId 任务id
     * @param taskName 任务名称
     * @return JSONObject
     */
    @Log(PushType.A5)
    @Push(value = PushType.A5,type = 3)
    @PutMapping("/{taskId}/name")
    public JSONObject upadteTaskName(@PathVariable(value = "taskId")String taskId,
                                     @RequestParam(value = "taskName")String taskName){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskName(taskName);
            taskService.updateById(task);
            String[] taskJoinAndExecutorId = taskService.getTaskJoinAndExecutorId(taskId);
            if(taskJoinAndExecutorId != null){
                for (String s : taskJoinAndExecutorId) {
                    UserNews userNews = new UserNews();
                    userNews.setNewsToUserId(s);
                    userNews.setNewsPublicId(taskId);
                    userNews.setNewsName(taskName);
                    userNewsService.updateUserNews(userNews);
                }
            }

            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data",taskId);
            object.put("id",taskId);
            object.put("name",taskName);
            object.put("publicType", Constants.TASK);
        }catch(Exception e){
            log.error("系统异常,任务名称更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务执行者
     * @param taskId 任务id
     * @param userId 执行者id
     * @return JSONObject
     */
    @AutomationRule(value = "#taskId",trigger = "settingExecutors",objectValue = "#userId")
    @Log(PushType.A6)
    @Push(value = PushType.A6,type = 3)
    @PutMapping("/{taskId}/executor")
    public JSONObject upadteTaskExecutor(@PathVariable(value = "taskId")String taskId,
                                         @RequestParam(value = "executor")String userId){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setExecutor(userId);
            taskService.updateById(task);
            Task one = taskService.getOne(new QueryWrapper<Task>().lambda().select(Task::getParentId, Task::getTaskId).eq(Task::getTaskId, taskId));
            if(one.getParentId().equals("0")){
                object.put("msgId",this.getTaskProjectId(taskId));
                object.put("data",taskId);
            } else{
                object.put("msgId", taskService.findChildTaskProject(taskId));
                object.put("data", one.getParentId());
            }
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("id",taskId);
            object.put("publicType", Constants.TASK);
        }catch(Exception e){
            log.error("系统异常,执行者更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务开始时间
     * @param taskId 任务id
     * @param startTime 任务开始时间
     * @return JSONObject
     */
    @NotEmpty
    @Push(value = PushType.A7,type = 3)
    @PutMapping(value = "/{taskId}/starttime")
    public JSONObject upadteTaskStartTime(@PathVariable(value = "taskId")String taskId,
                                          @RequestParam(value = "startTime")Long startTime){
        JSONObject object = new JSONObject();
        try{
            taskService.updateStartTime(taskId,startTime);
            Task task = new Task();
            task.setTaskId(taskId);
            task.setUpdateTime(System.currentTimeMillis());
            if (0==startTime){
                task.setStartTime(0L);
            }else {
                task.setStartTime(startTime);
            }
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data", taskId);
            object.put("id",taskId);
            object.put("publicType", Constants.TASK);
        }catch(Exception e){
            log.error("系统异常,开始时间更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务结束时间
     * @param taskId 任务id
     * @param endTime 任务结束时间
     * @return JSONObject
     */
    @AutomationRule(value = "#taskId",trigger = "endTime",objectValue = "#endTime")
    @Push(value = PushType.A8,type = 3)
    @PutMapping("/{taskId}/endtime")
    public JSONObject upadteTaskEndTime(@PathVariable(value = "taskId")String taskId,
                                        @RequestParam(value = "endTime")Long endTime){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setUpdateTime(System.currentTimeMillis());
            if (0==endTime){
                task.setEndTime(0L);
            }else {
                task.setEndTime(endTime);
            }
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data", taskId);
            object.put("id",taskId);
            object.put("publicType", Constants.TASK);
        }catch(Exception e){
            log.error("系统异常,结束时间更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务重复性
     * @param taskId 任务id
     * @param repeat 任务结束时间
     * @return JSONObject
     */
    @AutomationRule(value = "#taskId",trigger = "repeat",objectValue = "#repeat")
    @Log(PushType.A9)
    @Push(value = PushType.A9,type = 3)
    @PutMapping("/{taskId}/repeat")
    public JSONObject upadteTaskRepeat(@PathVariable(value = "taskId")String taskId,
                                       @RequestParam(value = "repeat")String repeat){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setRepeat(repeat);
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data",taskId);
            object.put("id",taskId);
            object.put("publicType",Constants.TASK);
        }catch(Exception e){
            log.error("系统异常,重复性更新失败:",e);
            throw new AjaxException("系统异常,重复性更新失败:",e);
        }
        return object;
    }

    /**
     * 更新任务提醒规则
     * @param taskId 任务id
     * @param remindType 任务的提醒规则类型
     * @param num 时间数量
     * @param timeType 时间类型
     * @param customTime 自定义时间
     * @return
     */
    @Log(PushType.A24)
    @Push(value = PushType.A24,type = 1)
    @PutMapping("/{taskId}/remind")
    public JSONObject updateTaskRemind(@PathVariable(value = "taskId")String taskId,
                                       @RequestParam(value = "ruleId") String ruleId,
                                       @RequestParam(value = "remindType",required = false)String remindType,
                                       @RequestParam(value = "num", required = false) Integer num,
                                       @RequestParam(value = "timeType", required = false) String timeType,
                                       @RequestParam(value = "customTime", required = false) String customTime){
        JSONObject object = new JSONObject();
        try{
            //更新任务提醒
            TaskRemindRule taskRemindRule = new TaskRemindRule();
            taskRemindRule.setTaskId(taskId);
            taskRemindRule.setId(ruleId);
            taskRemindRule.setNum(num);
            taskRemindRule.setRemindType(remindType);
            taskRemindRule.setTimeType(timeType);
            taskRemindRule.setCustomTime(customTime);
            taskService.updateTaskRemind(taskRemindRule);
            object.put("msg","时间规则更新成功!");
            object.put("result",1);
        } catch(Exception e){
            log.error("系统异常,提醒模式更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移除任务提醒规则
     * @param id 规则的id
     * @return
     */
    @Log(PushType.A23)
    @Push(value = PushType.A23,type = 1)
    @DeleteMapping("/{id}/remind")
    public JSONObject removeRemind(@PathVariable("id") String id){
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.removeRemind(id);
            jsonObject.put("msg","移除成功!");
            jsonObject.put("result",1);
        } catch (SchedulerException e){
            log.error(e.getMessage(),e);
            throw new AjaxException(e);
        } catch (Exception e){
            log.error("系统异常,提醒规则移除失败!",e);
            throw new AjaxException("系统异常,提醒规则移除失败!",e);
        }
        return jsonObject;
    }

    /**
     * 新增任务提醒规则
     * @param taskId 任务id
     * @param remindType 任务的提醒规则类型
     * @param num 时间数量
     * @param timeType 时间类型
     * @param customTime 自定义时间
     * @return
     */
    @Log(PushType.A10)
    @Push(value = PushType.A10,type = 1)
    @PostMapping("/{taskId}/remind")
    public JSONObject addTaskRemind(@PathVariable(value = "taskId")String taskId,
                                       @RequestParam(value = "remindType",required = false)String remindType,
                                       @RequestParam(value = "num", required = false) Integer num,
                                       @RequestParam(value = "timeType", required = false) String timeType,
                                       @RequestParam(value = "customTime", required = false) String customTime,
                                       @RequestParam(value = "users") String users){
        JSONObject object = new JSONObject();
        try{
            TaskRemindRule t = new TaskRemindRule();
            t.setTaskId(taskId);
            t.setRemindType(remindType);
            t.setTimeType(timeType);
            t.setCustomTime(customTime);
            t.setNum(num);
            taskService.addTaskRemind(t,users);
            object.put("result",1);
            object.put("msg","成功!");
        } catch (ServiceException e){
            throw new AjaxException(e);
        } catch(Exception e){
            log.error("系统异常,新增提醒规则失败!",e);
            throw new AjaxException("系统异常,新增提醒规则失败!",e);
        }
        return object;
    }

    /**
     * 更新任务提醒的成员信息
     * @param taskId 任务id
     * @param users 成员信息
     * @return
     */
    @Log(PushType.A26)
    @PutMapping("/{taskId}/remind/user")
    public JSONObject updateRemindUsers(@PathVariable String taskId, @RequestParam("users") String users){
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.updateRemindUsers(taskId,users);
            jsonObject.put("result",1);
            jsonObject.put("msg","更新成功!");
        } catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取所有任务提醒信息
     * @return
     */
    @Log(PushType.A25)
    @GetMapping("/{taskId}/remind")
    public JSONObject getRemind(@PathVariable String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",taskRemindRuleService.list(new QueryWrapper<TaskRemindRule>().eq("task_id",taskId)));
            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功!");
        } catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新任务备注
     * @param taskId 任务id
     * @param remarks 任务备注信息
     * @return JSONObject
     */
    @Log(PushType.A11)
    @Push(value = PushType.A11,type = 1)
    @PutMapping("/{taskId}/remarks")
    public JSONObject upadteTaskRemarks(@PathVariable(value = "taskId")String taskId,
                                       @RequestParam(value = "remarks")String remarks){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            if(remarks.equals("<p><br></p>")){
                task.setRemarks("");
            } else{
                task.setRemarks(remarks);
            }
            taskService.updateById(task);
            object.put("result",1);
            object.put("remarks",remarks);
            object.put("msgId",getTaskProjectId(taskId));
            object.put("data",taskId);
            //object.put("data",new JSONObject().fluentPut("type","任务").fluentPut("id", taskId));
            object.put("id",taskId);
            object.put("msg","更新成功");
        }catch(Exception e){
            log.error("系统异常,备注更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务优先级
     * @param taskId 任务id
     * @param priority 任务优先级
     * @return JSONObject
     */
    @AutomationRule(value = "#taskId",trigger = "priority",objectValue = "#priority")
    @Log(PushType.A12)
    @Push(value = PushType.A12,type = 3)
    @PutMapping("/{taskId}/priority")
    public JSONObject upadteTaskPriority(@PathVariable(value = "taskId")String taskId,
                                         @RequestParam(value = "priority")String priority){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setPriority(priority);
            taskService.updateById(task);
            object.put("result",1);
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data",taskId);
            object.put("id",taskId);
            object.put("publicType", Constants.TASK);
        }catch(Exception e){
            log.error("系统异常,优先级更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 新增子任务
     * @param taskId 父任务id
     * @param taskName 子任务名称
     * @param executor 子任务的执行者
     * @param startTime 子任务的结束时间
     * @return JSONObject
     */
    @Log(PushType.A13)
    @Push(value = PushType.A13,type = 3)
    @PostMapping("/{taskId}/addchild")
    public JSONObject addChildTask(@PathVariable(value = "taskId")String taskId,
                                   @RequestParam(value = "taskName")String taskName,
                                   @RequestParam(value = "executor",required = false)String executor,
                                   @RequestParam(value = "startTime",required = false)String startTime){
        JSONObject object = new JSONObject();
        try{
            if(StringUtils.isEmpty(taskName)){
                object.put("result",0);
                object.put("msg", "任务名称不能为空！");
                return object;
            }
            Task task = new Task();
            task.setParentId(taskId);
            task.setTaskName(taskName);
            task.setProjectId(this.getTaskProjectId(taskId));

            //子任务存分组
            Task taskGroupId = taskService.findTaskByTaskId(taskId);
                    //getOne(new QueryWrapper<Task>().select("project_id").eq("task_id", taskId));
            if(StringUtils.isNotEmpty(taskGroupId.getTaskGroupId())){
                task.setTaskGroupId(taskGroupId.getTaskGroupId());
            }

            if(StringUtils.isNotEmpty(executor)){
                task.setExecutor(executor);
            }
            if(StringUtils.isNotEmpty(startTime)){
                task.setStartTime(DateUtils.strToLong(startTime));
            }
            Task parentTask = taskService.getById(taskId);
            Integer pLevel = parentTask.getLevel();
            task.setLevel(pLevel + 1);
            taskService.saveTask(task);
            Task pTask = new Task();
            pTask.setTaskId(taskId);
            pTask.setUpdateTime(System.currentTimeMillis());
            pTask.setSubIsAllComplete(false);
            taskService.updateById(pTask);
            object.put("result",1);
            object.put("msg","创建成功!");
            object.put("data",new JSONObject().fluentPut("taskId",taskId).fluentPut("projectId", taskService.findChildTaskProject(taskId)));
            object.put("msgId",taskService.findChildTaskProject(taskId));
            object.put("id",taskId);
        }catch(Exception e){
            log.error("系统异常,子任务添加失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务参与者
     * @param taskId 任务id
     * @param taskUids 参与者id
     * @return
     */
    @SuppressWarnings("unchecked")
    @Push(value = PushType.A14,type = 1)
    @PutMapping("/{taskId}/members")
    public JSONObject addTaskUids(@PathVariable(value = "taskId")String taskId,
                                  @RequestParam(value = "taskUids")String taskUids){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskUIds(taskUids);
            taskService.updateById(task);

            String[] taskIdList = taskUids.split(",");
            userNewsService.saveUserNews(taskIdList ,taskId, Constants.TASK,ShiroAuthenticationManager.getUserEntity().getUserName() + PushType.A14.getName());

//            //推送微信小程序消息给多个用户
//            WeChatAppMessageTemplate weChatAppMessageTemplate = WeChatAppMessageTemplateBuild.updateTaskJoin();
//            weChatAppMessageService.pushToMultipleUsers(
//                    Arrays.asList(taskIdList),
//                    weChatAppMessageTemplate,
//                    updateTaskJoinInfo
//            );


            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data",taskId);
            object.put("id",taskId);
        }catch(Exception e){
            log.error("系统异常,任务参与者更新:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 复制任务
     * @param taskId 任务id
     * @param projectId 项目id
     * @param groupId 组id
     * @param menuId 菜单id
     * @return 是否复制成功
     */
    @Log(PushType.A15)
    @Push(value = PushType.A15,type = 1)
    @PostMapping("/{taskId}/copy")
    public JSONObject copyTask(@PathVariable(value = "taskId")String taskId,
                               @RequestParam(value = "projectId")String projectId,
                               @RequestParam(value = "groupId")String groupId,
                               @RequestParam(value = "menuId")String menuId){
        JSONObject object = new JSONObject();
        try{
            taskService.copyTask(taskId,projectId,groupId,menuId);
            object.put("data",projectId);
            object.put("result",1);
            object.put("msg","复制成功");
            object.put("msgId",projectId);
            object.put("id",taskId);
        }catch(Exception e){
            log.error("系统异常,任务复制失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移动任务
     * @param taskId 任务id
     * @param projectId 项目id
     * @param groupId 组id
     * @param menuId 菜单id
     * @return
     */
    @AutomationRule(value = "#taskId",trigger = AutomationRuleConstans.MOVE_TASK)
    @Log(PushType.A16)
    @Push(value = PushType.A16,type = 2 )
    @PutMapping("/{taskId}/move")
    public JSONObject moveTask(@PathVariable(value = "taskId")String taskId,
                               @RequestParam(value = "projectId")String projectId,
                               @RequestParam(value = "groupId")String groupId,
                               @RequestParam(value = "menuId")String menuId){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            //获取到任务移动前的项目id
            String taskProjectId = this.getTaskProjectId(taskId);
            taskService.mobileTask(taskId,projectId,groupId,menuId);
            object.put("result",1);
            object.put("msg","移动成功");
            Map<String,Object> maps = new HashMap<String,Object>(2);
            if(projectId.equals(taskProjectId)){
                maps.put(projectId,projectId);
            } else{
                maps.put(projectId,projectId);
                maps.put(taskProjectId,taskProjectId);
            }
            object.put("data",maps);
            object.put("id",taskId);
        }catch(Exception e){
            log.error("系统异常,任务移动失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移到回收站
     * @param taskId 任务id
     * @return
     */
    @Push(value = PushType.A17,type = 3)
    @PutMapping("/{taskId}/recyclebin")
    public JSONObject moveToRecycleBin(@PathVariable(value = "taskId")String taskId){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskDel(1);
            task.setUpdateTime(System.currentTimeMillis());
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","移入成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data",taskId);
            object.put("id",taskId);
            object.put("publicType", Constants.TASK);
        }catch(Exception e){
            log.error("系统异常,移入回收站失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 任务隐私模式
     * @param taskId 任务id
     * @return
     */
    @Log(PushType.A18)
    @Push(value = PushType.A18,type = 1)
    @PutMapping("/{taskId}/privacy")
    public JSONObject taskPrivacy(@PathVariable(value = "taskId")String taskId,@RequestParam Integer privacy){
        JSONObject object = new JSONObject();
        try{
            String projectId = getTaskProjectId(taskId);
            Task task = new Task();
            task.setTaskId(taskId);
            task.setPrivacyPattern(privacy);
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","修改成功");
            object.put("msgId",projectId);
            object.put("data",projectId);
            object.put("id",taskId);
        }catch(Exception e){
            log.error("系统异常,隐私模式更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 子任务转父任务
     * @param taskId 任务id
     * @return
     */
    @Log(PushType.A19)
    @Push(value = PushType.A19)
    @PutMapping("/{taskId}/to_father")
    public JSONObject taskToParent(@PathVariable(value = "taskId")String taskId){
        JSONObject object = new JSONObject();
        try{
            Task task = taskService.getById(taskId);
            Task parentTask = taskService.getOne(new QueryWrapper<Task>().eq("parent_id", task.getTaskId()));
            task.setTaskGroupId(parentTask.getTaskGroupId());
            task.setTaskMenuId(parentTask.getTaskMenuId());
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","移入成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("task",taskService.getById(taskId)));
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 对此任务点赞
     * @param taskId 任务id
     * @return
     */
    @Log(PushType.A20)
    @Push(value = PushType.A20,type = 1)
    @PutMapping("/{taskId}/fabulous")
    public JSONObject taskFabulous(@PathVariable(value = "taskId")String taskId){
        JSONObject object = new JSONObject();
        try{
            Fabulous fabulous = new Fabulous();
            fabulous.setMemberId(ShiroAuthenticationManager.getUserId());
            fabulous.setPublicId(taskId);
            fabulousService.save(fabulous);
            Task task = taskService.getById(taskId);
            int count = fabulousService.count(new QueryWrapper<Fabulous>().eq("public_id", taskId));
            task.setFabulousCount(count);
            taskService.updateById(task);
            object.put("result",1);
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data",new JSONObject().fluentPut("task",taskService.getById(taskId)));
            object.put("id",taskId);
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }


    /**
     * 上传文件
     *
     * @param projectId 项目id
     */
    @Log(PushType.A21)
    @Push(value = PushType.A21,type = 1)
    @PostMapping("/{taskId}/upload")
    public JSONObject uploadFile(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "files") String files,
            @PathVariable(value = "taskId") String taskId

    ) {
        JSONObject object = new JSONObject();
        try {
            fileService.saveFileBatch(projectId,files,null,taskId);
            object.put("result", 1);
            object.put("msgId",taskId);
            object.put("data",fileService.list(new QueryWrapper<File>().eq("public_id",taskId)));
            object.put("id",taskId);
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 上传模型
     * @param taskId 任务id
     * @param projectId 项目id
     * @param fileCommon 缩略图
     * @param fileModel 模型
     * @param filename 自定义文件名称
     * @return
     */
    @Log(PushType.A22)
    @Push(value = PushType.A22,type = 1)
    @PostMapping("/{taskId}/model")
    public JSONObject uploadModel(
            @PathVariable(value = "taskId") String taskId,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "fileCommon") String fileCommon,
            @RequestParam(value = "fileModel") String fileModel,
            @RequestParam(value = "filename") String filename
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.saveModel(fileModel,fileCommon,taskId,filename,null);
            jsonObject.put("result",1);
            jsonObject.put("msgId",taskId);
            jsonObject.put("data",fileService.getOne(new QueryWrapper<File>().eq("public_id",taskId)));
            jsonObject.put("id",taskId);
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取任务的子任务(用于绑定信息处)
     * @param taskId 任务id
     * @return 信息
     */
    @GetMapping("/{taskId}/bind/child")
    public JSONObject getBindChild(@PathVariable String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",taskService.getBindChild(taskId));
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,获取子任务失败!",e);
        }
    }

    /**
     * 获取任务的看板数据
     * @param projectId 项目id
     * @return 任务集合
     */
    @GetMapping("/{projectId}/panel")
    public JSONObject getTaskPanel(@PathVariable String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", taskService.getTaskPanel(projectId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常,数据获取失败!",e);
        }
    }

    /**
     * 更新任务的开始/结束时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 结果
     */
    @PutMapping("{taskId}/start_end_time")
    public JSONObject updateStartEndTime(@PathVariable String taskId,@RequestParam(required = false) Long startTime, @RequestParam(required = false) Long endTime){
        JSONObject jsonObject = new JSONObject();
        try {
            if(startTime == null && endTime == null){
                jsonObject.put("msg","开始和结束时间必须给定一个!");
                jsonObject.put("result",0);
                return jsonObject;
            }
            Task task = new Task();
            task.setTaskId(taskId);
            task.setStartTime(startTime);
            task.setEndTime(endTime);
            if(taskService.updateById(task)){
                jsonObject.put("result",1);
            }
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,更新失败!",e);
        }
    }

    /**
     * 根据任务名称模糊搜索任务名称
     * @param name 任务名称
     * @param projectId 项目id
     * @return 任务信息列表
     */
    @GetMapping("/{name}/like")
    public JSONObject likeName(@PathVariable String name, @RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", taskService.likeTaskName(name,projectId));
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,信息获取失败!",e);
        }
    }

    /**
     * 获取任务的项目id
     * @param taskId 任务id
     * @return 项目id
     */
    private String getTaskProjectId(String taskId){
        Task one = taskService.getOne(new QueryWrapper<Task>().select("project_id").eq("task_id", taskId));
        if(one != null && StringUtils.isNotEmpty(one.getProjectId())){
            return one.getProjectId();
        }
        return "";
    }

    private String getTaskName(String taskId){
        return taskService.getTaskNameById(taskId);
    }





    /**
     * 将字符串复制到剪切板。
     * @param url  端口/任务 日程  文件  分享 / id
     */
    @PostMapping("/setSysClip")
    public  JSONObject setSysClipboardText(@RequestParam String url) {
        JSONObject jsonObject = new JSONObject();
        try {
            //System.setProperty("java.awt.headless", "true");
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(url);
            clip.setContents(tText, null);

            jsonObject.put("result",1);
            jsonObject.put("msg","复制到剪贴板成功!");
            return jsonObject;

        } catch (Exception e){
            throw new AjaxException("系统异常,更新失败!",e);
        }
    }



}
