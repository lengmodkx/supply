package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Date:2019/4/11 11:00
 * @Author heshaohua
 **/
@RestController
@RequestMapping("me")
public class MyApi {


    @Resource
    private TaskService taskService;

    @Resource
    private ScheduleService scheduleService;

    /**
     * 根据筛选条件获取我的任务信息
     * @param isDone 是否完成
     * @param order 根据 (最近创建时间,截止时间,优先级) 排序
     * @param type 类型 (我执行的,我创建的,我参与的)
     * @return 任务 或者 项目的集合
     */
    @GetMapping("/task")
    public JSONObject getExecute(@RequestParam Boolean isDone,@RequestParam String order,@RequestParam String type){
        JSONObject jsonObject = new JSONObject();
        try {
            if(Constants.PROJECT.equals(order)){
                jsonObject.put("data",taskService.findExecuteOrderProject(isDone));
            } else{
                jsonObject.put("data",taskService.findMeAndOrder(isDone,order,type));
            }
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,获取信息失败!",e);
        }
    }

    /**
     * 获取近期的事儿
     * @return 任务和日程的集合
     */
    @GetMapping("recentThing")
    public JSONObject getRecentThing(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",new JSONObject().fluentPut("task",taskService.findByUserIdAndByTreeDay()).fluentPut("schedule",scheduleService.findScheduleByUserIdAndByTreeDay()));
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,获取信息失败!",e);
        }
    }
}
