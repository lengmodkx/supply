package com.art1001.supply.service.task.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.*;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.mapper.task.TaskLogMapper;
import com.art1001.supply.mapper.task.TaskMapper;
import com.art1001.supply.mapper.task.TaskMemberMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.task.TaskLogService;
import com.art1001.supply.service.task.TaskMemberService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * taskServiceImpl
 */
@Service
public class TaskServiceImpl implements TaskService {

	/** taskMapper接口*/
	@Resource
	private TaskMapper taskMapper;

	/** userMapper接口 */
	@Resource
    private UserMapper userMapper;

	/** taskMemberService 接口*/
	@Resource
    private TaskMemberService taskMemberService;

	/** TaskLogService接口 */
	@Resource
    private TaskLogService taskLogService;

	
	/**
	 * 重写方法
	 * 查询分页task数据
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Task> findTaskPagerList(Pager pager){
		return taskMapper.findTaskPagerList(pager);
	}

	/**
	 * 重写方法
	 * 通过taskId获取单条task数据
	 * @param taskId
	 * @return
	 */
	@Override 
	public Task findTaskByTaskId(String taskId){
		return taskMapper.findTaskByTaskId(taskId);
	}

	/**
	 * 重写方法
	 * 删除任务
	 * 通过taskId删除task数据
     * @param taskId 任务id
     */
	@Override
	public int deleteTaskByTaskId(String taskId){
	    return taskMapper.deleteTaskByTaskId(taskId);
    }

	/**
	 * 重写方法
	 * 修改task数据
	 * @param task 任务信息
	 */
	@Override
	public TaskLogVO updateTask(Task task){
	    String content = "";
	    TaskLogVO taskLogVO = new TaskLogVO();
	    //任务更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //更新任务内容
        if(task.getTaskName() != null && task.getTaskName() != ""){
            taskLogVO = saveTaskLog(task,TaskLogFunction.T.getName());
        }
        //更新任务优先级
        if(task.getPriority() != null && task.getPriority() != ""){
            taskLogVO = saveTaskLog(task,TaskLogFunction.F.getName() + " " + task.getPriority());
        }
        //更新任务备注
        if(task.getRemarks() != null && task.getRemarks() != null){
            taskLogVO = saveTaskLog(task,TaskLogFunction.E.getName());
        }
        //更新任务执行者
        if(task.getExecutor() != null && task.getExecutor() != ""){
            taskLogVO = saveTaskLog(task,TaskLogFunction.U.getName());
        }
        int result = taskMapper.updateTask(task);
        taskLogVO.setResult(result);
        return taskLogVO;
	}

	/**
	 * 重写方法
	 * 将添加的任务信息保存至数据库
     * @param memberId 该任务的参与者
     * @param project 当前项目的实体信息
     * @param task task信息
     */
	@Override
	public TaskLogVO saveTask(UserEntity[] memberId, Project project, Task task) {
        //获取当前登录用户的id
        //String id = ShiroAuthenticationManager.getUserEntity().getId();
        task.setMemberId("4");
        //设置该任务的id
        task.setTaskId(IdGen.uuid());
        //初始创建任务设置为父任务
        task.setParentId("0");
        //设置该任务的创建时间
        task.setCreateTime(System.currentTimeMillis());
        //设置该任务的最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //设置该任务的初始状态
        task.setTaskStatus("1");
        //设置该任务是否删除 0 未删除 1 已删除
        task.setTaskDel(0);
        //保存任务信息
        taskMapper.saveTask(task);
        //将任务的参与者信息保存至 (任务-参与者 [task_member] ) 关系表中
        taskMemberService.saveManyTaskeMmber(memberId,task);
        //拿到TaskLog对象并且保存
        return saveTaskLog(task, TaskLogFunction.R.getName());
    }

	/**
	 * 重写方法
	 * 获取所有task数据
	 * @return
	 */
	@Override
	public List<Task> findTaskAllList(){
		return taskMapper.findTaskAllList();
	}

