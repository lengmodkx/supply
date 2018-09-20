package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.base.PublicVO;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.collect.PublicCollectVO;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.task.PushType;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.chat.ChatService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.crypto.EndecryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("/public")
public class PublicController {

    /** 任务逻辑层接口 */
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
    private UserNewsService userNewsService;

    @Resource
    private UserService userService;

    @Resource
    private LogService logService;


    @Resource
    private SimpMessagingTemplate messagingTemplate;

    @Resource
    private ChatService chatService;

    /**
     * 所有收藏的常量
     */
    static final String allCollect = "所有收藏";


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
    @PostMapping("cancelCollect")
    @ResponseBody
    public JSONObject cancelCollect(String publicCollectId){
        JSONObject jsonObject = new JSONObject();
        try {
            int result = publicCollectService.cancelCollect(publicCollectId);
            jsonObject.put("result",result);
        } catch (Exception e){
            log.error("系统异常,取消失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 取消收藏
     * @param publicId 收藏id
     * @return
     */
    @PostMapping("cancelCollectByUser")
    @ResponseBody
    public JSONObject cancelCollectByUser(String publicId){
        JSONObject jsonObject = new JSONObject();
        try {
            int result = publicCollectService.cancelCollectByUser(publicId);
            jsonObject.put("result",result);
        } catch (Exception e){
            log.error("系统异常,取消失败,{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 收藏项 (文件,日程,分享)
     * @param publicId 项id
     * @param publicType 项的类型 (文件,日程,分享)
     * @return
     */
    @PostMapping("collectItem")
    @ResponseBody
    public JSONObject collectItem(String publicId, String publicType){
        JSONObject jsonObject = new JSONObject();
        try {
            publicCollectService.collectItem(publicId, publicType);
        } catch (Exception e){
            log.error("系统异常,收藏失败!");
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,收藏失败!");
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
                                  @RequestParam String base64url){
        JSONObject jsonObject = new JSONObject();
        try {
            //先删除阿里云上项目的图片然后再上传
            UserEntity userEntity = userService.findById(userId);
            AliyunOss.deleteFile(userEntity.getImage());
            String filename = System.currentTimeMillis()+".jpg";

            base64url = base64url.substring(22);
            byte[] bytes = Base64.decodeBase64(base64url);
            InputStream input = new ByteArrayInputStream(bytes);
            AliyunOss.uploadInputStream(Constants.MEMBER_IMAGE_URL + filename,input);

            userEntity.setImage(Constants.MEMBER_IMAGE_URL + filename);
            userService.update(userEntity);

            jsonObject.put("result", 1);
            jsonObject.put("msg", "上传成功");
            jsonObject.put("url",Constants.MEMBER_IMAGE_URL + filename);
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    @PostMapping("/saveMy")
    @ResponseBody
    public JSONObject saveMy(@RequestParam String userId,
                             @RequestParam String userName,
                             @RequestParam(required = false,defaultValue = "") String email,
                             @RequestParam(required = false,defaultValue = "") String job,
                             @RequestParam(required = false,defaultValue = "") String telephone,
                             @RequestParam(required = false,defaultValue = "") String birthday,
                             @RequestParam(required = false,defaultValue = "") String address){
        JSONObject jsonObject = new JSONObject();
        try {
            UserEntity userEntity = userService.findById(userId);
            userEntity.setUserName(userName);

            if(StringUtils.isNotEmpty(email)){
                userEntity.setEmail(email);
            }

            if(StringUtils.isNotEmpty(job)){
                userEntity.setJob(job);
            }

            if(StringUtils.isNotEmpty(telephone)){
                userEntity.setTelephone(telephone);
            }

            if(StringUtils.isNotEmpty(birthday)){
                userEntity.setBirthday(DateUtils.toDate(birthday+" 00:00:00",""));
            }
            if (StringUtils.isNotEmpty(address)){
                userEntity.setAddress(address);
            }

            userService.update(userEntity);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "更新成功");
            jsonObject.put("data",userName);
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }



    @PostMapping("/setDefaultImg")
    @ResponseBody
    public JSONObject setDefaultImg(@RequestParam String userId){
        JSONObject jsonObject = new JSONObject();
        try{
            UserEntity userEntity = userService.findById(userId);
            userEntity.setImage(userEntity.getDefaultImage());
            userService.update(userEntity);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "更新成功");
            jsonObject.put("url",userEntity.getDefaultImage());
        }catch (Exception e){
            throw  new AjaxException(e);
        }
        return jsonObject;
    }

    @PostMapping("updatePassword")
    public JSONObject updatePassword(@RequestParam String userId,@RequestParam String oldPassword,@RequestParam String password){

        JSONObject jsonObject = new JSONObject();
        try{
            UserEntity userEntity = userService.findById(userId);
            // 加密用户输入的密码，得到密码和加密盐，保存到数据库
            UserEntity user1 = EndecryptUtils.md5Password(userEntity.getAccountName(), oldPassword, 2);
            if(user1.getPassword().equals(userEntity.getPassword())){
                UserEntity user = EndecryptUtils.md5Password(userEntity.getAccountName(), password, 2);
                //设置添加用户的密码和加密盐
                userEntity.setPassword(user.getPassword());
                userEntity.setCredentialsSalt(user.getCredentialsSalt());
                userService.update(userEntity);
                jsonObject.put("result", 1);
                jsonObject.put("msg", "更新成功");
            }else{
                jsonObject.put("result", 0);
                jsonObject.put("msg", "原密码错误，请重新输入");
            }

        }catch (Exception e){
            throw  new AjaxException(e);
        }
        return jsonObject;
    }






    @GetMapping("mypage.html")
    public String findThreeDay(Model model){
        try {
            //近三天的 日程
            List<Schedule> scheduleByUserIdAndByTreeDay = scheduleService.findScheduleByUserIdAndByTreeDay(ShiroAuthenticationManager.getUserId());
            //近三天的 任务
            List<Task> taskByUserIdAndByTreeDay = taskService.findTaskByUserIdAndByTreeDay(ShiroAuthenticationManager.getUserId());

            List<PublicVO> publicVOs = new ArrayList<PublicVO>(3);
            String[] days = {"今天","明天","后天"};
            for (String day : days) {
                PublicVO publicVO = new PublicVO();
                publicVO.setName(day);

                for (Schedule s : scheduleByUserIdAndByTreeDay) {
                    if(day.equals(days[0]) && DateUtils.getDateStr().equals(DateUtils.getDateStr(new Date(s.getStartTime()),"yyyy-MM-dd"))) {
                        publicVO.getScheduleList().add(s);
                        continue;
                    }
                    if(day.equals(days[1]) && DateUtils.getAfterDay(DateUtils.getDateStr(),1,"yyyy-MM-dd","yyyy-MM-dd").equals(DateUtils.getDateStr(new Date(s.getStartTime()),"yyyy-MM-dd"))){
                        publicVO.getScheduleList().add(s);
                        continue;
                    }
                    if(day.equals(days[2]) && DateUtils.getAfterDay(DateUtils.getDateStr(),2,"yyyy-MM-dd","yyyy-MM-dd").equals(DateUtils.getDateStr(new Date(s.getStartTime()),"yyyy-MM-dd"))){
                        publicVO.getScheduleList().add(s);
                        continue;
                    }
                }

                for (Task t : taskByUserIdAndByTreeDay) {
                    if(day.equals(days[0]) && DateUtils.getDateStr().equals(DateUtils.getDateStr(new Date(t.getStartTime()),"yyyy-MM-dd"))) {
                        publicVO.getTaskList().add(t);
                        continue;
                    }
                    if(day.equals(days[1]) && DateUtils.getAfterDay(DateUtils.getDateStr(),1,"yyyy-MM-dd","yyyy-MM-dd").equals(DateUtils.getDateStr(new Date(t.getStartTime()),"yyyy-MM-dd"))){
                        publicVO.getTaskList().add(t);
                        continue;
                    }
                    if(day.equals(days[2]) && DateUtils.getAfterDay(DateUtils.getDateStr(),2,"yyyy-MM-dd","yyyy-MM-dd").equals(DateUtils.getDateStr(new Date(t.getStartTime()),"yyyy-MM-dd"))){
                        publicVO.getTaskList().add(t);
                        continue;
                    }
                }
                publicVOs.add(publicVO);
            }
            model.addAttribute("datas",publicVOs);
        } catch (Exception e){
            log.error("系统异常,数据拉取失败,{}",e);
        }
        return "mypage";
    }

    /**
     * 查询出日历上的所有任务
     * @return
     */
    @PostMapping("/scheduleCalendar")
    @ResponseBody
    public JSONObject scheduleCalendar(){
        JSONObject jsonObject = new JSONObject();
        try{
            List<Schedule> list = scheduleService.findCalendarSchedule();
            jsonObject.put("data",list);
        }catch (Exception e){
            log.error("系统异常,数据拉取失败,{}",e);
            jsonObject.put("msg","系统异常,数据拉取失败!");
            jsonObject.put("result",0);
        }
        return jsonObject;
    }


    @PostMapping("chat")
    @ResponseBody
    public JSONObject chat(String publicId, String publicType, String content, String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            int logFlag = 0;
            if(BindingConstants.BINDING_TASK_NAME.equals(publicType)){
                logFlag = 1;
            }
            if(BindingConstants.BINDING_FILE_NAME.equals(publicType)){
                logFlag = 2;
            }
            if(BindingConstants.BINDING_SCHEDULE_NAME.equals(publicType)){
                logFlag = 3;
            }
            if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
                logFlag = 4;
            }

            if(StringUtils.isEmpty(content)){
                return jsonObject;
            }
            //保存聊天信息
            Log log = new Log();
            log.setId(IdGen.uuid());
            if(StringUtils.isEmpty(content)){
                log.setContent("");
            }else{
                log.setContent(content);
                //查询出该任务的所有成员id

                String[] users = chatService.findMemberByPublicType(publicId,publicType).split(",");
                //保存消息信息
                userNewsService.saveUserNews(users,publicId,publicType,content,1);
            }

            log.setLogType(1);
            log.setMemberId(ShiroAuthenticationManager.getUserId());
            log.setPublicId(publicId);
            log.setLogFlag(logFlag);
            log.setCreateTime(System.currentTimeMillis());
            Log log1 = logService.saveLog(log);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "上传成功");

            PushType taskPushType = new PushType(TaskLogFunction.A14.getName());
            Map<String,Object> map = new HashMap<>();
            if(BindingConstants.BINDING_TASK_NAME.equals(publicType)){
                map.put("taskLog",log1);
            }
            if(BindingConstants.BINDING_FILE_NAME.equals(publicType)){
                map.put("fileLog",log1);
            }
            if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
                map.put("shareLog",log1);
                map.put("shareId",publicId);
            }
            if(BindingConstants.BINDING_SCHEDULE_NAME.equals(publicType)){
                map.put("scheduleLog",log1);
            }
            taskPushType.setObject(map);

            if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
                messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(taskPushType)));
                messagingTemplate.convertAndSend("/topic/"+publicId,new ServerMessage(JSON.toJSONString(taskPushType)));
            } else{
                messagingTemplate.convertAndSend("/topic/"+publicId,new ServerMessage(JSON.toJSONString(taskPushType)));
            }
        } catch (Exception e){
             log.error("系统异常, 消息发送失败!");
             jsonObject.put("result",0);
             jsonObject.put("msg","系统异常, 消息发送失败!");
        }
        return jsonObject;
    }

}
