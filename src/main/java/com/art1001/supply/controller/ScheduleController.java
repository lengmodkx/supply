package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.controller.base.BaseController;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.BindingVo;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.relation.GroupVO;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.schedule.ScheduleLogFunction;
import com.art1001.supply.entity.schedule.ScheduleVo;
import com.art1001.supply.entity.task.PushType;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.IdGen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @Resource
    private BindingService bindingService;

    @Resource
    private UserService userService;

    @Resource
    private LogService logService;

    @Resource
    private TagService tagService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    @Resource
    private PublicCollectService publicCollectService;

    @Resource
    private RelationService relationService;


    @RequestMapping("/schedule.html")
    public String schedule(@RequestParam String projectId, String currentGroup, Model model){
        String userId = ShiroAuthenticationManager.getUserId();
        UserEntity userEntity = userService.findById(userId);
        model.addAttribute("currentGroup",currentGroup);
        model.addAttribute("user",userEntity);
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        model.addAttribute("scheduleVo",scheduleService.findScheduleGroupByCreateTime(null,projectId));

        //加载该项目下所有分组的信息
        List<GroupVO> groups = relationService.loadGroupInfo(projectId);
        model.addAttribute("groups",groups);
        return "scheduling";
    }

    /**
     * 在日历上创建日程
     * @param model
     * @return
     */
    @GetMapping("calendarCreateSchedule.html")
    public String calendarCreateSchedule(Model model,String rq){
        model.addAttribute("projecs",projectService.listProjectByUid(ShiroAuthenticationManager.getUserId()));
        model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        model.addAttribute("rq",rq);
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
    public String editSchedule(String projectId, @RequestParam String id,Model model){
        Schedule schedule = scheduleService.findScheduleById(id);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:00");
        //查询出该日程的log信息
        List<Log> logs = logService.initLog(id);
        Collections.reverse(logs);

        //查询该文件有没有被当前用户收藏
        model.addAttribute("isCollect",publicCollectService.isCollItem(id));

        model.addAttribute("logs",logs);
        model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        model.addAttribute("startTime",format.format(schedule.getStartTime()));
        model.addAttribute("startTimeTemp",format.format(schedule.getEndTime()));
        model.addAttribute("endTime",format.format(schedule.getEndTime()));
        model.addAttribute("endTimeTemp",format.format(schedule.getEndTime()));
        model.addAttribute("schedule",schedule);
        //查询出日程的关联信息
        BindingVo bindingVo = bindingService.listBindingInfoByPublicId(id);
        model.addAttribute("bindingVo",bindingVo);
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
                                  Long scheduleCalendar,
                                  @RequestParam(required = false) String memberId){
        JSONObject jsonObject = new JSONObject();
        try {
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            Schedule schedule = new Schedule();
            schedule.setProjectId(projectId);
            schedule.setScheduleName(scheduleName);
            schedule.setRepeat(repeat);
            schedule.setRemind(remand);
            schedule.setScheduleCalendar(scheduleCalendar);
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
                schedule.setScheduleId(IdGen.uuid());
                scheduleService.saveSchedule(schedule);
                jsonObject.put("result",1);
                jsonObject.put("msg","创建成功");
                jsonObject.put("schedule",scheduleService.findScheduleById(schedule.getScheduleId()));
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

    /**
     * 将日程移入回收站
     * @param scheduleId 日程id
     * @param projectId 项目id
     * @return
     */
    @PostMapping("moveToRecycleBin")
    @ResponseBody
    public JSONObject moveToRecycleBin(String scheduleId,String projectId){
        JSONObject jsonObject = new JSONObject();
        JSONObject pushData = new JSONObject();
        try {
            scheduleService.moveToRecycleBin(scheduleId);

            pushData.put("scheduleId",scheduleId);
            pushData.put("type","将日程移入了回收站");
            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(pushData)));
        } catch (Exception e){
            e.printStackTrace();
            log.error("系统异常,操作失败!");
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,操作失败!");
        }
        return jsonObject;
    }

    /**
     * 查询出日程的全部成员信息
     * @param scheduleId 日程id
     * @param projectId 项目id
     * @return
     */
    @RequestMapping("findScheduleMemberInfo")
    public String findScheduleMemberInfo(String scheduleId,String projectId,Model model){
        try {
            Schedule scheduleById = scheduleService.findScheduleById(scheduleId);
            List<UserEntity> joinInfo = userService.findManyUserById(scheduleById.getMemberIds());
            List<UserEntity> projectMembers = userService.findProjectAllMember(projectId);
            projectMembers = projectMembers.stream().filter(item -> !joinInfo.contains(item)).collect(Collectors.toList());
            model.addAttribute("scheduleId",scheduleId);
            model.addAttribute("joinInfo",joinInfo);
            model.addAttribute("projectMembers",projectMembers);
        } catch (Exception e){
            throw new SystemException(e);
        }
        return "tk-schedule-people";
    }

    /**
     * 添加或者移除日程的成员
     * @param scheduleId 日程的id
     * @param addUserEntity 更新完后的成员id
     * @return
     */
    @PostMapping("addAndRemoveScheduleMember")
    @ResponseBody
    public JSONObject addAndRemoveScheduleMember(String scheduleId, String addUserEntity){
        JSONObject jsonObject = new JSONObject();
        try {
            scheduleService.addAndRemoveScheduleMember(scheduleId, addUserEntity);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,{}",e);
            jsonObject.put("msg","系统异常!");
            jsonObject.put("result",0);
        }
        return jsonObject;
    }

    /**
     * 日程的评论
     * @param publicId 日程的id
     * @param content 文件的 评论 内容
     * @return
     */
    @PostMapping("chat")
    @ResponseBody
    public JSONObject chat(String publicId,String content){
        JSONObject jsonObject = new JSONObject();
        try {
            Log log = new Log();
            log.setId(IdGen.uuid());
            log.setContent(ShiroAuthenticationManager.getUserEntity().getUserName()+" 说: "+ content);
            log.setLogType(1);
            log.setMemberId(ShiroAuthenticationManager.getUserId());
            log.setPublicId(publicId);
            log.setLogFlag(3);
            log.setCreateTime(System.currentTimeMillis());
            Log log1 = logService.saveLog(log);
            jsonObject.put("result",1);
            //推送数据
            PushType taskPushType = new PushType(TaskLogFunction.A14.getName());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("scheduleLog",log1);
            taskPushType.setObject(map);
            //推送至日程的详情界面
            messagingTemplate.convertAndSend("/topic/"+publicId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            jsonObject.put("result",0);
            log.error("系统异常,发送失败,{}",e);
        }
        return jsonObject;
    }

    /**
     * 更新了日程的名称 并且推送
     * @param scheduleId 日程的id
     * @param scheduleName 日程的标题
     * @return
     */
    @PostMapping("updateScheduleName")
    @ResponseBody
    public JSONObject updateScheduleName(String scheduleId, String scheduleName){
        JSONObject jsonObject = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setScheduleName(scheduleName);
            schedule.setUpdateTime(System.currentTimeMillis());
            Log log = scheduleService.updateSchedule(schedule);
            PushType taskPushType = new PushType();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("name",scheduleName);
            map.put("type",ScheduleLogFunction.D.getId());
            map.put("scheduleLog",log);
            taskPushType.setObject(map);
            //推送至日程的详情界面
            messagingTemplate.convertAndSend("/topic/"+scheduleId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("系统异常,更新失败!");
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,更新失败!");
        }
        return jsonObject;
    }

    /**
     * 更新日程( 开始 / 结束 ) 时间
     * @param scheduleId 日程的id
     * @param startTime 日程的开始时间
     * @param endTime 日程的结束时间
     * @return
     */
    @PostMapping("updateScheduleStartAndEndTime")
    @ResponseBody
    public JSONObject updateScheduleStartAndEndTime(String scheduleId, String startTime, String endTime){
        JSONObject jsonObject = new JSONObject();
        try {
            //更新日程时间信息
            Log log = scheduleService.updateScheduleStartAndEndTime(scheduleId,startTime,endTime);

            //包装推送数据
            PushType taskPushType = new PushType();
            Map<String,Object> map = new HashMap<String,Object>();
            if(!StringUtils.isEmpty(startTime)){
                map.put("type",ScheduleLogFunction.B.getId());
                map.put("startTime",startTime);
            } else{
                map.put("type",ScheduleLogFunction.C.getId());
                map.put("endTime",endTime);
            }
            map.put("scheduleLog",log);
            taskPushType.setObject(map);
            //推送至日程的详情界面
            messagingTemplate.convertAndSend("/topic/"+scheduleId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("任务时间信息更新失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新日程的重复规则
     * @param scheduleId 日程的id
     * @param repeat 日程的重复规则
     * @return
     */
    @PostMapping("updateScheduleRepeat")
    @ResponseBody
    public JSONObject updateScheduleRepeat(String scheduleId, String repeat){
        JSONObject jsonObject = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setRepeat(repeat);
            Log log = scheduleService.updateSchedule(schedule);
            jsonObject.put("result",1);
            //包装推送数据
            PushType taskPushType = new PushType();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("repeat",repeat);
            map.put("type",ScheduleLogFunction.E.getId());
            map.put("scheduleLog",log);
            taskPushType.setObject(map);
            //推送至日程的详情界面
            messagingTemplate.convertAndSend("/topic/"+scheduleId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            log.error("系统异常,操作失败!");
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,操作失败!");
        }
        return jsonObject;
    }

    /**
     * 更新日程的提醒模式
     * @param scheduleId 日程的id
     * @param remind 提醒模式
     * @return
     */
    @PostMapping("updateScheduleRemind")
    @ResponseBody
    public JSONObject updateScheduleRemind(String scheduleId, String remind){
        JSONObject jsonObject = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setRemind(remind);
            Log log = scheduleService.updateSchedule(schedule);
            //包装推送数据
            PushType taskPushType = new PushType();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("remind",remind);
            map.put("type",ScheduleLogFunction.F.getId());
            map.put("scheduleLog",log);
            taskPushType.setObject(map);
            //推送至日程的详情界面
            messagingTemplate.convertAndSend("/topic/"+scheduleId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            e.printStackTrace();
            log.error("系统异常,请重试!");
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,操作失败!");
        }
        return jsonObject;
    }

    /**
     * 更新日程的地址
     * @param scheduleId 日程id
     * @param address 地址
     * @return
     */
    @PostMapping("updateScheduleAddress")
    @ResponseBody
    public JSONObject updateScheduleAddress(String scheduleId, String address){
        JSONObject jsonObject = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setAddress(address);
            Log log = scheduleService.updateSchedule(schedule);
            //包装推送数据
            PushType taskPushType = new PushType();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("address",address);
            map.put("type",ScheduleLogFunction.G.getId());
            map.put("scheduleLog",log);
            taskPushType.setObject(map);
            //推送至日程的详情界面
            messagingTemplate.convertAndSend("/topic/"+scheduleId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            e.printStackTrace();
            log.error("系统异常,请重试!");
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,操作失败!");
        }
        return jsonObject;
    }

    /**
     * 是否是全天
     * @param scheduleId 日程id
     * @param isAllday 是否是全天 (是:true 否:false)
     * @return
     */
    @PostMapping("isAllday")
    @ResponseBody
    public JSONObject isAllday(String scheduleId, boolean isAllday){
        JSONObject jsonObject = new JSONObject();
        try {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            Log log = scheduleService.updateSchedule(schedule);
            //包装推送数据
            PushType taskPushType = new PushType();
            Map<String,Object> map = new HashMap<String,Object>();
            if(!isAllday){
                Schedule scheduleById = scheduleService.findScheduleById(scheduleId);
                map.put("startTime",scheduleById.getStartTime());
                map.put("endTime",scheduleById.getEndTime());
            }
            map.put("type",ScheduleLogFunction.K.getId());
            map.put("isAllday",isAllday);
            map.put("scheduleLog",log);
            taskPushType.setObject(map);
            //推送至日程的详情界面
            messagingTemplate.convertAndSend("/topic/"+scheduleId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            e.printStackTrace();
            log.error("系统异常,请重试!");
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,操作失败!");
        }
        return jsonObject;
    }

    /**
     * 查询出未来的日程
     * @param lable 标识是查询为来的日程还是 过去的日程 (1:未来 0:过去)
     * @return
     */
    @PostMapping("afterSchedule")
    @ResponseBody
    public  JSONObject  afterSchedule(int lable){
        JSONObject jsonObject = new JSONObject();
        try {
            List<ScheduleVo> scheduleList = scheduleService.afterSchedule(lable);
            jsonObject.put("scheduleList",scheduleList);
        } catch (Exception e){
            e.printStackTrace();
            log.error("系统异常,请重试!");
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,数据拉取失败!");
        }
        return jsonObject;
    }

    /**
     * 永久的删除日程
     * @param id 日程id
     * @return
     */
    @PostMapping("/deleteSchedule")
    @ResponseBody
    public JSONObject deleteSchedule(String id,String projectId){
        JSONObject jsonObject = new JSONObject();
        JSONObject pushData = new JSONObject();
        try {
            scheduleService.deleteScheduleById(id);
            pushData.put("id",id);
            pushData.put("type","删除回收站信息");
            messagingTemplate.convertAndSend("/topic/"+projectId+"recycleBin",new ServerMessage(JSON.toJSONString(pushData)));
        } catch (Exception e){
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,操作失败!");
            log.error("系统异常,操作失败!");
        }
        return jsonObject;
    }

    /**
     * 恢复
     * @param scheduleId 日程id
     * @param projectId 项目
     * @return
     */
    @PostMapping("recoverySchedule")
    @ResponseBody
    public JSONObject recoverySchedule(String scheduleId,String projectId){
        JSONObject jsonObject = new JSONObject();
        JSONObject pushData = new JSONObject();
        try {
            scheduleService.recoverySchedule(scheduleId);
            jsonObject.put("result",1);
            Schedule scheduleById = scheduleService.findScheduleById(scheduleId);
            pushData.put("schedule",scheduleById);
            pushData.put("date",DateUtils.getDateStr(new Date(scheduleById.getCreateTime()),"yyyy-MM"));
            pushData.put("type","恢复了日程");

            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(pushData)));
            pushData.put("type","恢复了信息");
            pushData.put("id",scheduleId);
            pushData.remove("schedule");
            messagingTemplate.convertAndSend("/topic/"+projectId+"recycleBin",new ServerMessage(JSON.toJSONString(pushData)));
        } catch (Exception e){
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,操作失败!");
            log.error("系统异常,操作失败!");
        }
        return jsonObject;
    }
}
