package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 日程
 * @author 汪亚锋
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@RestController
@RequestMapping("schedules")
public class ScheduleApi {

    @Resource
    private ScheduleService scheduleService;

    /**
     * 创建日程
     * @param projectId 项目id
     * @param scheduleName 日程名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param repeat 是否重复
     * @param remind 是否提醒
     * @param memberIds 参与者
     * @param privacy 隐私
     * @return
     */
    @PostMapping
    public JSONObject addSchedule(@RequestParam String projectId,
                                    @RequestParam String scheduleName,
                                    @RequestParam(required = false) String startTime,
                                    @RequestParam(required = false) String endTime,
                                    @RequestParam(defaultValue = "不重复") String repeat,
                                    @RequestParam(defaultValue = "不提醒") String remind,
                                    @RequestParam String memberIds,
                                    @RequestParam(defaultValue = "0") Integer privacy){
        JSONObject object = new JSONObject();
        try{
            Schedule schedule = new Schedule();
            schedule.setProjectId(projectId);
            schedule.setScheduleName(scheduleName);
            if(StringUtils.isNotEmpty(startTime)){
                schedule.setStartTime(DateUtils.strToLong(startTime));
            }
            if(StringUtils.isNotEmpty(endTime)){
                schedule.setEndTime(DateUtils.strToLong(endTime));
            }
            schedule.setRepeat(repeat);
            schedule.setRemind(remind);
            schedule.setMemberIds(memberIds);
            schedule.setPrivacyPattern(privacy);
            scheduleService.saveSchedule(schedule);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,创建失败:",e);
            throw new AjaxException(e);
        }
        return object;
      }

