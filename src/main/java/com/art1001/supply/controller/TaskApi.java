package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */

/**
 * 任务增删改查，复制，移动
 * @author 汪亚锋
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
            log.error("创建任务:{}",e);
            throw new AjaxException(e);
        }

        return object;
    }



}
