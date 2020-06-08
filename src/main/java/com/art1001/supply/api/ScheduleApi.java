package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.fabulous.Fabulous;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.schedule.ScheduleListVO;
import com.art1001.supply.entity.schedule.ScheduleVo;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.fabulous.FabulousService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日程
 *
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
public class ScheduleApi extends BaseController {

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private PublicCollectService publicCollectService;

    @Resource
    private FabulousService fabulousService;

    @Resource
    private BindingService bindingService;

    @Resource
    private LogService logService;

    @Resource
    private TaskService taskService;

    /**
     * 点击日程菜单栏时页面初始化
     */
    @GetMapping
    public JSONObject initSchedule(@RequestParam("projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("after", scheduleService.findScheduleListByProjectId(projectId));
            jsonObject.put("before", scheduleService.findScheduleGroup(projectId));
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取选中成员在当前时间范围中是否合法
     *
     * @param userId    当前用户id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 冲突的日程集合
     */
    @GetMapping("check_time")
    public Object testMemberTimeRange(String userId, Long startTime, Long endTime) {
        try {
            return success(scheduleService.testMemberTimeRange(userId, startTime, endTime));
        } catch (Exception e) {
            throw new AjaxException("系统异常,成员时间范围合法性检测失败!");
        }
    }


    /**
     * 创建日程
     *
     * @param projectId    项目id
     * @param scheduleName 日程名称
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param repeat       是否重复
     * @param remind       是否提醒
     * @param memberIds    参与者
     * @param privacy      隐私
     * @return
     */
    @Push(value = PushType.D9, type = 1)
    @PostMapping
    public JSONObject addSchedule(@RequestParam String projectId,
                                  @RequestParam String scheduleName,
                                  @RequestParam(required = false) Long startTime,
                                  @RequestParam(required = false) Long endTime,
                                  @RequestParam(defaultValue = "不重复", required = false) String repeat,
                                  @RequestParam(defaultValue = "不提醒", required = false) String remind,
                                  @RequestParam(value = "memberIds", required = false) String memberIds,
                                  @RequestParam(defaultValue = "0", required = false) Integer privacy,
                                  @RequestParam(defaultValue = "0", required = false) Integer isAllday) {
        JSONObject object = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setProjectId(projectId);
            schedule.setScheduleName(scheduleName);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setIsAllday(isAllday);
            schedule.setRepeat(repeat);
            schedule.setRemind(remind);
            schedule.setMemberIds(memberIds);
            schedule.setPrivacyPattern(privacy);
            scheduleService.saveSchedule(schedule);
            object.put("msgId", projectId);
            object.put("data", projectId);
            object.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常,创建失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }


    /**
     * 根据id获取日程信息
     *
     * @return
     */
    @GetMapping("/{scheduleId}")
    public JSONObject getSchedule(@PathVariable("scheduleId") String scheduleId) {
        JSONObject object = new JSONObject();
        try {
            Schedule scheduleById = scheduleService.findScheduleById(scheduleId);
            //当前用户是否收藏了该日程
            scheduleById.setIsCollect(publicCollectService.count(new QueryWrapper<PublicCollect>().eq("public_id", scheduleId).eq("member_id", ShiroAuthenticationManager.getUserId())) > 0);
            //当前用户是否对该日程点过赞
            scheduleById.setIsFabulous(fabulousService.count(new QueryWrapper<Fabulous>().eq("member_id", ShiroAuthenticationManager.getUserId()).eq("public_id", scheduleId)) > 0);
//            //该日程的关联信息
//            scheduleById.setBindings(bindingService.list(new QueryWrapper<Binding>().eq("public_id", scheduleId)));
            //设置关联信息
            bindingService.setBindingInfo(scheduleId, null, null, null, scheduleById);
            //查询出该任务的日志信息
            scheduleById.setLogs(logService.initLog(scheduleId));
            //获取日程的未读消息数
            int unMsgCount = logService.count(new QueryWrapper<Log>().eq("public_id", scheduleId)) - 10;
            scheduleById.setUnReadMsg(unMsgCount > 0 ? unMsgCount : 0);
            object.put("data", scheduleById);
            object.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常,名称更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 删除日程
     *
     * @param scheduleId 日程id
     * @return
     */
    @DeleteMapping("/{scheduleId}")
    public JSONObject deleteSchedule(@PathVariable String scheduleId) {
        JSONObject object = new JSONObject();
        try {
            scheduleService.deleteScheduleById(scheduleId);
            object.put("result", 1);
            object.put("msg", "删除成功!");
        } catch (Exception e) {
            log.error("系统异常,删除失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程名称
     *
     * @param scheduleId   日程id
     * @param scheduleName 日程名称
     * @return
     */
    @Push(value = PushType.D1, type = 1)
    @PutMapping("/{scheduleId}/schedule_name")
    public JSONObject updateScheduleName(@PathVariable String scheduleId,
                                         @RequestParam String scheduleName) {
        JSONObject object = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setScheduleName(scheduleName);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleService.updateById(schedule);
            object.put("result", 1);
            object.put("msgId", scheduleService.getProjectId(scheduleId));
            object.put("data", scheduleId);
            object.put("msg", "更新成功!");
            object.put("id", scheduleId);
        } catch (ServiceException e) {
            throw new AjaxException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("系统异常,名称更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程开始时间
     *
     * @param scheduleId 日程id
     * @param startTime  日程开始时间
     * @return
     */
    @Push(value = PushType.D2, type = 1)
    @PutMapping("/{scheduleId}/starttime")
    public JSONObject updateScheduleStartTime(@PathVariable String scheduleId,
                                              @RequestParam Long startTime) {
        JSONObject object = new JSONObject();
        try {

            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setStartTime(startTime);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleService.updateById(schedule);
            object.put("msgId", scheduleService.getProjectId(scheduleId));
            object.put("data", scheduleId);
            object.put("result", 1);
            object.put("msg", "更新成功");
        } catch (Exception e) {
            log.error("系统异常,开始时间更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程结束时间
     *
     * @param scheduleId 日程id
     * @param endTime    日程结束结束
     * @return
     */
    @Push(value = PushType.D3, type = 1)
    @PutMapping("/{scheduleId}/endtime")
    public JSONObject updateScheduleEndtime(@PathVariable String scheduleId,
                                            @RequestParam Long endTime) {
        JSONObject object = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setEndTime(endTime);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleService.updateById(schedule);
            object.put("result", 1);
            object.put("msg", "更新成功!");
            object.put("msgId", scheduleService.getProjectId(scheduleId));
            object.put("data", scheduleId);
        } catch (Exception e) {
            log.error("系统异常,结束时间更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程重复
     *
     * @param scheduleId 日程id
     * @param repeat     日程重复
     * @return
     */
    @Push(value = PushType.D7, type = 1)
    @PutMapping("/{scheduleId}/repeat")
    public JSONObject updateRepeat(@PathVariable String scheduleId,
                                   @RequestParam String repeat) {
        JSONObject object = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setRepeat(repeat);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleService.updateById(schedule);
            object.put("result", 1);
            object.put("msgId", scheduleService.getProjectId(scheduleId));
            object.put("data", scheduleId);
            object.put("msg", "更新成功!");
        } catch (Exception e) {
            log.error("系统异常,重复性更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程提醒时间
     *
     * @param scheduleId 日程id
     * @param remind     日程提醒
     * @return
     */
    @PutMapping("/{scheduleId}/remind")
    public JSONObject upadteRemind(@PathVariable String scheduleId,
                                   @RequestParam String remind) {
        JSONObject object = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setRemind(remind);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleService.updateById(schedule);
            object.put("result", 1);
            object.put("msg", "更新成功!");
        } catch (Exception e) {
            log.error("系统异常,提醒时间更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程备注
     *
     * @param scheduleId 日程id
     * @param remarks    日程备注
     * @return
     */
    @Push(value = PushType.D5, type = 1)
    @PutMapping("/{scheduleId}/remarks")
    public JSONObject updateRemarks(@PathVariable String scheduleId,
                                    @RequestParam String remarks) {
        JSONObject object = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setRemarks(remarks);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleService.updateById(schedule);
            object.put("result", 1);
            object.put("msgId", scheduleService.getProjectId(scheduleId));
            object.put("data", scheduleId);
            object.put("msg", "更新成功!");
        } catch (Exception e) {
            log.error("系统异常,备注更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程地点
     *
     * @param scheduleId 日程id
     * @param address    日程地点
     * @return
     */
    @Push(type = 1, value = PushType.D4)
    @PutMapping("/{scheduleId}/address")
    public JSONObject updateAddress(@PathVariable String scheduleId,
                                    @RequestParam String address) {
        JSONObject object = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setAddress(address);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleService.updateById(schedule);
            object.put("result", 1);
            object.put("msgId", scheduleService.getProjectId(scheduleId));
            object.put("data", scheduleId);
            object.put("msg", "更新成功!");
        } catch (Exception e) {
            log.error("系统异常,地点更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程参与者
     *
     * @param scheduleId 日程id
     * @param memberIds  日程参与者
     * @return
     */
    @Push(value = PushType.D6, type = 1)
    @PutMapping("/{scheduleId}/memberIds")
    public JSONObject updateMembers(@PathVariable String scheduleId,
                                    @RequestParam String memberIds) {
        JSONObject object = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setMemberIds(memberIds);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleService.updateById(schedule);
            object.put("result", 1);
            object.put("msgId", scheduleService.getProjectId(scheduleId));
            object.put("data", scheduleId);
            object.put("msg", "更新成功!");
        } catch (Exception e) {
            log.error("系统异常,参与者更新失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 复制日程
     *
     * @param scheduleId 日程id
     * @param projectId  项目id
     * @return
     */
    @Push(type = 1, value = PushType.D10)
    @PostMapping("/{scheduleId}/copy")
    public JSONObject copySchedule(@PathVariable String scheduleId, @RequestParam String projectId) {
        JSONObject object = new JSONObject();
        try {
            Schedule schedule = scheduleService.findScheduleById(scheduleId);
            schedule.setProjectId(projectId);
            //清空参与者和评论
            scheduleService.saveSchedule(schedule);
            object.put("msgId", projectId);
            object.put("result", 1);
            object.put("data", projectId);
        } catch (Exception e) {
            log.error("系统异常,复制失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移动日程
     *
     * @param scheduleId 日程id
     * @param projectId  项目id
     * @return
     */
    @Push(type = 2, value = PushType.D11)
    @PutMapping("/{scheduleId}/move")
    public JSONObject moveSchedule(@PathVariable String scheduleId, @RequestParam String projectId) {
        JSONObject object = new JSONObject();
        try {
            String pId = scheduleService.getProjectId(scheduleId);
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setProjectId(projectId);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleService.updateById(schedule);
            Map<String, Object> maps = new HashMap<String, Object>(2);
            if (projectId.equals(pId)) {
                maps.put(projectId, projectId);
            } else {
                maps.put(projectId, projectId);
                maps.put(pId, pId);
            }
            object.put("data", maps);
            object.put("msg", "移动成功!");
            object.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常,移动失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移动日程到回收站
     *
     * @param scheduleId 日程id
     * @return
     */
    @Push(value = PushType.D12, type = 1)
    @PutMapping("/{scheduleId}/recyclebin")
    public JSONObject moveToRecycleBin(@PathVariable String scheduleId) {
        JSONObject object = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setIsDel(1);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleService.updateById(schedule);
            String projectId = scheduleService.getProjectId(scheduleId);
            object.put("msgId", projectId);
            object.put("data", projectId);
            object.put("msg", "成功移至回收站!");
            object.put("result", 1);
        } catch (Exception e) {
            log.error("移入回收站失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新日程隐私
     *
     * @param scheduleId 日程id
     * @param privacy    日程隐私
     * @return 是否成功
     */
    @Push(type = 1, value = PushType.D13)
    @PutMapping("/{scheduleId}/privacy")
    public JSONObject upadtePrivacy(@PathVariable String scheduleId, @RequestParam Integer privacy) {
        JSONObject object = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setPrivacyPattern(privacy);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleService.updateById(schedule);
            String projectId = scheduleService.getProjectId(scheduleId);
            object.put("msgId", projectId);
            object.put("data", projectId);
            object.put("msg", "更新成功!");
            object.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 获取日程信息 (用于绑定处)
     * 该日程信息仅包括 scheduleName,startTime,endTime,id
     *
     * @param projectId 项目id
     * @return 日程信息
     */
    @GetMapping("/{projectId}/bind")
    public JSONObject getBindInfo(@PathVariable String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<ScheduleVo> beforeByMonth = scheduleService.getBeforeByMonth(projectId);
            jsonObject.put("data", new JSONObject().fluentPut("before", beforeByMonth).fluentPut("after", scheduleService.getAfterBind(projectId)).fluentPut("beforeCount", beforeByMonth.stream().mapToInt(b -> b.getScheduleList().size()).sum()));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,获取绑定信息失败!", e);
        }
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param: projectId 项目id
     * @Param: memberId 项目id
     * @return:
     * @Description: 获取日程列表
     * @create: 15:19 2020/5/6
     */
    @GetMapping("/getScheduleList/{projectId}/{memberId}")
    public JSONObject getScheduleList(@PathVariable String projectId, @PathVariable String memberId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", scheduleService.getScheduleList(projectId, memberId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }

    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param:
     * @return:
     * @Description: 我创建的会议
     * @create: 10:09 2020/6/5
     */
    @GetMapping("/MyCreateShedules/{projectId}")
    public JSONObject MyCreateShedules(@PathVariable String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", scheduleService.getCreatedSchedules(projectId));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
    * @Author: 邓凯欣
    * @Email：dengkaixin@art1001.com
    * @Param:
    * @return:
    * @Description: 我参加的会议
    * @create: 11:34 2020/6/5
    */
    @GetMapping("/MyJoinMeetings/{projectId}")
    public JSONObject MyJoinMeetings(@PathVariable String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", scheduleService.getJoinMeetings(projectId));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }
}
