package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.TaskPushType;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/binding")
public class BindingController {

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
    public JSONObject saveBinding(@RequestParam String publicId,@RequestParam String bindId,@RequestParam String publicType){
        JSONObject jsonObject = new JSONObject();
        Binding binding = new Binding();
        try {
            //判断是不是和自己关联
            if(Objects.equals(publicId,bindId)){
                jsonObject.put("msg","不能和自己关联!");
                jsonObject.put("result",0);
                return jsonObject;
            }
            //查询记录表中有没有存在该条关联记录
            int recordCount = bindingService.getBindingRecord(publicId,bindId);
            if(recordCount > 0){
                jsonObject.put("msg","已存在关联信息,不能重复关联!");
                jsonObject.put("result",0);
                return jsonObject;
            }
            binding.setId(IdGen.uuid());
            binding.setPublicId(publicId);
            binding.setBindId(bindId);
            binding.setPublicType(publicType);
            bindingService.saveBinding(binding);
            jsonObject.put("result",1);
            jsonObject.put("msg","保存成功");
            TaskPushType taskPushType = new TaskPushType("关联");
            Map<String,Object> map = new HashMap<String,Object>(16);
            map.put("publicType",publicType);
            taskPushType.setObject(map);
            map.put("bindingInfo",taskService.findTaskByTaskId(bindId));
            map.put("bindId",binding.getId());
            messagingTemplate.convertAndSend("/topic/"+publicId,new ServerMessage(JSON.toJSONString(taskPushType)));
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     *
     * @param id 绑定id
     * @param bId 绑定目标id
     * @return
     */
    @RequestMapping("/deleteBinding")
    @ResponseBody
    public JSONObject deleteBinding(@RequestParam String id,String bId){
        JSONObject jsonObject = new JSONObject();
        try {
            bindingService.deleteBindingById(id);
            jsonObject.put("result",1);
            jsonObject.put("msg","删除成功");
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * @param model
     * @param taskId 当前哪个任务需要关联的任务id
     * @return
     */
    @RequestMapping("/relevance.html")
    public String bindpage(Model model,String taskId){
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
        return "tk-relevance";
    }


}
