package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.controller.base.BaseController;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("schedule")
public class ScheduleController extends BaseController {
    @Resource
    private ProjectService projectService;

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private ProjectMemberService projectMemberService;


    @RequestMapping("/schedule.html")
    public String schedule(@RequestParam String projectId, Model model){

        model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        model.addAttribute("scheduleVo",scheduleService.findScheduleGroupByCreateTime(null,projectId));
        return "scheduling";
    }

    /**
     * 在日历上创建日程
     * @param model
     * @return
     */
    @GetMapping("calendarCreateSchedule.html")
    public String calendarCreateSchedule(Model model){
        model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        return "tk-calendar-create-rc";
    }



    @RequestMapping("/addschedule.html")
    public String addschedule(@RequestParam String projectId, Model model){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:00");
        model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        model.addAttribute("startTime",format.format(System.currentTimeMillis()));
        model.addAttribute("startTimeTemp",System.currentTimeMillis());
        model.addAttribute("endTime",format.format(System.currentTimeMillis()+60*60*1000));
        model.addAttribute("endTimeTemp",System.currentTimeMillis()+60*60*1000);

        return "tk-add-calendar";
    }


    @RequestMapping("/editSchedule.html")
    public String editSchedule(@RequestParam String projectId, @RequestParam String id,Model model){
        Schedule schedule = scheduleService.findScheduleById(id);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:00");
        model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        model.addAttribute("startTime",format.format(schedule.getStartTime()));
        model.addAttribute("startTimeTemp",format.format(schedule.getEndTime()));
        model.addAttribute("endTime",format.format(schedule.getEndTime()));
        model.addAttribute("endTimeTemp",format.format(schedule.getEndTime()));
        model.addAttribute("schedule",schedule);
        return "tk-edit-schedule";
    }



    @RequestMapping("/searchPeople.html")
    public String searchPeople(@RequestParam String projectId, Model model){

        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        model.addAttribute("user",userEntity);
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(projectId);
        List<ProjectMember> memberAllList = projectMemberService.findProjectMemberAllList(projectMember);
        memberAllList = memberAllList.stream().filter(projectMember1->!userEntity.getId().equals(projectMember1.getMemberId())).collect(Collectors.toList());
        model.addAttribute("members",memberAllList);

        return "tk-search-people";
    }

    @PostMapping("/addschedule")
    @ResponseBody
    public JSONObject addschedule(@RequestParam(required = false) String scheduleId,
                                  @RequestParam String projectId,
                                  @RequestParam String scheduleName,
                                  @RequestParam String repeat,
                                  @RequestParam String remand,
                                  @RequestParam(required = false) String allDay,
                                  @RequestParam(required = false) String allPeopleDay,
                                  @RequestParam String startTimeTemp,
                                  @RequestParam String endTimeTemp,
                                  @RequestParam(required = false) String memberId){
        JSONObject jsonObject = new JSONObject();
        try {
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            Schedule schedule = new Schedule();
            schedule.setProjectId(projectId);
            schedule.setScheduleName(scheduleName);
            schedule.setRepeat(repeat);
            schedule.setRemind(remand);
            schedule.setMemberId(userEntity.getId());
            schedule.setMemberIds(memberId);
            if("on".equals(allDay)){
                schedule.setStartTime(System.currentTimeMillis());
                schedule.setEndTime(DateUtils.getEndTime());
            }else{
                schedule.setStartTime(Long.valueOf(startTimeTemp));
                schedule.setEndTime(Long.valueOf(endTimeTemp));
            }

            schedule.setCreateTime(System.currentTimeMillis());
            schedule.setUpdateTime(System.currentTimeMillis());
            if(StringUtils.isNotEmpty(scheduleId)){
                schedule.setScheduleId(scheduleId);
                scheduleService.updateSchedule(schedule);
                jsonObject.put("result",1);
                jsonObject.put("msg","更新成功");
            }else{
                scheduleService.saveSchedule(schedule);
                jsonObject.put("result",1);
                jsonObject.put("msg","创建成功");
            }

        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    @PostMapping("scheduleList")
    @ResponseBody
    public JSONObject scheduleList(String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            Map<String,List> map = new HashMap<String,List>();
            //查询出未来的日程
            map.put("data",scheduleService.findBeforeSchedule(System.currentTimeMillis(),projectId,0));
            //查询出过去的日程
            map.put("before",scheduleService.findBeforeSchedule(System.currentTimeMillis(),projectId,1));
            jsonObject.put("data",map);
        } catch (Exception e){
            jsonObject.put("result",0);
            log.error("系统异常,数据拉取失败,{}",e);
        }
        return jsonObject;
    }

    /**
     * 单独移除某个参与者
     * @param scheduleId
     * @param uId
     * @return
     */
    @PostMapping("removeScheduleMember")
    @ResponseBody
    public JSONObject removeScheduleMember(String scheduleId,String uId){
        JSONObject jsonObject = new JSONObject();
        try {
            Schedule schedule = scheduleService.findScheduleById(scheduleId);
            Arrays.asList(schedule.getMemberIds().split(",")).remove(uId);
            scheduleService.updateSchedule(schedule);
            jsonObject.put("result",1);
            jsonObject.put("msg","移除成功");

        } catch (Exception e){
            jsonObject.put("result",0);
        }
        return jsonObject;
    }








}
