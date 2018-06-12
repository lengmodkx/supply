package com.art1001.supply.service.task.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.project.Project;
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
        //更新项目内容
        if(task.getTaskName() != null && task.getTaskName() != ""){
            taskLogVO = saveTaskLog(task,TaskLogFunction.T.getName());
        }
        //更新项目优先级
        if(task.getPriority() != null && task.getPriority() != ""){
            taskLogVO = saveTaskLog(task,TaskLogFunction.F.getName());
        }
        //更新项目重复规则
        if((task.getRepeat() != "" && task.getRepeat() != null) || (task.getRepetitionTime() != null)){
            taskLogVO = saveTaskLog(task,TaskLogFunction.D.getName());
        }
        //更新项目备注
        if(task.getRemarks() != null && task.getRemarks() != null){
            taskLogVO = saveTaskLog(task,TaskLogFunction.E.getName());
        }
        //更新项目执行者
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
	public TaskLogVO saveTask(String[] memberId, Project project, Task task) {
        //将任务的参与者信息保存至 (任务-参与者 [task_member] ) 关系表中
        taskMemberService.saveManyTaskeMmber(memberId,task.getMemberId());
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
    public TaskLogVO updateTaskTime(Task task) {
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

}