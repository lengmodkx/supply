package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.task.TaskLogVO;
import com.art1001.supply.entity.task.TaskPushType;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.project.ProjectService;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/binding")
public class BindingController {

    @Resource
    private BindingService bindingService;

    @Resource
    private ProjectService projectService;

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
            binding.setPublicId(publicId);
            binding.setBindId(bindId);
            binding.setPublicType(publicType);
            bindingService.saveBinding(binding);
            jsonObject.put("result",1);
            jsonObject.put("msg","保存成功");
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
            TaskLogVO taskLogVO = bindingService.deleteBindingById(id);
            jsonObject.put("result",1);
            jsonObject.put("msg","删除成功");
//            TaskPushType taskPushType = new TaskPushType(TaskLogFunction.A17.getName());
//            Map<String,Object> map = new HashMap<String,Object>();
//            map.put("taskLog",taskLogVO);
//            map.put("bId",bId);
//            taskPushType.setObject(map);
            //messagingTemplate.convertAndSend("/topic/"+ taskLogVO.getTask().getTaskId(),new ServerMessage(JSON.toJSONString(taskPushType)));
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    @RequestMapping("/relevance.html")
    public String bindpage(){
        return "tk-relevance";
    }


}
