package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.task.Task;
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
     * @param task 任务实体信息
     * @return
     */
    @GetMapping("addTask")
    @ResponseBody
    public JSONObject addTask(Task task){
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
            task.setTaskId(id);
            //保存任务信息到数据库
            taskService.saveTask(task);
            jsonObject.put("msg","添加任务成功!");
        } catch (Exception e){
            jsonObject.put("error","任务添加失败!");
            log.error("当前任务保存失败!  用户:" + id + "{}",e);
        }
        return jsonObject;
    }

    /**
     *
     * @param task 包含该任务的id、任务组id
     * @return
     */
    @GetMapping("mobileTask")
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
            log.error("当前任务移动失败!  任务id：{}, {}", task.getTagId(),e);
            jsonObject.put("result", 0);
            jsonObject.put("msg","任务移动失败！");
        }
        return jsonObject;
    }

    @GetMapping("moveToRecycleBin")
    public JSONObject moveToRecycleBin(@RequestParam String taskId,@RequestParam String taskDel) {
        JSONObject jsonObject = new JSONObject();
        try {
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
            jsonObject.put("msg", "操作失败！");
            jsonObject.put("result","0");
        }
        return jsonObject;
    }
}
