package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Todo;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    /**
     * 创建任务
     * @param taskName 任务名称
     * @param taskUIds 任务参与者
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
    @PostMapping
    public JSONObject createTask(@RequestParam("taskName") String taskName,
                                 @RequestParam("taskUIds") String taskUIds,
                                 @RequestParam("privacyPattern") Integer privacyPattern,
                                 @RequestParam(value = "executor",required = false) String executor,
                                 @RequestParam(value = "startTime",required = false) String startTime,
                                 @RequestParam(value = "endTime",required = false)String endTime,
                                 @RequestParam(value = "repeat",required = false)String repeat,
                                 @RequestParam(value = "remind",required = false)String remind,
                                 @RequestParam(value = "priority",required = false)String priority,
                                 @RequestParam(value = "tagIds",required = false)String tagIds){
        JSONObject object = new JSONObject();
        try {
            Task task = new Task();
            task.setTaskName(taskName);
            task.setTaskUIds(taskUIds);
            task.setPrivacyPattern(privacyPattern);
            if(StringUtils.isNotEmpty(executor)){
                task.setExecutor(executor);
            }

            if(StringUtils.isNotEmpty(startTime)){
                task.setStartTime(DateUtils.strToLong(startTime));
            }

            if(StringUtils.isNotEmpty(endTime)){
                task.setStartTime(DateUtils.strToLong(endTime));
            }

            if(StringUtils.isNotEmpty(repeat)){
                task.setRepeat(repeat);
            }

            if(StringUtils.isNotEmpty(remind)){
                task.setRemind(remind);
            }
            if(StringUtils.isNotEmpty(priority)){
                task.setPriority(priority);
            }
            if(StringUtils.isNotEmpty(tagIds)){
                task.setTagId(tagIds);
            }
            taskService.saveTask(task);
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
    @Todo
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
    @Todo
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
    @Todo
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
    @Todo
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
    @Todo
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
    @Todo
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
    @Todo
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
    @Todo
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
    @Todo
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
     * 更新任务标签
     * @param taskId 任务id
     * @param tagIds 任务标签
     * @return JSONObject
     */
    @Todo
    @PutMapping("/{taskId}/tags")
    public JSONObject upadteTaskTags(@PathVariable(value = "taskId")String taskId,
                                         @RequestParam(value = "tagIds")String tagIds){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTagId(tagIds);
            taskService.updateTask(task);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,标签更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 新增子任务
     * @param taskId 父任务id
     * @param taskName 子任务名称
     * @param executor 子任务的执行者
     * @param endTime 子任务的结束时间
     * @return JSONObject
     */
    @Todo
    @PutMapping("/{taskId}/addchild")
    public JSONObject addChildTask(@PathVariable(value = "taskId")String taskId,
                                   @RequestParam(value = "taskName")String taskName,
                                   @RequestParam(value = "executor")String executor,
                                   @RequestParam(value = "endTime",required = false)String endTime){
        JSONObject object = new JSONObject();
        try{
            Task task = new Task();
            task.setParentId(taskId);
            task.setTaskName(taskName);
            task.setExecutor(executor);
            if(StringUtils.isNotEmpty(endTime)){
                task.setEndTime(DateUtils.strToLong(endTime));
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
    @Todo
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
     * @param muneId 菜单id
     * @return
     */
    @PutMapping("/{taskId}/copy")
    public JSONObject copyTask(@PathVariable(value = "taskId")String taskId,
                               @RequestParam(value = "projectId")String projectId,
                               @RequestParam(value = "muneId")String muneId){
        JSONObject object = new JSONObject();
        try{
            taskService.copyTask(taskId,projectId,muneId);
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
     * @param muneId 菜单id
     * @return
     */
    @PutMapping("/{taskId}/move")
    public JSONObject moveTask(@PathVariable(value = "taskId")String taskId,
                               @RequestParam(value = "projectId")String projectId,
                               @RequestParam(value = "muneId")String muneId){
        JSONObject object = new JSONObject();
        try{
            taskService.mobileTask(taskId,projectId,muneId);
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
            task.setIsDel(1);
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
