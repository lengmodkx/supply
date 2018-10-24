package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Log;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.fabulous.Fabulous;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.tag.TagRelation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskRemindRule;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.quartz.MyJob;
import com.art1001.supply.quartz.QuartzService;
import com.art1001.supply.quartz.job.RemindJob;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.fabulous.FabulousService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.task.TaskRemindRuleService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

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
public class TaskApi {

    @Resource
    private TaskService taskService;

    @Resource
    private PublicCollectService publicCollectService;

    @Resource
    private RelationService relationService;

    @Resource
    private BindingService bindingService;

    @Resource
    private LogService logService;

    @Resource
    private FabulousService fabulousService;

    /** 标签标签的逻辑层接口 */
    @Resource
    private TagRelationService tagRelationService;

    @Resource
    private UserService userService;

    @Resource
    private QuartzService quartzService;

    @Resource
    private TaskRemindRuleService taskRemindRuleService;

    /**
     * 任务页面初始化
     * @return
     */
    @GetMapping("/{taskId}")
    public JSONObject getTask(@PathVariable(value = "taskId") String taskId){
        JSONObject object = new JSONObject();
        try {
            //查询出此条任务的具体信息
            Task taskInfo = taskService.findTaskByTaskId(taskId);
            object.put("task",taskInfo);
            //判断当前用户有没有对该任务点赞
            int count = fabulousService.count(new QueryWrapper<Fabulous>().eq("member_id", ShiroAuthenticationManager.getUserId()).eq("public_id", taskId));
            object.put("isFabulous",count);
            //判断当前用户有没有收藏该任务
            int collectCount = publicCollectService.count(new QueryWrapper<PublicCollect>().eq("public_id", taskId).eq("member_id", ShiroAuthenticationManager.getUserId()));
            object.put("collect",collectCount);
            //查询出该任务所在的菜单信息
            Relation relation = relationService.getOne(new QueryWrapper<Relation>().eq("project_id",taskInfo.getProjectId()));
            object.put("relation",relation);
            //查询出任务的关联信息
            List<Binding> bindings = bindingService.list(new QueryWrapper<Binding>().eq("public_id", taskId));
            object.put("bindings",bindings);
            //查询出该任务的日志信息
            object.put("taskLogs",logService.initLog(taskId));
            object.put("result",1);
        } catch (Exception e){
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 创建任务
     * @param taskName 任务名称
     * @param memberIds 任务参与者
     * @param privacyPattern 任务隐私模式
     * @param executor 任务执行者
     * @param startTime 任务开始时间
     * @param endTime 任务结束时间
     * @param repeat 任务重复
     * @param remind 任务提醒
     * @param priority 任务优先级
     * @param tagIds 任务标签
     * @return JSONObject
     */
    @Log(PushType.A1)
    @Push(value = PushType.A1,type = 1)
    @PostMapping
    public JSONObject addTask(@RequestParam("taskName") String taskName,
                              @RequestParam("memberIds") String memberIds,
                              @RequestParam("privacyPattern") Integer privacyPattern,
                              @RequestParam("projectId") String projectId,
                              @RequestParam(value = "executor",required = false) String executor,
                              @RequestParam(value = "startTime",required = false) String startTime,
                              @RequestParam(value = "endTime",required = false)String endTime,
                              @RequestParam(value = "repeat",required = false)String repeat,
                              @RequestParam(value = "remind",required = false)String remind,
                              @RequestParam(value = "priority",required = false)String priority,
                              @RequestParam(value = "tagIds",required = false)String tagIds,
                              @RequestParam(value = "taskMenuId",required = false)String taskMenuId,
                              @RequestParam(value = "taskGroupId",required = false)String taskGroupId){
        JSONObject object = new JSONObject();
        try {
            Task task = new Task();
            task.setTaskName(taskName);
            task.setTaskUIds(memberIds);
            task.setTaskMenuId(taskMenuId);
            task.setProjectId(projectId);
            task.setTaskGroupId(taskGroupId);
            task.setPrivacyPattern(privacyPattern);
            task.setExecutor(executor);
            task.setRepeat(repeat);
            task.setRemind(remind);
            task.setPriority(priority);
            if(StringUtils.isNotEmpty(startTime)){
                task.setStartTime(DateUtils.strToLong(startTime));
            }

            if(StringUtils.isNotEmpty(endTime)){
                task.setStartTime(DateUtils.strToLong(endTime));
            }
            //设置任务的创建者
            task.setMemberId(ShiroAuthenticationManager.getUserId());
            taskService.saveTask(task);
            //保存任务和标签的关联关系
            if(StringUtils.isNotEmpty(tagIds)){
                Arrays.stream(tagIds.split(",")).forEach(tagId->{
                    TagRelation tagRelation = new TagRelation();
                    tagRelation.setTagId(Long.valueOf(tagId));
                    tagRelation.setTaskId(task.getTaskId());
                    tagRelationService.save(tagRelation);
                });
            }
            object.put("result",1);
            object.put("msg","创建成功!");
            object.put("data",task);
            object.put("msgId",projectId);
            object.put("id",task.getTaskId());
            object.put("name",task.getTaskName());
        }catch (Exception e){
            log.error("创建任失败:",e);
            throw new AjaxException(e);
        }

        return object;
    }

    /**
     * 删除任务
     * @param taskId 任务id
     * @return JSONObject
     */
    @Log(PushType.A2)
    @Push(value = PushType.A2,type = 1)
    @DeleteMapping("/{taskId}")
    public JSONObject deleteTask(@PathVariable(value = "taskId")String taskId){
        JSONObject object = new JSONObject();
        try{
            taskService.removeById(taskId);
            object.put("result",1);
            object.put("msg","删除成功!");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("taskId",taskId));
        }catch(Exception e){
            log.error("系统异常,删除失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 完成任务
     * @param taskId 任务id
     * @return JSONObject
     */
    @Log(PushType.A3)
    @Push(value = PushType.A3,type = 1)
    @PutMapping("/{taskId}/finish")
    public JSONObject finishTask(@PathVariable(value = "taskId")String taskId){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskStatus("1");
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("status",1));
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
            task.setTaskStatus("0");
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("status",1));
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
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("taskName",taskName));
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
                                          @RequestParam(value = "startTime")String startTime){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            if(StringUtils.isNotEmpty(startTime)){
                task.setStartTime(DateUtils.strToLong(startTime));
            }else{
                task.setStartTime(null);
            }

            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("startTime",startTime));
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
                                        @RequestParam(value = "endTime")String endTime){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            if(StringUtils.isNotEmpty(endTime)){
                task.setStartTime(DateUtils.strToLong(endTime));
            }else{
                task.setStartTime(null);
            }
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("endTime",endTime));
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
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("repeat",repeat));
        }catch(Exception e){
            log.error("系统异常,重复性更新失败:",e);
            throw new AjaxException(e);
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
    @Log(PushType.A10)
    @Push(value = PushType.A10,type = 1)
    @PutMapping("/{taskId}/remind")
    public JSONObject updateTaskRemind(@PathVariable(value = "taskId")String taskId,
                                       @RequestParam(value = "jobName")String jobName,
                                       @RequestParam(value = "remindType")String remindType,
                                       @RequestParam(value = "num", required = false) int num,
                                       @RequestParam(value = "timeType", required = false) String timeType,
                                       @RequestParam(value = "customTime", required = false) String customTime,
                                       @RequestParam(value = "users") String[] users){
        JSONObject object = new JSONObject();
        try{
            //更新任务提醒
            TaskRemindRule taskRemindRule = new TaskRemindRule();
            taskRemindRule.setNum(num);
            taskRemindRule.setRemindType(remindType);
            taskRemindRule.setTimeType(timeType);
            taskRemindRule.setCustomTime(customTime);
            taskRemindRuleService.update(taskRemindRule,new QueryWrapper<TaskRemindRule>().eq("job_name",jobName));
            //更新成功后添加到定时任务
            MyJob myJob = new MyJob();
            myJob.setJobName(jobName);
            myJob.setCronTime(taskService.remindCron(taskId,remindType,num,timeType,customTime));
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("users",users);
            myJob.setJobDataMap(jobDataMap);
            myJob.setJobGroupName("task");
            myJob.setTriggerGroupName("task");
            quartzService.modifyJobDateMap(RemindJob.class,myJob);
        }catch(Exception e){
            log.error("系统异常,提醒模式更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
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
                                       @RequestParam(value = "remindType")String remindType,
                                       @RequestParam(value = "num", required = false) int num,
                                       @RequestParam(value = "timeType", required = false) String timeType,
                                       @RequestParam(value = "customTime", required = false) String customTime,
                                       @RequestParam(value = "users") String[] users){
        JSONObject object = new JSONObject();
        try{
            //封装实体
            TaskRemindRule taskRemindRule = new TaskRemindRule();
            taskRemindRule.setId(IdGen.uuid());
            taskRemindRule.setJobName(IdGen.uuid());
            taskRemindRule.setNum(num);
            taskRemindRule.setRemindType(remindType);
            taskRemindRule.setTaskId(taskId);
            taskRemindRule.setTimeType(timeType);
            String cron = taskService.remindCron(taskId,remindType,num,timeType,customTime);
            taskRemindRule.setTimeCron(cron);
            taskRemindRuleService.save(taskRemindRule);

            //添加到定时任务
            MyJob myJob = new MyJob();
            myJob.setJobName(taskRemindRule.getJobName());
            myJob.setCronTime(cron);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("users",users);
            myJob.setJobDataMap(jobDataMap);
            myJob.setJobGroupName("task");
            myJob.setTriggerGroupName("task");
            quartzService.addJobByCronTrigger(RemindJob.class,myJob);
        }catch(Exception e){
            log.error("系统异常,提醒模式更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
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
            object.put("msg","更新成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("priority",priority));
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
            task.setExecutor(executor);
            if(StringUtils.isNotEmpty(startTime)){
                task.setStartTime(DateUtils.strToLong(startTime));
            }
            taskService.saveTask(task);
            object.put("result",1);
            object.put("msg","创建成功!");
            object.put("data",task);
            object.put("msgId",taskId);
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
    @Log(PushType.A14)
    @Push(value = PushType.A14,type = 1)
    @PutMapping("/{taskId}/members")
    public JSONObject addTaskUids(@PathVariable(value = "taskId")String taskId,
                                  @RequestParam(value = "taskUids")String taskUids){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setParentId(taskId);
            task.setTaskUIds(taskUids);
            taskService.updateById(task);
            object.put("result",1);
            object.put("msg","更新成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("members",userService.findManyUserById(taskUids)));
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
     * @return
     */
    @Log(PushType.A15)
    @Push(value = PushType.A15)
    @PutMapping("/{taskId}/copy")
    public JSONObject copyTask(@PathVariable(value = "taskId")String taskId,
                               @RequestParam(value = "projectId")String projectId,
                               @RequestParam(value = "groupId")String groupId,
                               @RequestParam(value = "menuId")String menuId){
        JSONObject object = new JSONObject();
        try{
            taskService.copyTask(taskId,projectId,groupId,menuId);
            object.put("result",1);
            object.put("msg","复制成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("task",taskService.getById(taskId)));
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
    @Push(value = PushType.A16)
    @PutMapping("/{taskId}/move")
    public JSONObject moveTask(@PathVariable(value = "taskId")String taskId,
                               @RequestParam(value = "projectId")String projectId,
                               @RequestParam(value = "groupId")String groupId,
                               @RequestParam(value = "menuId")String menuId){
        JSONObject object = new JSONObject();
        try{
            taskService.mobileTask(taskId,projectId,groupId,menuId);
            object.put("result",1);
            object.put("msg","移动成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("task",taskService.getById(taskId)));
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
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("task",taskService.getById(taskId)));
        }catch(Exception e){
            log.error("系统异常,移入回收站失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     *  任务隐私模式
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
        }catch(Exception e){
            log.error("系统异常,隐私模式更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     *  子任务转父任务
     * @param taskId 任务id
     * @return
     */
    @Log(PushType.A19)
    @Push(value = PushType.A19)
    @PutMapping("/{taskId}/toFather")
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
     *  对此任务点赞
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
            object.put("msg","移入成功");
            object.put("msgId",taskId);
            object.put("data",new JSONObject().fluentPut("task",taskService.getById(taskId)));
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }


}
