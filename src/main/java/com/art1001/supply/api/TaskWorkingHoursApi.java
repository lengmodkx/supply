package com.art1001.supply.api;


import com.art1001.supply.api.request.WorkingHoursRequestParam;
import com.art1001.supply.entity.Result;
import com.art1001.supply.service.task.TaskWorkingHoursService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
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
    @RequestMapping("/remove")
    public Result removeWorkingHours(@NotBlank(message = "工时id不能为空！") String id){
        log.info("Remove working hours by id.[{}]", id);
        taskWorkingHoursService.removeWorkingHours(id);
        return Result.success();
    }

    /**
     * 添加一条任务工时记录
     * @param workingHoursRequestParam 参数信息
     * @return 结果
     */
    @RequestMapping("/addition")
    public Result additionWorkingHours(@Validated WorkingHoursRequestParam workingHoursRequestParam){
        log.info("Addition working hours. [{}]", workingHoursRequestParam);
        taskWorkingHoursService.addition(workingHoursRequestParam);
        return Result.success();
    }





}

