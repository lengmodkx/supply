package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.task.TaskService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    @Resource
    private FileService fileService;

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

    /**
     * 获取未来的日程
     * @return
     */
    @GetMapping("/schedule/after")
    public JSONObject getScheduleAfter(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",scheduleService.findMe());
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,获取数据失败!",e);
        }
    }

    /**
     * 获取和用户有关的日程的月份信息
     * @return 月份集合
     */
    @GetMapping("/schedule/before")
    public JSONObject getScheduleBefore(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",scheduleService.findScheduleMonth());
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,数据获取失败!",e);
        }
    }

    /**
     * 根据月份获取日程信息
     * @param month 月份
     * @return
     */
    @GetMapping("schedule/{month}")
    public JSONObject getScheduleByMonth(@PathVariable String month){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result",1);
            jsonObject.put("data",scheduleService.findByMonth(month));
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,数据获取失败!",e);
        }
    }

    /**
     * 获取我创建的文件并且排序
     * @param order 排序规则(名称,大小,创建时间)
     * @return 我创建的文件数据
     */
    @GetMapping("file")
    public JSONObject meCreated(@RequestParam(required = false) String order,String type){
        JSONObject jsonObject = new JSONObject();
        try {
            if ("create".equals(type)){
                jsonObject.put("data",fileService.created(order));
                jsonObject.put("result",1);
            }else if ("join".equals(type)){
                jsonObject.put("data",null);
                jsonObject.put("result",1);
            }else {
                jsonObject.put("result",0);
            }


            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,获取数据失败!");
        }
    }

}
