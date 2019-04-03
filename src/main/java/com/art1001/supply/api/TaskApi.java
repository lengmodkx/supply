package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Log;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.entity.fabulous.Fabulous;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskRemindRule;
import com.art1001.supply.enums.MediaTypes;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.BaseException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.fabulous.FabulousService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.task.TaskRemindRuleService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.AjAttribute;
import org.quartz.SchedulerException;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
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
public class TaskApi extends BaseController {

    @Resource
    private TaskService taskService;

    @Resource
    private FabulousService fabulousService;

    @Resource
    private UserService userService;

    @Resource
    private RelationService relationService;

    @Resource
    private TaskRemindRuleService taskRemindRuleService;

    @Resource
    private FileService fileService;

    /**
     * 任务页面初始化
     * @return object
     */
    @GetMapping("/{taskId}")
    public JSONObject getTask(@PathVariable(value = "taskId") String taskId){
        try {
            return success(taskService.taskInfoShow(taskId));
        } catch (Exception e){
            e.printStackTrace();
            throw new AjaxException("系统异常,获取任务信息失败!",e);
        }
    }

    /**
     * 创建任务
     * @return object
     */
    @Log(PushType.A1)
    @Push(value = PushType.A1,type = 1)
    @PostMapping
    public JSONObject addTask(Task task){
        try {
            taskService.saveTask(task);

            return success(task.getProjectId(),relationService.initMainPage(task.getTaskGroupId()),task.getTaskId(),task.getTaskName(),task.getProjectId());
        } catch (BaseException e){
            return error(e.getMessage());
        } catch (Exception e){
            throw new AjaxException("系统异常任务创建失败!",e);
        }
    }

    /**
     * 删除任务
     * @param taskId 任务id
     * @return object
     */
    @Log(PushType.A2)
    @Push(value = PushType.A2,type = 1)
    @DeleteMapping("/{taskId}")
    public Object deleteTask(@PathVariable(value = "taskId")String taskId){
        try{
            taskService.removeById(taskId);
            return success(taskId,taskId,taskService.findTaskNameById(taskId));
        }catch(Exception e){
            throw new AjaxException("系统异常,删除失败",e);
        }
    }

