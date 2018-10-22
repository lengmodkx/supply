package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Log;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.fabulous.Fabulous;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.tag.TagRelation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.fabulous.FabulousService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private ProjectService projectService;

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
    /**
     * 任务页面初始化
     * @return
     */
    @GetMapping("/{taskId}")
    public JSONObject getTask(@PathVariable(value = "taskId") String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            //查询出此条任务的具体信息
            Task taskInfo = taskService.findTaskByTaskId(taskId);
            jsonObject.put("task",taskInfo);

            //当前项目Id
            jsonObject.put("projectId",taskInfo.getProjectId());

            //判断当前用户有没有对该任务点赞
            int count = fabulousService.count(new QueryWrapper<Fabulous>().eq("member_id", ShiroAuthenticationManager.getUserId()).eq("public_id", taskId));
            jsonObject.put("isFabulous",count);

            //查询出当前的项目信息详情
            jsonObject.put("projectInfo",projectService.findProjectByProjectId(taskInfo.getProjectId()));

            //判断当前用户有没有收藏该任务
            int collectCount = publicCollectService.count(new QueryWrapper<PublicCollect>().eq("public_id", taskId).eq("member_id", ShiroAuthenticationManager.getUserId()));
            jsonObject.put("collectCount",collectCount);

            //查询出该任务所在的菜单信息
            Relation menuRelation = relationService.getOne(new QueryWrapper<Relation>().select("relation_id","relation_name","parent_id").eq("relation_id", taskInfo.getTaskMenuId()));
            jsonObject.put("menu",menuRelation);

            //根据该任务的菜单查询出任务的分组信息
            Relation taskGroup = relationService.getOne(new QueryWrapper<Relation>().select("relation_id","relation_name").eq("relation_id",menuRelation.getParentId()));
            jsonObject.put("taskGroup",taskGroup);

            //查询出任务的关联信息
            List<Binding> bindings = bindingService.list(new QueryWrapper<Binding>().eq("public_id", taskId));
            jsonObject.put("bindings",bindings);

            //查询出我参与的所有项目信息
            List<Project> projectByMemberId = projectService.findProjectByMemberId(ShiroAuthenticationManager.getUserId(),0);
            jsonObject.put("projectByMemberId",projectByMemberId);

            //查询出该任务的日志信息
            jsonObject.put("taskLogs",logService.initLog(taskId));

            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,任务数据拉取失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
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
    @Log
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
            object.put("data",task.getTaskId());
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
    @DeleteMapping("/{taskId}")
    public JSONObject deleteTask(@PathVariable(value = "taskId")String taskId){
        JSONObject object = new JSONObject();
        try{
            taskService.deleteTask(taskId);
            object.put("result",1);
            object.put("msg","删除成功!");
        }catch(Exception e){
            log.error("系统异常,删除失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 完成任务/重做任务
     * @param taskId 任务id
     * @param taskStatus 完成/未完成
     * @return JSONObject
     */
    @Log
    @PutMapping("/{taskId}/status")
    public JSONObject updateTaskStatus(@PathVariable(value = "taskId")String taskId,@RequestParam(value = "taskStatus")String taskStatus){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskStatus(taskStatus);
            taskService.updateTask(task);
            object.put("result",1);
            object.put("msg","更新成功");
        }catch(Exception e){
            log.error("系统异常,状态更新失败:",e);
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
    @Log
    @PutMapping("/{taskId}/name")
    public JSONObject upadteTaskName(@PathVariable(value = "taskId")String taskId,
                                 @RequestParam(value = "taskName")String taskName){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskName(taskName);
            taskService.updateTask(task);
            object.put("result",1);
            object.put("msg","更新成功");
        }catch(Exception e){
            log.error("系统异常,任务名称更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务执行者
     * @param taskId 任务id
     * @param executor 执行者id
     * @return JSONObject
     */
    @Log
    @PutMapping("/{taskId}/executor")
    public JSONObject upadteTaskExecutor(@PathVariable(value = "taskId")String taskId,
                                     @RequestParam(value = "executor")String executor){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setExecutor(executor);
            taskService.updateTask(task);
            object.put("result",1);
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
    @Log
    @PutMapping("/{taskId}/starttime")
    public JSONObject upadteTaskStartTime(@PathVariable(value = "taskId")String taskId,
                                         @RequestParam(value = "startTime")String startTime){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setStartTime(DateUtils.strToLong(startTime));
            taskService.updateTask(task);
            object.put("result",1);
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
    @Log
    @PutMapping("/{taskId}/endtime")
    public JSONObject upadteTaskEndTime(@PathVariable(value = "taskId")String taskId,
                                          @RequestParam(value = "endTime")String endTime){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setEndTime(DateUtils.strToLong(endTime));
            taskService.updateTask(task);
            object.put("result",1);
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
    @Log
    @PutMapping("/{taskId}/repeat")
    public JSONObject upadteTaskRepeat(@PathVariable(value = "taskId")String taskId,
                                        @RequestParam(value = "repeat")String repeat){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setRepeat(repeat);
            taskService.updateTask(task);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,重复性更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新任务提醒
     * @param taskId 任务id
     * @param remind 任务提醒
     * @return JSONObject
     */
    @Log
    @PutMapping("/{taskId}/remind")
    public JSONObject upadteTaskRemind(@PathVariable(value = "taskId")String taskId,
                                       @RequestParam(value = "remind")String remind){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setRemind(remind);
            taskService.updateTask(task);
            object.put("result",1);
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
    @Log
    @PutMapping("/{taskId}/remarks")
    public JSONObject upadteTaskRemarks(@PathVariable(value = "taskId")String taskId,
                                       @RequestParam(value = "remarks")String remarks){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setRemarks(remarks);
            taskService.updateTask(task);
            object.put("result",1);
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
    @Log
    @PutMapping("/{taskId}/priority")
    public JSONObject upadteTaskPriority(@PathVariable(value = "taskId")String taskId,
                                        @RequestParam(value = "priority")String priority){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setPriority(priority);
            taskService.updateTask(task);
            object.put("result",1);
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
    @Log
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
        }catch(Exception e){
            log.error("系统异常,子任务添加失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 任务参与者
     * @param taskId 任务id
     * @param taskUids 参与者id
     * @return
     */
    @Log
    @PutMapping("/{taskId}/members")
    public JSONObject addTaskUids(@PathVariable(value = "taskId")String taskId,
                                  @RequestParam(value = "taskUids")String taskUids){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setParentId(taskId);
            task.setTaskUIds(taskUids);
            taskService.saveTask(task);
            object.put("result",1);
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
     * @param menuId 菜单id
     * @return
     */
    @PutMapping("/{taskId}/copy")
    public JSONObject copyTask(@PathVariable(value = "taskId")String taskId,
                               @RequestParam(value = "projectId")String projectId,
                               @RequestParam(value = "menuId")String menuId){
        JSONObject object = new JSONObject();
        try{
            taskService.copyTask(taskId,projectId,menuId);
            object.put("result",1);
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
     * @param menuId 菜单id
     * @return
     */
    @PutMapping("/{taskId}/move")
    public JSONObject moveTask(@PathVariable(value = "taskId")String taskId,
                               @RequestParam(value = "projectId")String projectId,
                               @RequestParam(value = "menuId")String menuId){
        JSONObject object = new JSONObject();
        try{
            taskService.mobileTask(taskId,projectId,menuId);
            object.put("result",1);
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
    @PutMapping("/{taskId}/recyclebin")
    public JSONObject moveToRecycleBin(@PathVariable(value = "taskId")String taskId){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskDel(1);
            taskService.updateTask(task);
            object.put("result",1);
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
    @PutMapping("/{taskId}/privacy")
    public JSONObject taskPrivacy(@PathVariable(value = "taskId")String taskId,@RequestParam Integer privacy){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setPrivacyPattern(privacy);
            taskService.updateTask(task);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,隐私模式更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }
}
