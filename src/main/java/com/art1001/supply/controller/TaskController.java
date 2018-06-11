package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.IdGen;
import io.netty.handler.codec.json.JsonObjectDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 任务控制器，关于任务的操作
 */
@Controller
@Slf4j
public class TaskController {

    @Resource
    private TaskService taskService;

    /**
     * 添加新任务
     * @param startTime 任务开始时间
     * @param endTime 任务结束时间
     * @param remindTime 任务提醒时间
     * @param repetitionTime 任务重复时间
     * @param task 任务实体信息
     * @return
     */
    @PostMapping("addTask")
    @ResponseBody
    public JSONObject addTask(@RequestParam String startTime,
                              @RequestParam String endTime,
                              @RequestParam String remindTime,
                              @RequestParam String repetitionTime,
                              Task task
    ){
        JSONObject jsonObject = new JSONObject();
        String id = "";
        try {
            //获取当前session中的用户,如果有用户则为该任务的创建人
            id = String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("id"));
            if(id == null || id.equals("")){
                jsonObject.put("msg","用户登陆超时，请重新登陆！");
                jsonObject.put("result","0");
                return jsonObject;
            }
            task.setMemberId(id);
            //保存任务信息到数据库
            taskService.saveTask(startTime,endTime,remindTime,repetitionTime,task);
            jsonObject.put("msg","添加任务成功!");
        } catch (Exception e){
            jsonObject.put("msg","任务添加失败!");
            jsonObject.put("result","0");
            log.error("当前任务保存失败!  用户:" + id + "{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 任务移动
     * @param task 包含该任务的id、当前组id、欲移动到任务组id
     * @return
     */
    @PostMapping("mobileTask")
    @ResponseBody
    public JSONObject mobileTask(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            //修改该任务的任务组编号
            int result = taskService.updateTask(task);
            if(result > 0){
                jsonObject.put("result", 1);
                jsonObject.put("msg","任务移动成功！");
            } else{
                jsonObject.put("result", 0);
                jsonObject.put("msg","任务移动失败！");
            }
        } catch (Exception e){
            log.error("当前任务移动失败!  任务id：{}\t 该任务初始组为:{}, {}", task.getTagId(),task.getTaskMenuId(),e);
            jsonObject.put("result", 0);
            jsonObject.put("msg","系统异常,移动失败！");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     *  将任务 (移入回收站/回复)
     * @param taskId 任务的id
     * @param taskDel 任务是否在回收站
     * @return
     */
    @PostMapping("moveToRecycleBin")
    @ResponseBody
    public JSONObject moveToRecycleBin(@RequestParam String taskId,@RequestParam String taskDel) {
        JSONObject jsonObject = new JSONObject();
        try {
            //将任务移入回收站
            int result = taskService.moveToRecycleBin(taskId, taskDel);
            if (result > 0) {
                jsonObject.put("msg", "操作成功！");
                jsonObject.put("result","1");
            } else {
                jsonObject.put("msg", "操作失败！");
                jsonObject.put("result","0");
            }
        } catch (Exception e) {
            log.error("操作失败，任务：" + taskId + ",操作前该任务状态:\t{}, {}",taskDel, e);
            jsonObject.put("msg", "系统异常,操作失败！");
            jsonObject.put("result","0");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更细项目信息 包括以下
     * 1.任务状态(完成 / 重做)
     * 2.修改任务内容丶优先级丶重复性
     * 3.修改任务备注
     * 4.修改任务的执行者
     *
     * @param task 任务的实体信息
     */
    @PostMapping("upateTaskInfo")
    @ResponseBody
    public JSONObject updateTask(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            int result = taskService.updateTask(task);
            if(result > 0){
                jsonObject.put("msg","更新成功!");
                jsonObject.put("result",result);
            } else{
                jsonObject.put("msg","更新失败!");
                jsonObject.put("result",result);
            }
        } catch (Exception e){
            log.error("系统异常,操作失败! 当前任务:{},{}",task.getTaskId(),e);
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
            log.error("系统异常,移除失败! 任务: {},{}",taskId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 完成任务(2=完成 1=未完成)
     * @param taskId 当前任务id
     * @param taskStatus 当前任务状态
     */
    @PostMapping
    @ResponseBody
    public JSONObject changeTaskStatus(@RequestParam String taskId,@RequestParam String taskStatus){
        JSONObject jsonObject = new JSONObject();
        try {
            //改变任务状态
            int result = taskService.changeTaskStatus(taskId,taskStatus);
            if(result >1){
                jsonObject.put("msg","修改成功!");
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","修改失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("任务状态修改失败! 任务id: {}, \t 修改前状态:{},{}",taskId,taskStatus,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新任务时间( 开始 / 结束 / 提醒)
     * @param startTime 任务开始时间
     * @param endTime 任务结束时间
     * @param taskId 任务id
     * @param remindTime 任务提醒时间
     */
    public JSONObject updateTaskTime(@RequestParam String startTime,
                                     @RequestParam String endTime,
                                     @RequestParam String remindTime,
                                     @RequestParam String taskId
    ){
        JSONObject jsonObject = new JSONObject();
        try {
            //更新任务时间信息
            int result = taskService.updateTaskTime(taskId,startTime,endTime,remindTime);
            if(result >1){
                jsonObject.put("msg","时间更新成功!");
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","时间更新失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("任务状态修改失败! 任务id: {},\t 修改前任务开始时间:{}\t 修改前任务结束时间:{}\t修改任务提醒时间{},{}",taskId,startTime,endTime,remindTime,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

}
