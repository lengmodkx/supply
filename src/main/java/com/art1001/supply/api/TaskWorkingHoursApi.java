package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushName;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.request.WorkingHoursRequestParam;
import com.art1001.supply.entity.Result;
import com.art1001.supply.service.task.TaskWorkingHoursService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author heshaohua
 * @since 2020-03-23
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/taskWorkingHours")
public class TaskWorkingHoursApi {


    @Resource
    private TaskWorkingHoursService taskWorkingHoursService;


    /**
     * 获取任务的工时列表
     * @param taskId 任务id
     * @return 工时列表
     */
    @RequestMapping("/list")
    public Result getWorkingHoursList(@NotBlank(message = "任务id不能为空！") String taskId){
        log.info("Get working hours list.[{}]", taskId);
        return Result.success(taskWorkingHoursService.getWorkingHoursList(taskId));
    }

    /**
     * 移除一项工时
     * @param id 任务工时id
     * @return 结果
     */
    @Push(value = PushType.A32, name =PushName.TASK,type = 1)
    @RequestMapping("/remove")
    public JSONObject removeWorkingHours(@RequestParam String id,
                                         @RequestParam String taskId,
                                         @RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        log.info("Remove working hours by id.[{}]", id);
        taskWorkingHoursService.removeWorkingHours(id);
        jsonObject.put("result", 1);
        jsonObject.put("data", taskId);
        jsonObject.put("msgId", projectId);
        return jsonObject;
    }

    /**
     * 添加一条任务工时记录
     * @param workingHoursRequestParam 参数信息
     * @return 结果
     */
    @Push(value = PushType.A32, name = PushName.TASK,type = 1)
    @RequestMapping("/addition")
    public JSONObject additionWorkingHours(@Validated WorkingHoursRequestParam workingHoursRequestParam){
        JSONObject jsonObject = new JSONObject();
        log.info("Addition working hours. [{}]", workingHoursRequestParam);
        taskWorkingHoursService.addition(workingHoursRequestParam);
        jsonObject.put("result", 1);
        jsonObject.put("data", workingHoursRequestParam.getTaskId());
        jsonObject.put("msgId", workingHoursRequestParam.getProjectId());
        return jsonObject;
    }
}

