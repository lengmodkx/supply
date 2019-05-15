package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.task.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description 日历api接口
 * @Date:2019/5/10 10:37
 * @Author heshaohua
 **/
@RestController
@RequestMapping("calendar")
public class CalendarApi {

    /**
     * 任务的逻辑层bean
     */
    @Resource
    private TaskService taskService;

    /**
     * 日程的逻辑层bean
     */
    @Resource
    private ScheduleService scheduleService;

    /**
     * 获取一个用户的日历任务信息和日程日历信息
     * @param userId 用户id
     * @return 任务和日历信息
     */
    @GetMapping("/{userId}")
    public JSONObject getCalendar(@PathVariable String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", new JSONObject().fluentPut("tasks", taskService.relevant(userId)).fluentPut("schedules", scheduleService.relevant(userId)));
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,获取数据失败!",e);
        }
    }
}
