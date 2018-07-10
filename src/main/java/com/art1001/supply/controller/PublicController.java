package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/public")
public class PublicController {

    /** 任务逻辑层接口  */
    @Resource
    private TaskService taskService;

    /** 日程的逻辑层接口 */
    @Resource
    private ScheduleService scheduleService;

    /** 项目的逻辑层接口 */
    @Resource
    private ProjectService projectService;

    @GetMapping("mypage.html")
    public String my(Model model){
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        List<Task> taskList = taskService.findTaskByUserIdAndByTreeDay(userEntity.getId());
        List<Schedule> scheduleList = scheduleService.findScheduleByUserIdAndByTreeDay(userEntity.getId());
        model.addAttribute("taskList",taskList);
        model.addAttribute("scheduleList",scheduleList);
        return "mypage";
    }

    @GetMapping("calendar.html")
    public String calendar(){
        return "tk-calendar";
    }


    /**
     * 我创建的任务
     * @return
     */
    @PostMapping("/myAddTask")
    public JSONObject myAddTask(String status,String orderType){
        JSONObject jsonObject = new JSONObject();
        List<Task> taskList = new ArrayList<Task>();
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        try {
            if(status.equals("完成")){
                taskList = taskService.findTaskByCreateMemberByStatus(userEntity.getId(),status);
                jsonObject.put("result",1);
                jsonObject.put("msg","获取成功");
                jsonObject.put("data",taskList);
                return jsonObject;
            }
            //如果按照项目名查询需要查询出所有项目和所有项目下的任务信息
            if(orderType.equals("4")){
                List<Project> projectList = projectService.findProjectAndTaskByCreateMember(userEntity.getId());
                jsonObject.put("result",1);
                jsonObject.put("msg","获取成功!");
                jsonObject.put("data",taskList);
            } else{
                //1.按照优先级排序 2.按照截止时间排序 3.按照创建时间排序
                if(orderType.equals("1")){
                    //查询出所有我创建的任务 (不查询项目信息)
                    taskList = taskService.findTaskByCreateMember(userEntity.getId());
                    //排序任务
                    taskList = orderTask(taskList);
                } else{
                    taskList = taskService.findTaskByCreateMemberAndTime(userEntity.getId(),orderType);
                }
                jsonObject.put("result",1);
                jsonObject.put("msg","获取成功");
                jsonObject.put("data",taskList);
            }
            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
            jsonObject.put("data",taskList);
        }catch (Exception e){
            throw new AjaxException(e);
        }


        return jsonObject;
    }

    /**
     * 我参与的任务
     * @return
     */
    @PostMapping("/myJoinTask")
    public JSONObject myJoinTask(String status,String orderType){

        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        List<Task> taskList = new ArrayList<Task>();
        try {
            if(status.equals("完成")){
                taskList = taskService.findTaskByUserIdByStatus(userEntity.getId(),status);
                jsonObject.put("result",1);
                jsonObject.put("msg","获取成功");
                jsonObject.put("data",taskList);
                return jsonObject;
            }
            //如果按照项目名查询需要查询出所有项目和所有项目下的任务信息
            if(orderType.equals("4")){
                List<Project> projectList = projectService.findProjectAndTaskByUserId(userEntity.getId());
                jsonObject.put("result",1);
                jsonObject.put("msg","获取成功!");
                jsonObject.put("data",taskList);
            } else{
                //1.按照优先级排序 2.按照截止时间排序 3.按照创建时间排序
                if(orderType.equals("1")){
                    //查询出所有我参与的任务 (不查询项目信息)
                    taskList = taskService.findTaskByUserId(userEntity.getId());
                    //排序任务
                    taskList = orderTask(taskList);
                } else{
                    taskList = taskService.findTaskByUserAndTime(userEntity.getId(),orderType);
                }
                jsonObject.put("result",1);
                jsonObject.put("msg","获取成功");
                jsonObject.put("data",taskList);
            }
            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
            jsonObject.put("data",taskList);
        }catch (Exception e){
            log.error("系统异常,数据拉取失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 我执行的任务
     * @return
     */
    @PostMapping("/myExecutorTask")
    public JSONObject myExecutorTask(String status,String orderType){
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        List<Task> taskList = new ArrayList<Task>();
        try {
            if(status.equals("完成")){
                taskList = taskService.findTaskByExecutorAndStatus(userEntity.getId());
                jsonObject.put("result",1);
                jsonObject.put("msg","获取成功");
                jsonObject.put("data",taskList);
                return jsonObject;
            }
            //如果按照项目名查询需要查询出所有项目和所有项目下的任务信息
            if(orderType.equals("4")){
                List<Project> projectList = projectService.findProjectAndTaskByUserId(userEntity.getId());
                jsonObject.put("result",1);
                jsonObject.put("msg","获取成功!");
                jsonObject.put("data",taskList);
            } else{
                //1.按照优先级排序 2.按照截止时间排序 3.按照创建时间排序
                if(orderType.equals("1")){
                    //查询出所有我执行的任务 (不查询项目信息)
                    taskList = taskService.findTaskByExecutor(userEntity.getId());
                    //排序任务
                    taskList = orderTask(taskList);
                } else{
                    taskList = taskService.findTaskByExecutorIdAndTime(userEntity.getId(),orderType);
                }
                jsonObject.put("result",1);
                jsonObject.put("msg","获取成功");
                jsonObject.put("data",taskList);
            }
        }catch (Exception e){
            log.error("系统异常,数据拉取失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 按照任务的优先级排序任务
     * @param taskList
     * @return
     */
    public List<Task> orderTask(List<Task> taskList){
        for (int i = taskList.size()-1;i>0;i--) {
            for (int j = taskList.size() - 1; j > 0; j--) {
                if (taskList.get(j).getPriority().equals("非常紧急")) {
                    Task task = taskList.get(j - 1);
                    taskList.set(j - 1, taskList.get(i));
                    taskList.set(j, task);
                }
                if (taskList.get(j).getPriority().equals("紧急")) {
                    if (taskList.get(j - 1).getPriority().equals("普通")) {
                        Task task = taskList.get(j - 1);
                        taskList.set(j - 1, taskList.get(j));
                        taskList.set(j, task);
                    }
                }
            }
        }
        return taskList;
    }


}
