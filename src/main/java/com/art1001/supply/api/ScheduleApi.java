package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
              throw new AjaxException(e);
          }
          return object;
      }


}
