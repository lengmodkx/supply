package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.*;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.automation.constans.AutomationRuleConstans;
import com.art1001.supply.entity.fabulous.Fabulous;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskRemindRule;
import com.art1001.supply.entity.task.vo.ExecutorVo;
import com.art1001.supply.entity.task.vo.TaskDynamicVO;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserNews;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.fabulous.FabulousService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
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
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.quartz.SchedulerException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.rmi.activation.ActivationGroup_Stub;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 任务增删改查，复制，移动
 *
 * @author 汪亚锋
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@Validated
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
    private WeChatAppMessageService weChatAppMessageService;

    @Resource
    private TaskRemindRuleService taskRemindRuleService;

    @Resource
    private FileService fileService;

    @Resource
    private UserNewsService userNewsService;

    @Resource
    private WeChatAppMessageTemplateDataBuildService updateTaskJoinInfo;

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectMemberService projectMemberService;


    private static final String ZERO = "0";

    /**
     * 任务页面初始化
     *
     * @return object
     */
    @GetMapping("/{taskId}")
    public JSONObject getTask(@PathVariable(value = "taskId") String taskId) {
        JSONObject jsonObject = new JSONObject();
        try {
            Task task = taskService.taskInfoShow(taskId);
            jsonObject.put("data", task);
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常,获取任务信息失败!", e);
        }
    }

    /**
     * 创建任务
     *
     * @return object
     */
    @AutomationRule(value = "#task.taskId", trigger = AutomationRuleConstans.ADD_TASK)
    @Push(value = PushType.A1, name = PushName.TASK,type = 1)
    @PostMapping
    public JSONObject addTask(Task task) {
        try {
            taskService.saveTask(task);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",task.getTaskId());
            jsonObject.put("msgId", task.getProjectId());
            jsonObject.put("name",task.getTaskName());
            jsonObject.put("data", task.getProjectId());
            jsonObject.put("result", 1);
            //jsonObject.put("task", taskService.getById(task.getTaskId()));

//            //推送微信小程序消息给多个用户
//            WeChatAppMessageTemplate weChatAppMessageTemplate = WeChatAppMessageTemplateBuild.createTask();
//            weChatAppMessageService.pushToMultipleUsers(
//                    Arrays.asList(task.getTaskUIds().split(",")),
//                    weChatAppMessageTemplate,
//                    updateTaskJoinInfo
//            );
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常任务创建失败!", e);
        }
    }

    /**
     * 获取从任务上传的附件信息
     *
     * @param taskId 任务id
     * @return
     */
    @GetMapping("{taskId}/files")
    public JSONObject getFiles(@PathVariable String taskId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", fileService.list(new QueryWrapper<File>().eq("public_id", taskId).eq("public_lable", 1).orderByDesc("create_time")));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,信息获取失败!", e);
        }
    }

    /**
     * 删除任务
     *
     * @param taskId 任务id
     * @return object
     */
    @Push(value = PushType.A2, name = PushName.TASK,type = 1)
    @DeleteMapping("/{taskId}")
    public JSONObject deleteTask(@PathVariable(value = "taskId") String taskId) {
        try {
            Task task = taskService.getOne(new QueryWrapper<Task>().eq("task_id", taskId));
            taskService.removeById(taskId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",taskId);
            jsonObject.put("msgId", task.getProjectId());
            jsonObject.put("name",task.getTaskName());
            jsonObject.put("data", task.getProjectId());
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,删除失败", e);
        }
    }
    /**
     * 完成任务
     *
     * @param taskId 任务id
     * @return object
     */
    @AutomationRule(value = "#taskId", trigger = "completed", objectValue = "#label")
    @Push(value = PushType.A3, name =PushName.TASK,type = 3)
    @PutMapping("/{taskId}/finish")
    public JSONObject finishTask(@PathVariable(value = "taskId") String taskId,
                                 @RequestParam(value = "projectId") String projectId,
                                 @RequestParam(required = false, defaultValue = "0") Integer label) {
        JSONObject object = new JSONObject();
        try {
            Task task = taskService.getById(taskId);
            //这里判断是否有子任务未完成
            Task one = taskService.getOne(new QueryWrapper<Task>().eq("parent_id", task.getTaskId()).eq("task_status", false));
            if(one!=null){
                object.put("msg", "有子任务未完成!");
                object.put("result", 0);
                return object;
            }
            //完成任务逻辑
            taskService.completeTask(task);

            object.put("result", 1);
            //判断点击的任务是否在父任务页面
            if (label == 1) {
                object.put("data", new JSONObject().fluentPut("taskId", task.getParentId()).fluentPut("projectId", projectId));
            } else {
                object.put("data", new JSONObject().fluentPut("taskId", taskId).fluentPut("projectId", projectId));
            }
            object.put("msgId", projectId);
            object.put("msg", "更新成功");
            object.put("publicType", Constants.TASK);
            object.put("id", taskId);
        } catch (ServiceException e) {
            throw new AjaxException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("系统异常,状态更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 重做任务
     *
     * @param taskId 任务id
     * @return JSONObject
     */
    @AutomationRule(value = "#taskId", trigger = "redone", objectValue = "#label")
    @Push(value = PushType.A4, name =PushName.TASK,type = 3)
    @PutMapping("/{taskId}/unFinish")
    public JSONObject unFinishTask(@PathVariable(value = "taskId") String taskId,
                                   @RequestParam(value = "projectId") String projectId,
                                   @RequestParam(required = false, defaultValue = "0") Integer label) {
        JSONObject object = new JSONObject();
        try {
            //这里判断父任务是否已经完成
            Task task = taskService.getById(taskId);
            if (!task.getParentId().equals("0")) {
                Task pTask = taskService.getOne(new QueryWrapper<Task>().eq("task_id", task.getParentId()));
                if (pTask.getTaskStatus()) {
                    object.put("result", 0);
                    object.put("msg", "父任务已经完成不能重做子任务!");
                    return object;
                }
            }

            task.setTaskStatus(false);
            taskService.updateById(task);
            Task pTask = new Task();
            pTask.setTaskId(task.getParentId());
            pTask.setUpdateTime(System.currentTimeMillis());
            pTask.setSubIsAllComplete(false);
            taskService.updateById(pTask);
            object.put("msgId", projectId);
            object.put("result", 1);
            object.put("msg", "更新成功");
            if (label == 1) {
                object.put("data", new JSONObject().fluentPut("taskId", task.getParentId()).fluentPut("projectId", object.getString("msgId")));
            } else {
                object.put("data", new JSONObject().fluentPut("taskId", taskId).fluentPut("projectId", object.getString("msgId")));
            }
            object.put("id", taskId);

            object.put("publicType", Constants.TASK);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 排列菜单下的任务
     *
     * @param projectId 项目id
     * @param taskIds   任务ids
     * @return
     */
    @AutomationRule(value = "#taskId", trigger = AutomationRuleConstans.DRAG_TASK)
    @Push(value = PushType.A27, name =PushName.TASK, type = 1)
    @PutMapping("/order")
    public JSONObject order(@RequestParam(value = "projectId") String projectId,
                            @RequestParam(value = "taskId", required = false) String taskId,
                            @RequestParam(value = "taskIds") String taskIds,
                            @RequestParam(value = "newMenu", required = false) String newMenu) {
        JSONObject object = new JSONObject();
        try {
            taskService.orderTask(taskIds, taskId, newMenu);
            object.put("result", 1);
            object.put("msg", "更新成功");
            object.put("msgId", projectId);
            object.put("data", projectId);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务名称
     *
     * @param taskId   任务id
     * @param taskName 任务名称
     * @return JSONObject
     */
    @Push(value = PushType.A5, name =PushName.TASK,type = 3)
    @PutMapping("/{taskId}/name")
    public JSONObject upadteTaskName(@PathVariable(value = "taskId") String taskId,
                                     @RequestParam(value = "taskName") String taskName) {
        JSONObject object = new JSONObject();
        try {
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskName(taskName);
            taskService.updateById(task);
            String[] taskJoinAndExecutorId = taskService.getTaskJoinAndExecutorId(taskId);
            if (taskJoinAndExecutorId != null) {
                for (String s : taskJoinAndExecutorId) {
                    UserNews userNews = new UserNews();
                    userNews.setNewsToUserId(s);
                    userNews.setNewsPublicId(taskId);
                    userNews.setNewsName(taskName);
                    userNewsService.updateUserNews(userNews);
                }
            }

            object.put("result", 1);
            object.put("msg", "更新成功");
            object.put("msgId", this.getTaskProjectId(taskId));
            object.put("data", taskId);
            object.put("id", taskId);
            object.put("name", taskName);
            object.put("publicType", Constants.TASK);
        } catch (Exception e) {
            log.error("系统异常,任务名称更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务执行者
     *
     * @param taskId 任务id
     * @param userId 执行者id
     * @return JSONObject
     */
    @AutomationRule(value = "#taskId", trigger = "settingExecutors", objectValue = "#userId")
    @Push(value = PushType.A6, name =PushName.TASK,type = 3)
    @PutMapping("/{taskId}/executor")
    public JSONObject upadteTaskExecutor(@PathVariable(value = "taskId") String taskId,
                                         @RequestParam(value = "executor") String userId) {
        JSONObject object = new JSONObject();
        try {
            Task task = new Task();
            task.setTaskId(taskId);
            if (StringUtils.isEmpty(userId)) {
                task.setExecutor("0");
            } else {
                task.setExecutor(userId);
            }
            taskService.updateById(task);
            Task one = taskService.getOne(new QueryWrapper<Task>().lambda().select(Task::getParentId, Task::getTaskId).eq(Task::getTaskId, taskId));
            if (one.getParentId().equals("0")) {
                object.put("msgId", this.getTaskProjectId(taskId));
                object.put("data", taskId);
            } else {
                object.put("msgId", taskService.findChildTaskProject(taskId));
                object.put("data", one.getParentId());
            }
            object.put("result", 1);
            object.put("msg", "更新成功");
            object.put("id", taskId);
            object.put("publicType", Constants.TASK);
        } catch (Exception e) {
            log.error("系统异常,执行者更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务开始时间
     *
     * @param taskId    任务id
     * @param startTime 任务开始时间
     * @return JSONObject
     */
    @Push(value = PushType.A7, name =PushName.TASK,type = 3)
    @PutMapping(value = "/{taskId}/starttime")
    public JSONObject upadteTaskStartTime(@PathVariable(value = "taskId") String taskId,
                                          @RequestParam(value = "startTime") Long startTime) {
        JSONObject object = new JSONObject();
        try {
            taskService.updateStartTime(taskId, startTime);
            Task task = new Task();
            task.setTaskId(taskId);
            task.setUpdateTime(System.currentTimeMillis());
            if (0 == startTime) {
                task.setStartTime(0L);
            } else {
                task.setStartTime(startTime);
            }
            object.put("result", 1);
            object.put("msg", "更新成功");
            object.put("msgId", this.getTaskProjectId(taskId));
            object.put("data", taskId);
            object.put("id", taskId);
            object.put("publicType", Constants.TASK);
        } catch (Exception e) {
            log.error("系统异常,开始时间更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务结束时间
     *
     * @param taskId  任务id
     * @param endTime 任务结束时间
     * @return JSONObject
     */
    @AutomationRule(value = "#taskId", trigger = "endTime", objectValue = "#endTime")
    @Push(value = PushType.A8, name =PushName.TASK,type = 3)
    @PutMapping("/{taskId}/endtime")
    public JSONObject upadteTaskEndTime(@PathVariable(value = "taskId") String taskId,
                                        @RequestParam(value = "endTime") Long endTime) {
        JSONObject object = new JSONObject();
        try {
            Task task = new Task();
            task.setTaskId(taskId);
            task.setUpdateTime(System.currentTimeMillis());
            if (0 == endTime) {
                task.setEndTime(0L);
            } else {
                task.setEndTime(endTime);
            }
            taskService.updateById(task);
            object.put("result", 1);
            object.put("msg", "更新成功");
            object.put("msgId", this.getTaskProjectId(taskId));
            object.put("data", taskId);
            object.put("id", taskId);
            object.put("publicType", Constants.TASK);
        } catch (Exception e) {
            log.error("系统异常,结束时间更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务重复性
     *
     * @param taskId 任务id
     * @param repeat 任务结束时间
     * @return JSONObject
     */
    @AutomationRule(value = "#taskId", trigger = "repeat", objectValue = "#repeat")
    @Push(value = PushType.A9, name =PushName.TASK,type = 3)
    @PutMapping("/{taskId}/repeat")
    public JSONObject upadteTaskRepeat(@PathVariable(value = "taskId") String taskId,
                                       @RequestParam(value = "repeat") String repeat) {
        JSONObject object = new JSONObject();
        try {
            Task task = new Task();
            task.setTaskId(taskId);
            task.setRepeat(repeat);
            taskService.updateById(task);
            object.put("result", 1);
            object.put("msg", "更新成功");
            object.put("msgId", this.getTaskProjectId(taskId));
            object.put("data", taskId);
            object.put("id", taskId);
            object.put("publicType", Constants.TASK);
        } catch (Exception e) {
            log.error("系统异常,重复性更新失败:", e);
            throw new AjaxException("系统异常,重复性更新失败:", e);
        }
        return object;
    }

    /**
     * 更新任务提醒规则
     *
     * @param taskId     任务id
     * @param remindType 任务的提醒规则类型
     * @param num        时间数量
     * @param timeType   时间类型
     * @param customTime 自定义时间
     * @return
     */
    @Push(value = PushType.A24, name =PushName.TASK,type = 1)
    @PutMapping("/{taskId}/remind")
    public JSONObject updateTaskRemind(@PathVariable(value = "taskId") String taskId,
                                       @RequestParam(value = "ruleId") String ruleId,
                                       @RequestParam(value = "remindType", required = false) String remindType,
                                       @RequestParam(value = "num", required = false) Integer num,
                                       @RequestParam(value = "timeType", required = false) String timeType,
                                       @RequestParam(value = "customTime", required = false) String customTime) {
        JSONObject object = new JSONObject();
        try {
            //更新任务提醒
            TaskRemindRule taskRemindRule = new TaskRemindRule();
            taskRemindRule.setTaskId(taskId);
            taskRemindRule.setId(ruleId);
            taskRemindRule.setNum(num);
            taskRemindRule.setRemindType(remindType);
            taskRemindRule.setTimeType(timeType);
            taskRemindRule.setCustomTime(customTime);
            taskService.updateTaskRemind(taskRemindRule);
            object.put("msg", "时间规则更新成功!");
            object.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常,提醒模式更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移除任务提醒规则
     *
     * @param id 规则的id
     * @return
     */
    @Push(value = PushType.A23,name =PushName.TASK, type = 1)
    @DeleteMapping("/{id}/remind")
    public JSONObject removeRemind(@PathVariable("id") String id) {
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.removeRemind(id);
            jsonObject.put("msg", "移除成功!");
            jsonObject.put("result", 1);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            throw new AjaxException(e);
        } catch (Exception e) {
            log.error("系统异常,提醒规则移除失败!", e);
            throw new AjaxException("系统异常,提醒规则移除失败!", e);
        }
        return jsonObject;
    }

    /**
     * 新增任务提醒规则
     *
     * @param taskId     任务id
     * @param remindType 任务的提醒规则类型
     * @param num        时间数量
     * @param timeType   时间类型
     * @param customTime 自定义时间
     * @return
     */
    @Push(value = PushType.A10, name =PushName.TASK,type = 1)
    @PostMapping("/{taskId}/remind")
    public JSONObject addTaskRemind(@PathVariable(value = "taskId") String taskId,
                                    @RequestParam(value = "remindType", required = false) String remindType,
                                    @RequestParam(value = "num", required = false) Integer num,
                                    @RequestParam(value = "timeType", required = false) String timeType,
                                    @RequestParam(value = "customTime", required = false) String customTime,
                                    @RequestParam(value = "users") String users) {
        JSONObject object = new JSONObject();
        try {
            TaskRemindRule t = new TaskRemindRule();
            t.setTaskId(taskId);
            t.setRemindType(remindType);
            t.setTimeType(timeType);
            t.setCustomTime(customTime);
            t.setNum(num);
            taskService.addTaskRemind(t, users);
            object.put("result", 1);
            object.put("msg", "成功!");
        } catch (ServiceException e) {
            throw new AjaxException(e);
        } catch (Exception e) {
            log.error("系统异常,新增提醒规则失败!", e);
            throw new AjaxException("系统异常,新增提醒规则失败!", e);
        }
        return object;
    }

    /**
     * 更新任务提醒的成员信息
     *
     * @param taskId 任务id
     * @param users  成员信息
     * @return
     */
    @PutMapping("/{taskId}/remind/user")
    public JSONObject updateRemindUsers(@PathVariable String taskId, @RequestParam("users") String users) {
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.updateRemindUsers(taskId, users);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "更新成功!");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取所有任务提醒信息
     *
     * @return
     */
    @GetMapping("/{taskId}/remind")
    public JSONObject getRemind(@PathVariable String taskId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", taskRemindRuleService.list(new QueryWrapper<TaskRemindRule>().eq("task_id", taskId)));
            jsonObject.put("result", 1);
            jsonObject.put("msg", "获取成功!");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新任务备注
     *
     * @param taskId  任务id
     * @param remarks 任务备注信息
     * @return JSONObject
     */
    @Push(value = PushType.A11, name =PushName.TASK,type = 1)
    @PutMapping("/{taskId}/remarks")
    public JSONObject upadteTaskRemarks(@PathVariable(value = "taskId") String taskId,
                                        @RequestParam(value = "remarks") String remarks) {
        JSONObject object = new JSONObject();
        try {
            Task task = new Task();
            task.setTaskId(taskId);
            if (remarks.equals("<p><br></p>")) {
                task.setRemarks("");
            } else {
                task.setRemarks(remarks);
            }
            taskService.updateById(task);
            object.put("result", 1);
            object.put("remarks", remarks);
            object.put("msgId", getTaskProjectId(taskId));
            object.put("data", taskId);
            //object.put("data",new JSONObject().fluentPut("type","任务").fluentPut("id", taskId));
            object.put("id", taskId);
            object.put("msg", "更新成功");
        } catch (Exception e) {
            log.error("系统异常,备注更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务优先级
     *
     * @param taskId   任务id
     * @param priority 任务优先级
     * @return JSONObject
     */
    @AutomationRule(value = "#taskId", trigger = "priority", objectValue = "#priority")
    @Push(value = PushType.A12, name =PushName.TASK,type = 3)
    @PutMapping("/{taskId}/priority")
    public JSONObject upadteTaskPriority(@PathVariable(value = "taskId") String taskId,
                                         @RequestParam(value = "priority") String priority) {
        JSONObject object = new JSONObject();
        try {
            Task task = new Task();
            task.setTaskId(taskId);
            task.setPriority(priority);
            taskService.updateById(task);
            object.put("result", 1);
            object.put("msgId", this.getTaskProjectId(taskId));
            object.put("data", taskId);
            object.put("id", taskId);
            object.put("publicType", Constants.TASK);
        } catch (Exception e) {
            log.error("系统异常,优先级更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 新增子任务
     *
     * @param taskId    父任务id
     * @param taskName  子任务名称
     * @param executor  子任务的执行者
     * @param startTime 子任务的结束时间
     * @return JSONObject
     */
    @Push(value = PushType.A13, name =PushName.TASK,type = 3)
    @PostMapping("/{taskId}/addchild")
    public JSONObject addChildTask(@PathVariable(value = "taskId") String taskId,
                                   @RequestParam(value = "taskName") String taskName,
                                   @RequestParam(value = "executor", required = false) String executor,
                                   @RequestParam(value = "startTime", required = false) String startTime) {
        JSONObject object = new JSONObject();
        try {
            if (StringUtils.isEmpty(taskName)) {
                object.put("result", 0);
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
            if (StringUtils.isNotEmpty(taskGroupId.getTaskGroupId())) {
                task.setTaskGroupId(taskGroupId.getTaskGroupId());
            }

            if (StringUtils.isNotEmpty(executor)) {
                task.setExecutor(executor);
            }
            if (StringUtils.isNotEmpty(startTime)) {
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
            object.put("result", 1);
            object.put("msg", "创建成功!");
            object.put("data", new JSONObject().fluentPut("taskId", taskId).fluentPut("projectId", taskService.findChildTaskProject(taskId)));
            object.put("msgId", taskService.findChildTaskProject(taskId));
            object.put("id", taskId);
        } catch (Exception e) {
            log.error("系统异常,子任务添加失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务参与者
     *
     * @param taskId   任务id
     * @param taskUids 参与者id
     * @return
     */
    @SuppressWarnings("unchecked")
    @Push(value = PushType.A14, name =PushName.TASK,type = 1)
    @PutMapping("/{taskId}/members")
    public JSONObject addTaskUids(@PathVariable(value = "taskId") String taskId,
                                  @RequestParam(value = "taskUids") String taskUids) {
        JSONObject object = new JSONObject();
        try {
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskUIds(taskUids);
            taskService.updateById(task);

            String[] taskIdList = taskUids.split(",");
            UserEntity byId = userService.findById(ShiroAuthenticationManager.getUserId());
            userNewsService.saveUserNews(taskIdList, taskId, Constants.TASK, byId.getUserName() + PushType.A14.getName(), null);

//            //推送微信小程序消息给多个用户
//            WeChatAppMessageTemplate weChatAppMessageTemplate = WeChatAppMessageTemplateBuild.updateTaskJoin();
//            weChatAppMessageService.pushToMultipleUsers(
//                    Arrays.asList(taskIdList),
//                    weChatAppMessageTemplate,
//                    updateTaskJoinInfo
//            );


            object.put("result", 1);
            object.put("msg", "更新成功");
            object.put("msgId", this.getTaskProjectId(taskId));
            object.put("data", taskId);
            object.put("id", taskId);
        } catch (Exception e) {
            log.error("系统异常,任务参与者更新:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 复制任务
     *
     * @param taskId    任务id
     * @param projectId 项目id
     * @param groupId   组id
     * @param menuId    菜单id
     * @return 是否复制成功
     */
    @Push(value = PushType.A15, name =PushName.TASK,type = 1)
    @PostMapping("/{taskId}/copy")
    public JSONObject copyTask(@PathVariable(value = "taskId") String taskId,
                               @RequestParam(value = "projectId") String projectId,
                               @RequestParam(value = "groupId") String groupId,
                               @RequestParam(value = "menuId") String menuId) {
        JSONObject object = new JSONObject();
        try {
            taskService.copyTask(taskId, projectId, groupId, menuId);
            object.put("data", projectId);
            object.put("result", 1);
            object.put("msg", "复制成功");
            object.put("msgId", projectId);
            object.put("id", taskId);
        } catch (Exception e) {
            log.error("系统异常,任务复制失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移动任务
     *
     * @param taskId    任务id
     * @param projectId 项目id
     * @param groupId   组id
     * @param menuId    菜单id
     * @return
     */
    @AutomationRule(value = "#taskId", trigger = AutomationRuleConstans.MOVE_TASK)
    @Push(value = PushType.A16, name =PushName.TASK,type = 2)
    @PutMapping("/{taskId}/move")
    public JSONObject moveTask(@PathVariable(value = "taskId") String taskId,
                               @RequestParam(value = "projectId") String projectId,
                               @RequestParam(value = "groupId") String groupId,
                               @RequestParam(value = "menuId") String menuId) {
        JSONObject object = new JSONObject();
        try {
            Task task = new Task();
            task.setTaskId(taskId);
            //获取到任务移动前的项目id
            String taskProjectId = this.getTaskProjectId(taskId);
            taskService.mobileTask(taskId, projectId, groupId, menuId);
            object.put("result", 1);
            object.put("msg", "移动成功");
            Map<String, Object> maps = new HashMap<String, Object>(2);
            if (projectId.equals(taskProjectId)) {
                maps.put(projectId, projectId);
            } else {
                maps.put(projectId, projectId);
                maps.put(taskProjectId, taskProjectId);
            }
            object.put("data", maps);
            object.put("id", taskId);
        } catch (Exception e) {
            log.error("系统异常,任务移动失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移到回收站
     *
     * @param taskId 任务id
     * @returnF
     */
    @Push(value = PushType.A17, name =PushName.TASK,type = 3)
    @PutMapping("/{taskId}/recyclebin")
    public JSONObject moveToRecycleBin(@PathVariable(value = "taskId") String taskId) {
        JSONObject object = new JSONObject();
        try {
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskDel(1);
            task.setUpdateTime(System.currentTimeMillis());
            taskService.updateById(task);
            object.put("result", 1);
            object.put("msg", "移入成功");
            object.put("msgId", this.getTaskProjectId(taskId));
            object.put("data", taskId);
            object.put("id", taskId);
            object.put("publicType", Constants.TASK);
        } catch (Exception e) {
            log.error("系统异常,移入回收站失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 任务隐私模式
     *
     * @param taskId 任务id
     * @return
     */
    @Push(value = PushType.A18, name =PushName.TASK,type = 1)
    @PutMapping("/{taskId}/privacy")
    public JSONObject taskPrivacy(@PathVariable(value = "taskId") String taskId, @RequestParam Integer privacy) {
        JSONObject object = new JSONObject();
        try {
            String projectId = getTaskProjectId(taskId);
            Task task = new Task();
            task.setTaskId(taskId);
            task.setPrivacyPattern(privacy);
            taskService.updateById(task);
            object.put("result", 1);
            object.put("msg", "修改成功");
            object.put("msgId", projectId);
            object.put("data", projectId);
            object.put("id", taskId);
        } catch (Exception e) {
            log.error("系统异常,隐私模式更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 子任务转父任务
     *
     * @param taskId 任务id
     * @return
     */
    @Push(value = PushType.A19)
    @PutMapping("/{taskId}/to_father")
    public JSONObject taskToParent(@PathVariable(value = "taskId") String taskId) {
        JSONObject object = new JSONObject();
        try {
            Task task = taskService.getById(taskId);
            Task parentTask = taskService.getOne(new QueryWrapper<Task>().eq("parent_id", task.getTaskId()));
            task.setTaskGroupId(parentTask.getTaskGroupId());
            task.setTaskMenuId(parentTask.getTaskMenuId());
            taskService.updateById(task);
            object.put("result", 1);
            object.put("msg", "移入成功");
            object.put("msgId", taskId);
            object.put("data", new JSONObject().fluentPut("task", taskService.getById(taskId)));
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 对此任务点赞
     *
     * @param taskId 任务id
     * @return
     */
    @Push(value = PushType.A20, name =PushName.TASK,type = 1)
    @PutMapping("/{taskId}/fabulous")
    public JSONObject taskFabulous(@PathVariable(value = "taskId") String taskId) {
        JSONObject object = new JSONObject();
        try {
            Fabulous fabulous = new Fabulous();
            fabulous.setMemberId(ShiroAuthenticationManager.getUserId());
            fabulous.setPublicId(taskId);
            fabulousService.save(fabulous);
            Task task = taskService.getById(taskId);
            int count = fabulousService.count(new QueryWrapper<Fabulous>().eq("public_id", taskId));
            task.setFabulousCount(count);
            taskService.updateById(task);
            object.put("result", 1);
            object.put("msgId", this.getTaskProjectId(taskId));
            object.put("data", new JSONObject().fluentPut("task", taskService.getById(taskId)));
            object.put("id", taskId);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return object;
    }


    /**
     * 上传文件
     *
     * @param projectId 项目id
     */
    @Push(value = PushType.A21,name =PushName.TASK, type = 1)
    @PostMapping("/{taskId}/upload")
    public JSONObject uploadFile(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "files") String files,
            @PathVariable(value = "taskId") String taskId

    ) {
        JSONObject object = new JSONObject();
        try {
            fileService.saveFileBatch(projectId, files, null, taskId);
            object.put("result", 1);
            object.put("msgId", taskId);
            object.put("data", fileService.list(new QueryWrapper<File>().eq("public_id", taskId)));
            object.put("id", taskId);
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 上传模型
     *
     * @param taskId     任务id
     * @param projectId  项目id
     * @param fileCommon 缩略图
     * @param fileModel  模型
     * @param filename   自定义文件名称
     * @return
     */
    @Push(value = PushType.A22,name =PushName.TASK, type = 1)
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
            fileService.saveModel(fileModel, fileCommon, taskId, filename, null);
            jsonObject.put("result", 1);
            jsonObject.put("msgId", taskId);
            jsonObject.put("data", fileService.getOne(new QueryWrapper<File>().eq("public_id", taskId)));
            jsonObject.put("id", taskId);
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取任务的子任务(用于绑定信息处)
     *
     * @param taskId 任务id
     * @return 信息
     */
    @GetMapping("/{taskId}/bind/child")
    public JSONObject getBindChild(@PathVariable String taskId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", taskService.getBindChild(taskId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,获取子任务失败!", e);
        }
    }

    /**
     * 获取任务的看板数据
     *
     * @param projectId 项目id
     * @return 任务集合
     */
    @GetMapping("/{projectId}/panel")
    public JSONObject getTaskPanel(@PathVariable String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", taskService.getTaskPanel(projectId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (ServiceException e) {
            throw new AjaxException(e.getMessage(), e);
        } catch (Exception e) {
            throw new AjaxException("系统异常,数据获取失败!", e);
        }
    }

    /**
     * 更新任务的开始/结束时间
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 结果
     */
    @PutMapping("{taskId}/start_end_time")
    public JSONObject updateStartEndTime(@PathVariable String taskId, @RequestParam(required = false) Long startTime, @RequestParam(required = false) Long endTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (startTime == null && endTime == null) {
                jsonObject.put("msg", "开始和结束时间必须给定一个!");
                jsonObject.put("result", 0);
                return jsonObject;
            }
            Task task = new Task();
            task.setTaskId(taskId);
            task.setStartTime(startTime);
            task.setEndTime(endTime);
            if (taskService.updateById(task)) {
                jsonObject.put("result", 1);
            }
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,更新失败!", e);
        }
    }

    /**
     * 根据任务名称模糊搜索任务名称
     *
     * @param name      任务名称
     * @param projectId 项目id
     * @return 任务信息列表
     */
    @GetMapping("/{name}/like")
    public JSONObject likeName(@PathVariable String name, @RequestParam String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", taskService.likeTaskName(name, projectId));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,信息获取失败!", e);
        }
    }

    /**
     * 获取任务的项目id
     *
     * @param taskId 任务id
     * @return 项目id
     */
    private String getTaskProjectId(String taskId) {
        Task one = taskService.getOne(new QueryWrapper<Task>().select("project_id").eq("task_id", taskId));
        if (one != null && StringUtils.isNotEmpty(one.getProjectId())) {
            return one.getProjectId();
        }
        return "";
    }

    private String getTaskName(String taskId) {
        return taskService.getTaskNameById(taskId);
    }


    /**
     * 将字符串复制到剪切板。
     *
     * @param url 端口/任务 日程  文件  分享 / id
     */
    @PostMapping("/setSysClip")
    public JSONObject setSysClipboardText(@RequestParam String url) {
        JSONObject jsonObject = new JSONObject();
        try {
            //System.setProperty("java.awt.headless", "true");
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(url);
            clip.setContents(tText, null);

            jsonObject.put("result", 1);
            jsonObject.put("msg", "复制到剪贴板成功!");
            return jsonObject;

        } catch (Exception e) {
            throw new AjaxException("系统异常,更新失败!", e);
        }
    }

    /**
     * 更新任务的进度
     *
     * @param taskId   任务id
     * @param progress 进度值
     * @return 结果
     */
    @PostMapping("/progress")
    public Result updateProgress(@NotBlank(message = "任务id不能为空") String taskId,

                                 @NotNull(message = "进度值不能为空！")
                                 @Range(message = "进度值不符合规范", min = 1, max = 100) Integer progress) {

        taskService.updateProgress(taskId, progress);
        return Result.success();
    }

    /**
     * 更新任务的计划工时
     *
     * @param taskId       任务id
     * @param workingHours 进度值
     * @return 结果
     */
    @Push(value = PushType.A31, name =PushName.TASK,type = 1)
    @PostMapping("/work_hours")
    public JSONObject updateWorkHours(@NotBlank(message = "任务id不能为空") String taskId,

                                      @NotNull(message = "计划值不能为空！")
                                      @Range(message = "计划值不符合规范", min = 1, max = 1000) Double workingHours) {
        JSONObject jsonObject = new JSONObject();
        try {
            taskService.updatePlanWorkHours(taskId, workingHours);
            log.info("Update work hours.[{},{}]", taskId, workingHours);
            Task task = taskService.findTaskByTaskId(taskId);
            jsonObject.put("result", 1);
            jsonObject.put("data", task.getTaskId());
            jsonObject.put("msgId", task.getProjectId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param: memberId 成员id
     * @Param: projectId 项目id
     * @return:
     * @Description: 任务安排-查询任务列表
     * @create: 16:36 2020/4/29
     */
    @GetMapping("/{memberId}/getTaskInfoList/{projectId}")
    public JSONObject getTaskInfoList(@PathVariable String memberId, @PathVariable String projectId, String classify) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", taskService.getTaskInfoList(memberId, projectId, classify));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，查询失败");
        }
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param:
     * @return:
     * @Description: 任务安排-批量交接任务-根据企业id和用户id查询项目
     * @create: 15:10 2020/4/30
     */
    @GetMapping("/{orgId}/getProjectsByMemberIdAndOrgId/{memberId}")
    public JSONObject getProjectsByMemberIdAndOrgId(@PathVariable String orgId, @PathVariable String memberId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", projectService.getProjectsByMemberIdAndOrgId(orgId, memberId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，查询失败");
        }
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param:
     * @return:
     * @Description: 任务安排-批量交接任务-根据项目id和用户id查询任务
     * @create: 16:03 2020/4/30
     */
    @GetMapping("/{memberId}/getTasksByProjectIdAndMemberId/{projectId}")
    public JSONObject getTasksByProjectIdAndMemberId(@PathVariable String memberId, @PathVariable String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", taskService.getTasksByProjectIdAndMemberId(memberId, projectId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，查询失败");
        }
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param:
     * @return:
     * @Description: 根据项目id获取执行者列表
     * @create: 10:54 2020/5/6
     */
    @GetMapping("/{projectId}/getExecutors")
    public JSONObject getExecutors(@PathVariable String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", taskService.getExecutors(projectId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }

    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param: taskIds 任务id集合
     * @Param: executor 任务执行者
     * @return:
     * @Description: 将任务批量指派给执行者
     * @create: 13:28 2020/5/6
     */
 /*   @GetMapping("/updateExecutors/{taskIds}/{executor}")
    public JSONObject updateExecutors(@PathVariable List<String> taskIds, @PathVariable String executor) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!CollectionUtils.isEmpty(taskIds)) {
                Task task = new Task();
                task.setExecutor(executor);
                task.setUpdateTime(System.currentTimeMillis());
                boolean update = taskService.update(task, new QueryWrapper<Task>().in("task_id", taskIds));
                if (update) {
                    jsonObject.put("result",1);
                    jsonObject.put("message","指派成功");
                }
            }
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }
    }*/

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param: taskIds 任务id集合
     * @Param: executor 任务执行者
     * @return:
     * @Description: 将任务指派给执行者
     * @create: 13:28 2020/5/6
     */
    @GetMapping("/updateExecutor/{taskId}/{executor}")
    public JSONObject updateExecutors(@PathVariable String taskId, @PathVariable String executor) {
        JSONObject jsonObject = new JSONObject();
        try {
            Task task = new Task();
            task.setExecutor(executor);
            task.setUpdateTime(System.currentTimeMillis());
            task.setTaskId(taskId);
            boolean update = taskService.updateById(task);
            if (update) {
                jsonObject.put("result", 1);
                jsonObject.put("message", "指派成功");
            }

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }
    }
    /**
     * 根据条件查询任务
     * @param example 条件
     *                1、全部任务
     *                2、查询今天的任务
     *                3、我执行的任务
     *                4、已完成的任务
     *                5、待认领的任务
     *                6、未完成的任务
     * @param groupId 分组id
     * @param projectId 项目id
     * @return
     */
    @GetMapping("/selectTaskByExample")
    public JSONObject selectTaskByExample(@RequestParam Integer example,@RequestParam String groupId,@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {

            List<Task>tasks=taskService.selectTaskByExample(example,groupId,projectId);
            jsonObject.put("result",1);
            jsonObject.put("data",tasks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.put("result",0);
        return jsonObject;

    }

    /**
     * 根据条件筛选任务
     * @param keyword 模糊搜索关键字
     * @param projectId 项目id
     * @param executor 执行者id
     * @param tagId 标签id
     * @param startTime 截止时间-开始时间
     * @param endTime 截止时间-结束时间
     * @param memberId 创建者id
     * @param taskUid 参与者id
     * @param taskStatus 是否完成 0未完成 1已完成
     * @param priority 优先级 普通 紧急 非常紧急
     * @return
     */
    @GetMapping("/searchTaskByExample")
    public JSONObject searchTaskByExample(@RequestParam(value = "keyword",required = false) String keyword,
                                          @RequestParam(value = "projectId") String projectId,
                                          @RequestParam(value = "executor",required = false)String executor,
                                          @RequestParam(value = "tagId",required = false)Integer tagId,
                                          @RequestParam(value = "startTime",required = false)Long startTime,
                                          @RequestParam(value = "endTime",required = false)Long endTime,
                                          @RequestParam(value = "memberId",required = false)String memberId,
                                          @RequestParam(value = "taskUid",required = false)String taskUid,
                                          @RequestParam(value = "taskStatus",required = false)Integer taskStatus,
                                          @RequestParam(value = "priority",required = false)String priority
                                          ){
        JSONObject jsonObject = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            String groupId = projectMemberService.findDefaultGroup(projectId, userId);
            Relation relation = new Relation();
            relation.setParentId(groupId);
            relation.setLable(1);

            List<Task>list=taskService.searchTaskByExample(relation,keyword,projectId,executor,tagId,startTime,endTime,memberId,taskUid,taskStatus,priority);
            jsonObject.put("result",1);
            jsonObject.put("data",list);
        } catch (Exception e) {
            jsonObject.put("result",0);
            e.printStackTrace();
        }
        return jsonObject;
    }


}