    /**
	 * 重写方法
     * 移入回收站/恢复任务
     * @param taskId 当前任务id
     * @param taskDel 当前任务是否已经在回收站
     * @return
     */
    @Override
    public TaskLogVO moveToRecycleBin(String taskId, String taskDel) {
        int result = taskMapper.moveToRecycleBin(taskId,taskDel,System.currentTimeMillis());
        Task task = new Task();
        task.setTaskId(taskId);
        TaskLogVO taskLogVO = new TaskLogVO();
        //如果任务状态为0 日志打印内容为 xxx把任务移入了回收站 否则   xxx恢复了任务
        if(taskDel == "0"){
            taskLogVO = saveTaskLog(task,TaskLogFunction.P.getName());
        } else{
            taskLogVO = saveTaskLog(task,TaskLogFunction.O.getName());
        }
        taskLogVO.setResult(result);
        return taskLogVO;
    }

	/**
	 * 重写方法
	 * 修改当前任务状态
	 * @param taskId 任务id
	 * @return
	 */
	@Override
	public TaskLogVO changeTaskStatus(String taskId,String taskStatus) {
	    //修改任务状态
	    int result = taskMapper.changeTaskStatus(taskId,taskStatus,System.currentTimeMillis());
        Task task = new Task();
        task.setTaskId(taskId);
        //如果当前状态为未完成  则 日志记录为 完成任务 否则为 重做任务
        if(taskStatus == "1"){
            TaskLogVO taskLogVO = saveTaskLog(task, TaskLogFunction.S.getName());
            taskLogVO.setResult(result);
            return taskLogVO;
        } else{
            TaskLogVO taskLogVO = saveTaskLog(task, TaskLogFunction.S.getName());
            taskLogVO.setResult(result);
	        return taskLogVO;
        }
	}

    /**
     * 重写方法
     * 设定任务的时间(开始 / 结束)
     * @param task 任务时间的信息
     * @return
     */
    @Override
    public TaskLogVO updateTaskStartAndEndTime(Task task) {
        String content = "";
        if(task.getStartTime() != null){
            content = TaskLogFunction.M.getName();
        }
        if(task.getEndTime() != null){
            content = TaskLogFunction.L.getName();
        }
        int result =  taskMapper.updateTask(task);
        TaskLogVO taskLogVO = saveTaskLog(task, content);
        taskLogVO.setResult(result);
        return taskLogVO;
    }

    /**
     * 判断当前菜单有没有任务
     * @param taskMenuId 菜单id
     * @return
     */
    @Override
    public int findTaskByMenuId(String taskMenuId) {
        return taskMapper.findTaskByMenuId(taskMenuId);
    }

    /**
     * 移动任务至 ( 项目、分组、菜单 )
     * @param task 任务的信息
     * @param oldTaskMenuVO 移动前该任务的位置信息
     * @param newTaskMenuVO 将要移动该任务到的位置信息
     * @return
     */
    @Override
    public TaskLogVO mobileTask(Task task, TaskMenuVO oldTaskMenuVO,TaskMenuVO newTaskMenuVO) {
        //设置更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //更新任务信息
        int result = taskMapper.updateTask(task);
        String content = "";
        //如果项目id不为空,说明该任务要移至其他项目,所以项目id,分组id,菜单id,肯定都不为空
        //或者如果分组id不为空说明该任务要移至其他分组,所以 分组id,菜单id,肯定不为空
        if((newTaskMenuVO.getProjectId() != null && newTaskMenuVO.getProjectId() != "") || (newTaskMenuVO.getTaskGroupId() != null && newTaskMenuVO.getTaskGroupId() != "")){
            //拼接任务操作日志内容的字符串
            content = TaskLogFunction.V.getName() + " " + oldTaskMenuVO.getTaskGroupName() + "/" + oldTaskMenuVO.getTaskMenuName() +" "+  TaskLogFunction.W.getName() + " " + newTaskMenuVO.getTaskGroupName() + "/" + newTaskMenuVO.getTaskMenuName();
            //保存日志信息
            TaskLogVO taskLogVO = saveTaskLog(task, content);
            taskLogVO.setResult(result);
            return taskLogVO;
        }
        //如果任务的菜单信息不为空 说明该任务要移至其他的任务菜单
        if(newTaskMenuVO.getTaskMenuId() != null && newTaskMenuVO.getTaskMenuId() != ""){
            //拼接任务操作日志内容的字符串
            content = TaskLogFunction.X.getName() + " " + newTaskMenuVO.getTaskMenuName();
            //保存日志信息
            TaskLogVO taskLogVO = saveTaskLog(task, content);
            taskLogVO.setResult(result);
            return taskLogVO;
        }
            return null;
    }

