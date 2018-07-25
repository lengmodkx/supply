package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskPushType;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("/binding")
public class BindingController {

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private BindingService bindingService;

    @Resource
    private ProjectService projectService;

    @Resource
    private UserService userService;

    @Resource
    private RelationService relationService;

    @Resource
    private TaskService taskService;

    @Resource
    private FileService fileService;

    @Resource
    private ShareService shareService;

    /** 用于订阅推送消息 */
    @Resource
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 任务,日程，文件，分享与任务,日程，文件，分享的绑定关系
     * @param publicId 任务,日程，文件，分享的id
     * @param bindId 被绑定的任务,日程，文件，分享的id
     * @param publicType 绑定类型 任务,日程，文件，分享 枚举类型
     * @return
     */
    @RequestMapping("/saveBinding")
    @ResponseBody
    public JSONObject saveBinding(@RequestParam String publicId,@RequestParam String bindId[],@RequestParam String publicType){
        JSONObject jsonObject = new JSONObject();
        List<String> bindList1 = Arrays.asList(bindId);
        List<String> bindList = new ArrayList<String>(bindList1);
        try {
            for(int i = 0;i < bindList.size();i++){
                //判断是不是和自己关联
                if(Objects.equals(publicId,bindList.get(i))){
                    bindList.remove(i);
                }
            }
            //查询记录表中有没有存在该条关联记录
            String[] record = bindingService.getBindingRecord(publicId,bindId);
            //如果大于0 说明一定有关联信息重复
            for(int i = 0;i < record.length;i++){
                for(int j = 0;j < bindList.size();j++){
                    if(Objects.equals(record[i],bindList.get(j))){
                        bindList.remove(j);
                    }
                }
            }
            //数据推送包装类
            TaskPushType taskPushType = new TaskPushType("关联");
            Map<String,Object> map = new HashMap<String,Object>();
            List bInfo = new ArrayList();
            for (int i = 0;i < bindList.size();i++){
                if(bindList.get(i) != null){
                    Binding binding = new Binding();
                    //设置该条绑定关系的id
                    binding.setId(IdGen.uuid());
                    //设置 谁绑定 的id
                    binding.setPublicId(publicId);
                    //设置被绑定的 信息的id
                    binding.setBindId(bindList.get(i));
                    //设置绑定的类型
                    binding.setPublicType(publicType);
                    bindingService.saveBinding(binding);
                    jsonObject.put("result",1);
                    jsonObject.put("msg","保存成功");

                    if(BindingConstants.BINDING_TASK_NAME.equals(publicType)){
                        Task bindingInfo = taskService.findTaskByTaskId(bindList.get(i));
                        bindingInfo.setCreatorInfo(null);
                        bInfo.add(bindingInfo);
                    }

                    if(BindingConstants.BINDING_FILE_NAME.equals(publicType)){
                        File bindingInfo = fileService.findFileById(bindList.get(i));
                        bInfo.add(bindingInfo);
                    }

                    if(BindingConstants.BINDING_SCHEDULE_NAME.equals(publicType)){
                        Schedule bindingInfo = scheduleService.findScheduleById(bindList.get(i));
                        bInfo.add(bindingInfo);
                    }

                    if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
                        Share bindingInfo = shareService.findById(bindList.get(i));
                        bInfo.add(bindingInfo);
                    }

                    map.put("bindingInfo",bInfo);
                    map.put("publicType",publicType);
                    taskPushType.setObject(map);
                }
            }
            if(map.size() > 0){
                messagingTemplate.convertAndSend("/topic/"+publicId,new ServerMessage(JSON.toJSONString(taskPushType)));
            }
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * @param publicId 当前绑定信息的 id
     * @param bindingId 被绑定的信息id
     * @return
     */
    @RequestMapping("/deleteBinding")
    @ResponseBody
    public JSONObject deleteBinding(String publicId,String bindingId){
        JSONObject jsonObject = new JSONObject();
        try {
            bindingService.deleteBindingById(publicId,bindingId);
            jsonObject.put("result",1);
            jsonObject.put("msg","删除成功");
            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.A17.getName());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("bId",bindingId);
            taskPushType.setObject(map);
            messagingTemplate.convertAndSend("/topic/"+publicId,new ServerMessage(JSON.toJSONString(taskPushType)));
        }catch (Exception e){
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,关联删除失败,请重试.");
            log.error("系统异常,取消失败,{}",e);
        }
        return jsonObject;
    }


    /**
     * @param model
     * @param taskId 当前哪个任务需要关联的 任务id
     * @param fileId 当前哪个文件需要关联的 文件id
     * @return
     */
    @RequestMapping("/relevance.html")
    public String bindpage(Model model,String taskId,String fileId){
        //获取当前用户的id
        String uId = ShiroAuthenticationManager.getUserId();
        //获取当前用户参与的所有项目
        List<Project> projectList = projectService.listProjectByUid(uId);
        if(!projectList.isEmpty()){
            List<Relation> allGroupInfoByProjectId = relationService.findAllGroupInfoByProjectId(projectList.get(0).getProjectId());
            model.addAttribute("groups",allGroupInfoByProjectId);
        }
        model.addAttribute("projectList",projectList);
        model.addAttribute("taskId",taskId);
        model.addAttribute("fileId",fileId);
        return "tk-relevance";
    }


}
