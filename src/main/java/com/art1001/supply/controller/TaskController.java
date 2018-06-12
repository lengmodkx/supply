package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.*;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskMemberService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import io.netty.handler.codec.json.JsonObjectDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

/**
 * 任务控制器，关于任务的操作
 */
@Controller
@RequestMapping("task")
@Slf4j
public class TaskController {

    /** 任务逻辑层接口 */
    @Resource
    private TaskService taskService;

    /** 标签逻辑层接口 */
    @Resource
    private TagService tagService;

    /** 任务关系表逻辑层接口 */
    @Resource
    private TaskMemberService taskMemberService;

    /**
     * 添加新任务
     * @param memberId 该任务的成员id数组
     * @param project 得到当前项目的实体信息
     * @param task 任务实体信息
     * @return
     */
    @PostMapping("saveTask")
    @ResponseBody
    public JSONObject saveTask(
                              @RequestParam String [] memberId,
                              @RequestParam Project project,
                              @RequestParam Task task
    ){
        JSONObject jsonObject = new JSONObject();
        try {
            //保存任务信息到数据库
            TaskLogVO taskLogVO = taskService.saveTask(memberId,project,task);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","添加任务成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            }
        } catch (Exception e){
            jsonObject.put("msg","任务添加失败!");
            jsonObject.put("result","0");
            log.error("当前任务保存失败!{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 任务移动
     * @param task 包含该任务的id
     * @param newTaskMenuVO 要移动到的 项目id,名称 分组id,名称 菜单id,名称
     * @param oldTaskMenuVO 移动之前的 项目id,名称 分组id,名称 菜单id,名称
     * @return
     */
    @PostMapping("mobileTask")
    @ResponseBody
    public JSONObject mobileTask(@RequestParam Task task, @RequestParam TaskMenuVO oldTaskMenuVO, @RequestParam TaskMenuVO newTaskMenuVO){
        JSONObject jsonObject = new JSONObject();
        try {
            //修改该任务的任务组编号
            TaskLogVO taskLogVO = taskService.mobileTask(task,oldTaskMenuVO,newTaskMenuVO);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("result", 1);
                jsonObject.put("msg","任务移动成功！");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("result", 0);
                jsonObject.put("msg","任务移动失败！");
            }
        } catch (Exception e){
            log.error("当前任务移动失败!  任务id：{}\t 该任务初始组为:{}, {}", task.getTagId(),task.getTaskMenuId(),e);
            jsonObject.put("result", 0);
            jsonObject.put("msg","系统异常,移动失败！");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     *  将任务 (移入回收站/回复)
     * @param taskId 任务的id
     * @param taskDel 任务是否在回收站
     * @return
     */
    @PostMapping("moveToRecycleBin")
    @ResponseBody
    public JSONObject moveToRecycleBin(@RequestParam String taskId,@RequestParam String taskDel) {
        JSONObject jsonObject = new JSONObject();
        try {
            //将任务移入回收站
            TaskLogVO taskLogVO = taskService.moveToRecycleBin(taskId, taskDel);
            if (taskLogVO.getResult() > 0) {
                jsonObject.put("msg", "操作成功！");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else {
                jsonObject.put("msg", "操作失败！");
                jsonObject.put("result","0");
            }
        } catch (Exception e) {
            log.error("操作失败，任务：" + taskId + ",操作前该任务状态:\t{}, {}",taskDel, e);
            jsonObject.put("msg", "系统异常,操作失败！");
            jsonObject.put("result","0");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更细项目信息 包括以下
     * 1.修改任务内容丶优先级丶重复性
     * 2.修改任务备注
     * 3.修改任务的执行者
     * @param task 任务的实体信息
     */
    @PostMapping("upateTaskInfo")
    @ResponseBody
    public JSONObject updateTask(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.updateTask(task);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","更新成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","更新失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常,操作失败! 当前任务:{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 永久的删除任务
     * @param taskId 任务id
     * @return
     */
    @PostMapping("delTask")
    @ResponseBody
    public JSONObject delTask(@RequestParam String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            //永久删除任务
            int result = taskService.deleteTaskByTaskId(taskId);
            if(result >1){
                jsonObject.put("msg","以将该任务清除!");
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","移除任务失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常,移除失败! 任务: {},{}",taskId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 完成任务(2=完成 1=未完成)
     * @param taskId 当前任务id
     * @param taskStatus 当前任务状态
     */
    @PostMapping("changeTaskStatus")
    @ResponseBody
    public JSONObject changeTaskStatus(@RequestParam String taskId,@RequestParam String taskStatus){
        JSONObject jsonObject = new JSONObject();
        try {
            //改变任务状态
            TaskLogVO taskLogVO = taskService.changeTaskStatus(taskId,taskStatus);
            if(taskLogVO.getResult() >1){
                jsonObject.put("msg","修改成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","修改失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("任务状态修改失败! 任务id: {}, \t 修改前状态:{},{}",taskId,taskStatus,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新任务时间( 开始 / 结束 / 提醒)
     * @param task 包含时间的实体信息
     */
    @PostMapping("updateTaskTime")
    @ResponseBody
    public JSONObject updateTaskTime(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            //更新任务时间信息
            TaskLogVO taskLogVO = taskService.updateTaskTime(task);
            if(taskLogVO.getResult() > 1){
                jsonObject.put("msg","时间更新成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","时间更新失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("任务时间信息更新失败{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 根据菜单id 查询该菜单下有没有任务
     * @param taskMenuId 分组id
     * @return
     */
    @PostMapping("findTaskByMenuId")
    @ResponseBody
    public JSONObject findTaskByMenuId(@RequestParam String taskMenuId){
        JSONObject jsonObject = new JSONObject();
        try{
            int result = taskService.findTaskByMenuId(taskMenuId);
            jsonObject.put("result",result);
        } catch (Exception e){
            log.error("查询失败! 当前菜单id:{},{}",taskMenuId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

//    /**
//     * 给当前任务设置标签
//     * @param oldTags 该任务更新标签之前的标签信息
//     * @param tag 标签名称
//     * @param taskId 当前任务id
//     * @return
//     */
//    @PostMapping("addTags")
//    public JSONObject addTags(String[] oldTags,Tag tag,@RequestParam String taskId){
//        JSONObject jsonObject = new JSONObject();
//        try {
//            int result = tagService.saveTag(tag,oldTags,taskId);
//            if(result > 0){
//                jsonObject.put("msg","标签添加成功!");
//                jsonObject.put("result",result);
//            } else{
//                jsonObject.put("msg","标签添加失败!");
//                jsonObject.put("result",result);
//            }
//        } catch (Exception e){
//            log.error("保存失败,标签名称为:{},{}",tag.getTagName(),e);
//            throw new AjaxException(e);
//        }
//        return jsonObject;
//    }


    /**
     * 给当前任务添加依赖关系
     * @param task 关联任务的实体信息
     * @param file 关联文件的实体信息
     * @param share 关联分享的实体信息
     * @param schedule 关联日程的实体信息
     * @param taskMember 关联关系信息
     * @param taskId 当前被操作的任务uid
     * @return
     */
    public JSONObject addTaskRely(@RequestParam Task task,
                                  @RequestParam File file,
                                  @RequestParam Share share,
                                  @RequestParam Schedule schedule,
                                  @RequestParam TaskMember taskMember,
                                  @RequestParam String taskId
    ){
        JSONObject jsonObject = new JSONObject();
        try{
            TaskLogVO taskLogVO = taskMemberService.saveTaskMember(task,file,share,schedule,taskMember,taskId);
            if(taskLogVO.getResult() >0){
                jsonObject.put("msg","关联成功!");
                jsonObject.put("result",taskLogVO.getResult());
                jsonObject.put("taskLog",taskLogVO);
            }
        } catch (Exception e){
            log.error("系统异常,关联失败! 当前任务id {},{}",taskId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    public JSONObject removeTaskRely(){
        JSONObject jsonObject = new JSONObject();
        try {

        } catch (Exception e){
            log.error("");
            throw new AjaxException(e);
        }
        return null;
    }
}