    /**
     * 返回日志实体对象
     */
    @Override
    public TaskLogVO saveTaskLog(Task task,String content){
        TaskLog taskLog = new TaskLog();
        taskLog.setId(IdGen.uuid());
        taskLog.setMemberName("admin");
        taskLog.setMemberId("4");
        //暂时不用
        //taskLog.setMemberName(ShiroAuthenticationManager.getUserEntity().getUserName());
        //taskLog.setMemberId(ShiroAuthenticationManager.getUserEntity().getId());
        //头像暂无
        taskLog.setMemberImg("");
        taskLog.setTaskId(task.getTaskId());
        taskLog.setContent("admin " + content);
        taskLog.setCreateTime(System.currentTimeMillis());
        taskLogService.saveTaskLog(taskLog);
        TaskLogVO taskLogVO = taskLogService.findTaskLogContentById(taskLog.getId());
        return taskLogVO;
    }

    /**
     * 根据任务id 数组查询出多条任务信息
     * @param taskId 任务id数组
     * @return
     */
    @Override
    public List<Task> findManyTask(String[] taskId) {
        return taskMapper.findManyTask(taskId);
    }

    /**
     * 转换子任务为顶级任务
     * @param task 包含任务的id,名称
     * @return
     */
    @Override
    public TaskLogVO turnToFatherLevel(Task task) {
        //将任务的父级任务设置为0 (没有父级任务)
        task.setParentId("0");
        //设置更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //更新任务信息
        int result = taskMapper.updateTask(task);
        StringBuilder content = new StringBuilder("");
        //拼接日志内容
        content.append(TaskLogFunction.A8.getName()).append(" ").append(task.getTaskName()).append(" ").append(TaskLogFunction.A9.getName());
        //保存日志
        TaskLogVO taskLogVO = saveTaskLog(task, content.toString());
        taskLogVO.setResult(result);
        return taskLogVO;
    }

    /**
     * 绑定标签到当前任务
     * @param tag 标签的实体信息
     * @param taskId 当前任务的id
     * @param countByTagName 判断要绑定到任务上的标签是不是已经存在
     * @return
     */
    @Override
    public TaskLogVO addTaskTags(Tag tag,String taskId,int countByTagName) {
        //先查询出当前任务原有的标签id信息
        String taskTag = taskMapper.findTaskTagByTaskId(taskId);
        //将原有标签id和新添加的标签id拼接在一起存入数据库
        StringBuilder newTaskTag = new StringBuilder();
        newTaskTag.append(taskTag).append(tag.getTagId()).append(",");
        Task task = new Task();
        task.setTaskId(taskId);
        //设置最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        task.setTagId(newTaskTag.toString());
        //更新到数据库
        int result = taskMapper.updateTask(task);
        TaskLogVO taskLogVO = new TaskLogVO();
        //判断 如果是向数据库新插入了标签 则保存日志 否则不保存
        if(countByTagName == 0){
            //拼接任务操作日志内容
            StringBuilder content = new StringBuilder("");
            content.append(TaskLogFunction.A10.getName()).append(" ").append(tag.getTagName());
            taskLogVO = saveTaskLog(task, content.toString());
            taskLogVO.setResult(result);
        }
        return taskLogVO;
    }

