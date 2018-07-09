package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/public")
public class PublicController {

    @Resource
    private TaskService taskService;


    @GetMapping("mypage.html")
    public String my(Model model){
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        List<Task> taskList = taskService.findTaskByUserId(userEntity.getId());
        model.addAttribute("taskList",taskList);
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
    public JSONObject myAddTask(){
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        try {
            List<Task> taskList = taskService.findTaskByMemberId(userEntity.getId());
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
    public JSONObject myJoinTask(){
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        try {
            List<Task> taskList = taskService.findTaskByUserId(userEntity.getId());
            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
            jsonObject.put("data",taskList);
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }

    /**
     * 我执行的任务
     * @return
     */
    @PostMapping("/myExecutorTask")
    public JSONObject myExecutorTask(){
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        try {

            List<Task> taskList = taskService.findTaskByExecutor(userEntity.getId(), "");
            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
            jsonObject.put("data",taskList);
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }



}