    /**
     * 删除日程
     * @param scheduleId 日程id
     * @return
     */
    @DeleteMapping("/{scheduleId}")
    public JSONObject deleteSchedule(@PathVariable String scheduleId){
        JSONObject object = new JSONObject();
        try{
            scheduleService.deleteScheduleById(scheduleId);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,删除失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程名称
     * @param scheduleId 日程id
     * @param scheduleName 日程名称
     * @return
     */
    @PatchMapping("/{scheduleId}/schedule_name")
    public JSONObject updateScheduleName(@PathVariable String scheduleId,
                                         @RequestParam String scheduleName){
        JSONObject object = new JSONObject();
        try{
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setScheduleName(scheduleName);
            scheduleService.updateSchedule(schedule);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,名称更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程开始时间
     * @param scheduleId 日程id
     * @param startTime 日程开始时间
     * @return
     */
    @PatchMapping("/{scheduleId}/starttime")
    public JSONObject updateScheduleStartTime(@PathVariable String scheduleId,
                                         @RequestParam String startTime){
        JSONObject object = new JSONObject();
        try{
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setStartTime(DateUtils.strToLong(startTime));
            scheduleService.updateSchedule(schedule);
            object.put("result",1);
            object.put("msg","更新成功");
        }catch(Exception e){
            log.error("系统异常,开始时间更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程结束时间
     * @param scheduleId 日程id
     * @param endtime 日程结束结束
     * @return
     */
    @PatchMapping("/{scheduleId}/endtime")
    public JSONObject updateScheduleEndtime(@PathVariable String scheduleId,
                                              @RequestParam String endtime){
        JSONObject object = new JSONObject();
        try{
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setEndTime(DateUtils.strToLong(endtime));
            scheduleService.updateSchedule(schedule);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,结束时间更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程重复
     * @param scheduleId 日程id
     * @param repeat 日程重复
     * @return
     */
    @PatchMapping("/{scheduleId}/repeat")
    public JSONObject updateRepeat(@PathVariable String scheduleId,
                                   @RequestParam String repeat){
        JSONObject object = new JSONObject();
        try{
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setRepeat(repeat);
            scheduleService.updateSchedule(schedule);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,重复性更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程提醒时间
     * @param scheduleId 日程id
     * @param remind 日程提醒
     * @return
     */
    @PatchMapping("/{scheduleId}/remind")
    public JSONObject upadteRemind(@PathVariable String scheduleId,
                                   @RequestParam String remind){
        JSONObject object = new JSONObject();
        try{

            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setRemind(remind);
            scheduleService.updateSchedule(schedule);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,提醒时间更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程备注
     * @param scheduleId 日程id
     * @param remarks 日程备注
     * @return
     */
    @PatchMapping("/{scheduleId}/remarks")
    public JSONObject updateRemarks(@PathVariable String scheduleId,
                                    @RequestParam String remarks){
        JSONObject object = new JSONObject();
        try{
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setRemarks(remarks);
            scheduleService.updateSchedule(schedule);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,备注更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程地点
     * @param scheduleId 日程id
     * @param address 日程地点
     * @return
     */
    @PatchMapping("/{scheduleId}/address")
    public JSONObject updateAddress(@PathVariable String scheduleId,
                                    @RequestParam String address){
        JSONObject object = new JSONObject();
        try{

            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setAddress(address);
            scheduleService.updateSchedule(schedule);
            object.put("result",1);
            object.put("msg","更新成功");
        }catch(Exception e){
            log.error("系统异常,地点更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程标签
     * @param scheduleId 日程id
     * @param tagIds 日程标签
     * @return
     */
    @PatchMapping("/{scheduleId}/tags")
    public JSONObject updateTags(@PathVariable String scheduleId,
                                    @RequestParam String tagIds){
        JSONObject object = new JSONObject();
        try{

            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setTagId(tagIds);
            scheduleService.updateSchedule(schedule);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,标签更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程参与者
     * @param scheduleId 日程id
     * @param memberIds 日程参与者
     * @return
     */
    @PatchMapping("/{scheduleId}/memberIds")
    public JSONObject updateMembers(@PathVariable String scheduleId,
                                    @RequestParam String memberIds){
        JSONObject object = new JSONObject();
        try{
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setMemberIds(memberIds);
            scheduleService.updateSchedule(schedule);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,参与者更新失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 复制日程
     * @param scheduleId 日程id
     * @param projectId 项目id
     * @return
     */
    @PostMapping("/{scheduleId}/copy")
    public JSONObject copySchedule(@PathVariable String scheduleId,@RequestParam String projectId){
        JSONObject object = new JSONObject();
        try{
            Schedule schedule = scheduleService.findScheduleById(scheduleId);
            schedule.setProjectId(projectId);
            //清空参与者和评论
            scheduleService.saveSchedule(schedule);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,复制失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移动日程
     * @param scheduleId 日程id
     * @param projectId 项目id
     * @return
     */
    @PatchMapping("/{scheduleId}/move")
    public JSONObject moveSchedule(@PathVariable String scheduleId,@RequestParam String projectId){
        JSONObject object = new JSONObject();
        try{
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,移动失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移动日程到回收站
     * @param scheduleId 日程id
     * @return
     */
    @PatchMapping("/{scheduleId}/recyclebin")
    public JSONObject moveToRecycleBin(@PathVariable String scheduleId){
        JSONObject object = new JSONObject();
        try{

            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setIsDel(1);
            scheduleService.updateSchedule(schedule);
            object.put("result",1);
        }catch(Exception e){
            log.error("移入回收站失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程隐私
     * @param scheduleId 日程id
     * @param privacy 日程隐私
     * @return
     */
    @PatchMapping("/{scheduleId}/privacy")
    public JSONObject upadtePrivacy(@PathVariable String scheduleId,@RequestParam Integer privacy){
        JSONObject object = new JSONObject();
        try{
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setPrivacyPattern(privacy);
            scheduleService.updateSchedule(schedule);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return object;
    }
    
    @GetMapping
    public JSONObject schedules(){
        JSONObject object = new JSONObject();
        try{
            object.put("result",1);
            object.put("msg","");
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }
}