    /**
     * 移除该任务上的标签
     * @param tags 当前任务上绑定的所有标签对象数组
     * @param tag 当前要被的标签对象
     * @param taskId 当前任务uid
     * @return
     */
    @Override
    public int removeTaskTag(Tag[] tags, Tag tag, String taskId) {
        StringBuilder taskTagsId = new StringBuilder();
        for (int i = 0; i < tags.length ; i++) {
            if(tags[i].getTagId().equals(tag.getTagId())){
                tags[i] = null;
                continue;
            }
            taskTagsId.append(tags[i].getTagId()).append(",");
        }
        System.out.println(taskTagsId.toString());
        Task task = new Task();
        task.setTagId(taskTagsId.toString());
        task.setTaskId(taskId);
        task.setUpdateTime(System.currentTimeMillis());
        int result = taskMapper.updateTask(task);
        return result;
    }

    /**
     * 更新任务的重复规则
     * @param task 任务的实体信息
     * @param object 时间重复周期的具体信息 (未设定)
     * @return
     */
    @Override
    public TaskLogVO updateTaskRepeat(Task task, Object object) {
        //判断是不是自定义重复
        if(!task.getRepeat().equals("自定义重复")){
            //如果不是自定义重复删除该任务的自定义重复时间
        }
        task.setUpdateTime(System.currentTimeMillis());
        int result = taskMapper.updateTask(task);
        String content = TaskLogFunction.D.getName();
        TaskLogVO taskLogVO = saveTaskLog(task, content);
        taskLogVO.setResult(result);
        return taskLogVO;
    }

    /**
     * 更新任务的提醒时间
     * @param task 任务实体信息
     * @param userEntity 用户实体信息
     * @return
     */
    @Override
    public TaskLogVO updateTaskRemindTime(Task task, UserEntity userEntity) {
        //判断是开始时提醒还是结束时提醒
        if(task.getRemind().equals("任务截止时提醒")){
           task.setRepetitionTime(task.getEndTime());
        }
        if(task.getRemind().equals("任务开始时提醒")){
            task.setRepetitionTime(task.getStartTime());
        }
        task.setUpdateTime(System.currentTimeMillis());
        String content = TaskLogFunction.M.getName();
        int result = taskMapper.updateTask(task);
        TaskLogVO taskLogVO = saveTaskLog(task, content);
        taskLogVO.setResult(result);
        return taskLogVO;
        //UserEntity是要被提醒的成员信息(暂时先不用)
    }

    /**
     * 清除任务的开始时间和结束时间
     * @param task 任务的实体信息
     * @return
     */
    @Override
    public TaskLogVO removeTaskStartAndEndTime(Task task) {
        StringBuilder content = new StringBuilder("");
        int result = 0;
        //设置最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //如果开始时间不为空 则清空开始时间
        if(task.getStartTime() != null){
            result = taskMapper.removeTaskStartTime(task);
            content.append(TaskLogFunction.J.getName());
        }
        //如果截止时间不为空 则清空截止时间
        if(task.getEndTime() != null){
            result = taskMapper.removeTaskEndTime(task);
            content.append(TaskLogFunction.K.getName());
        }
        //保存操作日志
        TaskLogVO taskLogVO = saveTaskLog(task, content.toString());
        taskLogVO.setResult(result);
        return taskLogVO;
    }

    /**
     * 添加项目成员
     * @param task 任务实体信息
     * @param userEntity 多个用户的信息
     * @return
     */
    @Override
    public TaskLogVO addTaskMember(Task task, UserEntity[] userEntity) {
        //向任务成员表中添加数据
        int result = taskMemberService.addManyMemberInfo(userEntity,task);
        StringBuilder content = new StringBuilder("");
        content.append(TaskLogFunction.C.getName()).append(" ");
        //循环用来拼接log日志字符串
        for (int i = 0; i < userEntity.length; i++) {
            if(i == userEntity.length - 1){
                content.append(userEntity[i].getUserName());
            } else{
                content.append(userEntity[i].getUserName()).append(",");
            }
        }
        TaskLogVO taskLogVO = saveTaskLog(task, content.toString());
        taskLogVO.setResult(result);
        return taskLogVO;
    }

    /**
     * 移除任务-成员关系
     * @param task 当前项目实体信息
     * @param userEntity 被移除的用户的信息
     * @return
     */
    @Override
    public TaskLogVO removeTaskMember(Task task, UserEntity[] userEntity) {
        return taskMemberService.delTaskMemberByTaskIdAndMemberId(task,userEntity);
    }


}