    /**
     * 完成任务
     * @param taskId 任务id
     * @return object
     */
    @Push(value = PushType.A3,type = 1)
    @PutMapping("/{taskId}/finish")
    public JSONObject finishTask(@PathVariable(value = "taskId")String taskId){
        JSONObject object = new JSONObject();
        try{
            taskService.completeTask(taskId);
            Task t = new Task();
            t.setTaskId(taskId);
            t.setTaskStatus("完成");
            object.put("data",new JSONObject().fluentPut("task",t));
            object.put("status",1);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("id",taskId);
        }catch(Exception e){
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
    @Log(PushType.A4)
    @Push(value = PushType.A4,type = 1)
    @PutMapping("/{taskId}/unFinish")
    public JSONObject unFinishTask(@PathVariable(value = "taskId")String taskId){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskStatus("未完成");
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data",new JSONObject().fluentPut("task",task));
            object.put("id",taskId);
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
            object.put("data",taskIds.split(","));
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
    @Push(value = PushType.A5,type = 1)
    @PutMapping("/{taskId}/name")
    public JSONObject upadteTaskName(@PathVariable(value = "taskId")String taskId,
                                     @RequestParam(value = "taskName")String taskName){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskName(taskName);
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data",new JSONObject().fluentPut("task",task));
            object.put("id",taskId);
            object.put("name",taskName);
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
    @Log(PushType.A6)
    @Push(value = PushType.A6,type = 1)
    @PutMapping("/{taskId}/executor")
    public JSONObject upadteTaskExecutor(@PathVariable(value = "taskId")String taskId,
                                         @RequestParam(value = "executor")String userId){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setExecutor(userId);
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("executor",userService.getById(userId)));
            object.put("id",taskId);
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
    @Log(PushType.A7)
    @Push(value = PushType.A7,type = 1)
    @PutMapping("/{taskId}/starttime")
    public JSONObject upadteTaskStartTime(@PathVariable(value = "taskId")String taskId,
                                          @RequestParam(value = "startTime")Long startTime){
        JSONObject object = new JSONObject();
        try{
            taskService.updateStartTime(taskId,startTime);
            Task task = new Task();
            task.setTaskId(taskId);
            task.setStartTime(startTime);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data",new JSONObject().fluentPut("task",task));
            object.put("id",taskId);
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
    @Log(PushType.A8)
    @Push(value = PushType.A8,type = 1)
    @PutMapping("/{taskId}/endtime")
    public JSONObject upadteTaskEndTime(@PathVariable(value = "taskId")String taskId,
                                        @RequestParam(value = "endTime")Long endTime){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setEndTime(endTime);
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data",new JSONObject().fluentPut("task",task));
            object.put("id",taskId);
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
    @Log(PushType.A9)
    @Push(value = PushType.A9,type = 1)
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
            object.put("data",new JSONObject().fluentPut("task",task));
            object.put("id",taskId);
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
            task.setRemarks(remarks);
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("remarks",remarks));
            object.put("id",taskId);
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
    @Log(PushType.A12)
    @Push(value = PushType.A12,type = 1)
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
            object.put("msgId",getTaskProjectId(taskId));
            object.put("data",new JSONObject().fluentPut("task",task));
            object.put("id",taskId);
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
    @Push(value = PushType.A13,type = 1)
    @PostMapping("/{taskId}/addchild")
    public JSONObject addChildTask(@PathVariable(value = "taskId")String taskId,
                                   @RequestParam(value = "taskName")String taskName,
                                   @RequestParam(value = "executor",required = false)String executor,
                                   @RequestParam(value = "startTime",required = false)String startTime){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setParentId(taskId);
            task.setTaskName(taskName);
            if(StringUtils.isNotEmpty(executor)){
                task.setExecutor(executor);
            }
            if(StringUtils.isNotEmpty(startTime)){
                task.setStartTime(DateUtils.strToLong(startTime));
            }
            taskService.saveTask(task);
            object.put("result",1);
            object.put("msg","创建成功!");
            object.put("data",new JSONObject().fluentPut("task",task));
            String taskProjectId = this.getTaskProjectId(taskId);
            if(StringUtils.isNotEmpty(taskProjectId)){
                object.put("msgId",taskProjectId);
            } else{
                object.put("msgId",taskService.findChildTaskProject(taskId));
            }
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
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data",new JSONObject().fluentPut("members",userService.findManyUserById(taskUids)));
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
            object.put("data",new JSONObject().fluentPut("task",taskService.copyTask(taskId,projectId,groupId,menuId)));
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
                maps.put(projectId,new JSONObject().fluentPut("task",taskService.findTaskByTaskId(taskId)));
            } else{
                maps.put(projectId,new JSONObject().fluentPut("task",taskService.findTaskByTaskId(taskId)));
                maps.put(taskProjectId,new JSONObject().fluentPut("task",task));
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
    @Log(PushType.A17)
    @Push(value = PushType.A17,type = 1)
    @PutMapping("/{taskId}/recyclebin")
    public JSONObject moveToRecycleBin(@PathVariable(value = "taskId")String taskId){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskDel(1);
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","移入成功");
            object.put("msgId",this.getTaskProjectId(taskId));
            object.put("data",new JSONObject().fluentPut("task",taskService.getById(taskId)));
            object.put("id",taskId);
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
            Task task = new Task();
            task.setTaskId(taskId);
            task.setPrivacyPattern(privacy);
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","移入成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("task",taskService.getById(taskId)));
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
     * 获取任务的项目id
     * @param taskId 任务id
     * @return 项目id
     */
    private String getTaskProjectId(String taskId){
        return taskService.getOne(new QueryWrapper<Task>().select("project_id").eq("task_id",taskId)).getProjectId();
    }



}
