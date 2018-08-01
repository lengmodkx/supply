package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.collect.PublicCollectVO;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskCollect;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.task.TaskCollectService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import io.netty.handler.codec.json.JsonObjectDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

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

    /** 任务收藏的逻辑层接口 */
    @Resource
    private PublicCollectService publicCollectService;

    @Resource
    private UserService userService;
    /**
     * 所有收藏的常量
     */
    static final String allCollect = "所有收藏";

    @GetMapping("mypage.html")
    public String my(Model model){
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        List<Task> taskList = taskService.findTaskByUserIdAndByTreeDay(userEntity.getId());
        List<Schedule> scheduleList = scheduleService.findScheduleByUserIdAndByTreeDay(userEntity.getId());
        model.addAttribute("taskList",taskList);
        model.addAttribute("scheduleList",scheduleList);
        return "mypage";
    }

    /**
     * 跳转到日历界面
     * @return
     */
    @GetMapping("calendar.html")
    public String calendar(){
        return "tk-calendar";
    }

    /**
     * 获取日历上的任务数据
     * @return
     */
    @PostMapping("taskCalendar")
    @ResponseBody
    public JSONObject taskCalendar(){
        JSONObject jsonObject = new JSONObject();
        try {
            String uId = ShiroAuthenticationManager.getUserId();
            List<Task> taskList = taskService.findTaskByCalendar(uId);
            jsonObject.put("data",taskList);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,数据拉取失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * 我创建的任务
     * @return
     */
    @PostMapping("/myAddTask")
    @ResponseBody
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
                jsonObject.put("orderType","4");
                jsonObject.put("result",1);
                jsonObject.put("msg","获取成功!");
                jsonObject.put("data",projectList);
                return jsonObject;
            } else{
                //1.按照优先级排序 2.按照截止时间排序 3.按照创建时间排序
                if(orderType.equals("1")){
                    //查询出所有我创建的任务 (不查询项目信息)
                    taskList = taskService.findTaskByCreateMember(userEntity.getId(),null);
                    //排序任务
                    orderTask(taskList);
                } else{
                    taskList = taskService.findTaskByCreateMember(userEntity.getId(),orderType);
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
    @ResponseBody
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
                jsonObject.put("orderType","4");
                jsonObject.put("result",1);
                jsonObject.put("msg","获取成功!");
                jsonObject.put("data",projectList);
                return jsonObject;
            } else{
                //1.按照优先级排序 2.按照截止时间排序 3.按照创建时间排序
                if(orderType.equals("1")){
                    //查询出所有我参与的任务 (不查询项目信息)
                    taskList = taskService.findTaskByUserId(userEntity.getId(),null);
                    //排序任务
                    orderTask(taskList);
                } else{
                    taskList = taskService.findTaskByUserId(userEntity.getId(),orderType);
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
    @ResponseBody
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
                List<Project> projectList = projectService.findProjectAndTaskByExecutorId(userEntity.getId());
                jsonObject.put("result",1);
                jsonObject.put("orderType","4");
                jsonObject.put("msg","获取成功!");
                jsonObject.put("data",projectList);
                return jsonObject;
            } else{
                //1.按照优先级排序 2.按照截止时间排序 3.按照创建时间排序
                if(orderType.equals("1")){
                    //查询出所有我执行的任务
                    taskList = taskService.findTaskByExecutor(userEntity.getId(),null);
                    if(!taskList.isEmpty()){
                        //排序任务
                        orderTask(taskList);
                    }
                    for (Task t : taskList) {
                        System.out.println(t.getTaskId()+"\t" + t.getPriority());
                    }
                } else{
                    taskList = taskService.findTaskByExecutor(userEntity.getId(),orderType);
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
     * 实现 页面的我的收藏 数据填充
     * 根据类型查询当前用户的收藏
     * @return
     */
    @PostMapping("myCollect")
    @ResponseBody
    public JSONObject myCollectTask(String type){
        //如果查询类型为所有收藏 则 设置为null
        if(allCollect.equals(type)){
            type = null;
        }
        JSONObject jsonObject = new JSONObject();
        String memberId = ShiroAuthenticationManager.getUserId();
        try {
            List<PublicCollectVO> taskList = publicCollectService.listMyCollect(memberId,type);
            jsonObject.put("data",taskList);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,数据拉取失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * 取消收藏
     * @param publicCollectId 收藏id
     * @return
     */
    @PostMapping("cancelCollectTask")
    @ResponseBody
    public JSONObject cancelCollectTask(String publicCollectId){
        JSONObject jsonObject = new JSONObject();
        try {
            int result = publicCollectService.cancelCollectTask(publicCollectId);
            jsonObject.put("result",result);
        } catch (Exception e){
            log.error("系统异常,取消失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 按照任务的优先级排序任务
     * @param taskList
     * @return
     */
    public void orderTask(List<Task> taskList){
        for (int i = taskList.size()-1;i > 0;i--) {
            for (int j = taskList.size() - 1; j > 0; j--) {
                if (taskList.get(j).getPriority().equals("非常紧急")) {
                    Task task = taskList.get(j - 1);
                    taskList.set(j - 1, taskList.get(j));
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
    }


    /**
     * 账号设置
     * @return	视图信息
     */
    @RequestMapping(value = "/accountInfo.html", method = RequestMethod.GET)
    public String accountSetting(String userId,Model model) {
        UserEntity userEntity = userService.findById(userId);
        model.addAttribute("user",userEntity);
        return "set-my-info";
    }



    //上传头像图片
    @PostMapping("/upload")
    @ResponseBody
    public JSONObject uploadImage(@RequestParam String userId,
                                  MultipartFile file){
        JSONObject jsonObject = new JSONObject();
        try {
            //先删除阿里云上项目的图片然后再上传
            UserEntity userEntity = userService.findById(userId);
            AliyunOss.deleteFile(userEntity.getUserInfo().getImage());
            String filename = System.currentTimeMillis()+".jpg";
            AliyunOss.uploadInputStream(Constants.MEMBER_IMAGE_URL + filename,file.getInputStream());
            UserInfoEntity userInfoEntity = new UserInfoEntity();
            userInfoEntity.setImage(Constants.MEMBER_IMAGE_URL + filename);
            userInfoEntity.setId(userEntity.getId());
            userEntity.setUserInfo(userInfoEntity);
            userService.update(userEntity);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "上传成功");
            jsonObject.put("data",Constants.MEMBER_IMAGE_URL + filename);
        }catch (Exception e){
            throw new SystemException(e);
        }
        return jsonObject;
    }
}
