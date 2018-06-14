package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.*;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
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
     * @param userEntity 该任务的成员id数组
     * @param project 得到当前项目的实体信息
     * @param task 任务实体信息
     * @return
     */
    @PostMapping("saveTask")
    @ResponseBody
    public JSONObject saveTask(
                              @RequestParam UserEntity [] userEntity,
                              @RequestParam Project project,
                              @RequestParam Task task
    ){
        JSONObject jsonObject = new JSONObject();
        try {
            //保存任务信息到数据库
            TaskLogVO taskLogVO = taskService.saveTask(userEntity,project,task);
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
     * 添加项目成员
     * @param task
     * @param userEntity
     */
    @PostMapping("addTaskMember")
    @ResponseBody
    public JSONObject addTaskMember(@RequestParam Task task,@RequestParam UserEntity[] userEntity){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.addTaskMember(task,userEntity);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","添加成功!");
                jsonObject.put("result",taskLogVO.getResult());
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","添加失败!");
                jsonObject.put("result",taskLogVO.getResult());
            }
        } catch (Exception e){
            log.error("系统异常,成员添加失败! 当前任务id:{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 移除任务-成员关系
     * @param task 当前项目实体信息
     * @param userEntity 被移除的用户的信息
     * @return
     */
    @PostMapping("removeTaskMember")
    @ResponseBody
    public JSONObject removeTaskMember(@RequestParam Task task,@RequestParam UserEntity[] userEntity){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.removeTaskMember(task,userEntity);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","任务参与者移除成功!");
                jsonObject.put("result",taskLogVO.getResult());
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","任务参与者移除失败!");
                jsonObject.put("result",taskLogVO.getResult());
            }
        } catch (Exception e){
            log.error("移除任务成员失败! 当前任务id: {},{}",task.getTaskId(),e);
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
     * 完成任务 和重做任务
     * @param task 当前任务信息
     */
    @PostMapping("resetAndCompleteTask")
    @ResponseBody
    public JSONObject resetAndCompleteTask(@RequestParam Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            //改变任务状态
            TaskLogVO taskLogVO = taskService.resetAndCompleteTask(task);
            if(taskLogVO.getResult() >1){
                jsonObject.put("msg","修改成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","修改失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("任务状态修改失败! 任务id: {}, \t 修改前状态:{},{}",task.getTaskId(),task.getTaskStatus(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新任务的重复规则
     * @param task 任务的实体信息
     * @param object 时间重复周期的具体信息 (未设定)
     * @return
     */
    @PostMapping("updateTaskRepeat")
    @ResponseBody
    public JSONObject updateTaskRepeat(@RequestParam Task task,@RequestParam Object object){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.updateTaskRepeat(task,object);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","重复规则设置成功");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","重复规则设置失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常,重复规则更新失败! 当前任务id: {},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return null;
    }

    /**
     * 更新任务( 开始 / 结束 ) 时间
     * @param task 包含时间的实体信息
     */
    @PostMapping("updateTaskStartAndEndTime")
    @ResponseBody
    public JSONObject updateTaskStartAndEndTime(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            //更新任务时间信息
            TaskLogVO taskLogVO = taskService.updateTaskStartAndEndTime(task);
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
     * 清除任务的开始时间和结束时间
     * @param task 任务的实体信息
     * @return
     */
    @PostMapping("removeTaskStartAndEndTime")
    @ResponseBody
    public JSONObject removeTaskStartAndEndTime(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.removeTaskStartAndEndTime(task);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","清空成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","清空失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常,清除失败! 当前任务id:{},{} ",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新任务的提醒时间
     * @param task 任务实体信息
     * @param userEntity 用户实体信息
     * @return
     */
    @PostMapping("updateTaskRemindTime")
    @ResponseBody
    public JSONObject updateTaskRemindTime(@RequestParam Task task,@RequestParam UserEntity userEntity){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.updateTaskRemindTime(task,userEntity);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","设置成功！");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            }
                jsonObject.put("msg","设置失败！");
                jsonObject.put("result","0");
        } catch (Exception e){
            log.error("系统异常,设置提醒时间失败! 任务id: {},{}",task.getTaskId(),e);
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

    /**
     * 给当前任务设置标签
     * @param tag 标签名称
     * @param taskId 当前任务id
     * @param projectId 当前任务的id
     * @return
     */
    @PostMapping("addTaskTags")
    @ResponseBody
    public JSONObject addTags(Tag tag,@RequestParam String taskId,@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            //根据标签名称查询 当前存不存在数据库 如果存在直接绑定到当前任务,如果不存在则先插入标签 在绑定到当前任务
            int countByTagName = tagService.findCountByTagName(projectId, tag.getTagName());
            if(countByTagName == 0){
                tag.setTagId(tagService.saveTag(tag));
            }
            //更新当前任务的标签信息
            TaskLogVO taskLogVO = taskService.addTaskTags(tag, taskId,countByTagName);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","标签添加成功!");
                jsonObject.put("result",taskLogVO.getResult());
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","标签添加失败!");
                jsonObject.put("result",taskLogVO.getResult());
            }
        } catch (Exception e){
            log.error("保存失败,标签名称为:{},{}",tag.getTagName(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 移除该任务上的标签
     * @param tags 当前任务上绑定的所有标签对象数组
     * @param tag 当前要被的标签对象
     * @param taskId 当前任务uid
     * @return
     */
    public JSONObject removeTaskTag(@RequestParam Tag[] tags,@RequestParam Tag tag,@RequestParam String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            //更新该任务的标签信息
            int result = taskService.removeTaskTag(tags,tag,taskId);
            if(result > 0){
                jsonObject.put("msg","标签移除成功!");
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","标签移除失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常，标签移除失败！ 当前任务id： {},{}",taskId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }


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
    @PostMapping("addTaskRely")
    @ResponseBody
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


    /**
     * 移除依赖关系
     * @param task 关联的任务
     * @param file 关联的文件
     * @param share 关联的分享
     * @param schedule 关联的日程
     * @param taskId 当前任务的id
     * @param taskRelyId  当前依赖的id
     * @return
     */
    @PostMapping("removeTaskRely")
    @ResponseBody
    public JSONObject removeTaskRely(@RequestParam Task task,
                                     @RequestParam File file,
                                     @RequestParam Share share,
                                     @RequestParam Schedule schedule,
                                     @RequestParam String taskId,
                                     @RequestParam String taskRelyId
    ){
        JSONObject jsonObject = new JSONObject();
        try {
            //删除关联关系
            TaskLogVO taskLogVO = taskMemberService.deleteTaskMemberById(task, file, share, schedule, taskId, taskRelyId);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","删除成功");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","删除失败");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常,关联关系删除失败!  任务关联id:{},{}",taskRelyId,e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 转子任务为顶级任务
     * @param task 包含当前任务的 id、name
     * @return
     */
    @PostMapping("turnToFatherLevel")
    @ResponseBody
    public JSONObject turnToFatherLevel(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.turnToFatherLevel(task);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","转换成功");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","转换失败");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统错误,任务转换失败! 当前任务id:{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 给当前任务点赞
     * @param task 任务的实体信息
     * @return
     */
    @PostMapping("clickFabulous")
    @ResponseBody
    public JSONObject clickFabulous(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            int result = taskService.clickFabulous(task);
            if(result > 0){
                jsonObject.put("msg","成功!");
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常! 点赞失败! 当前任务id: {},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 对当前任务取消赞
     * @param task 当前任务信息
     * @return
     */
    public JSONObject cancelFabulous(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            int result = taskService.cancelFabulous(task);
            if(result > 0){
                jsonObject.put("msg","赞已取消!");
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","取消失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常,取消赞失败! 当前任务id:{},{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    @PostMapping("addSubLevelTask")
    @ResponseBody
    public JSONObject addSubLevelTask(Task currentTask,Task subLevel,String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            //保存子任务信息至数据库
            TaskLogVO taskLogVO = taskService.addSubLevelTasks(currentTask,subLevel,projectId);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","添加成功!");
                jsonObject.put("result","1");
                jsonObject.put("taskLog",taskLogVO);
            } else{
                jsonObject.put("msg","添加失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常! 添加子级任务失败! 当前任务id: {},{}",currentTask.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 重做和完成子任务
     * @param task
     */
    @PostMapping("resetAndCompleteSubLevelTask")
    @ResponseBody
    public JSONObject resetAndCompleteSubLevelTask(Task task){
        JSONObject jsonObject = new JSONObject();
        try {
            TaskLogVO taskLogVO = taskService.resetAndCompleteSubLevelTask(task);
            if(taskLogVO.getResult() > 0){
                jsonObject.put("msg","状态更新成功!");
                jsonObject.put("result","1");
            } else{
                jsonObject.put("msg","状态更新失败!");
                jsonObject.put("result","0");
            }
        } catch (Exception e){
            log.error("系统异常! 状态更新失败 当前任务id: {}{}",task.getTaskId(),e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    public void copyTask(@RequestParam Task task){
        try {
            TaskLogVO taskLogVO = taskService.copyTask(task);

        } catch (Exception e){
            log.error("");
            throw new AjaxException(e);
        }
    }
}
