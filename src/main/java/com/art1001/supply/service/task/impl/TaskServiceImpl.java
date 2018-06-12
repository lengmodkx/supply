package com.art1001.supply.service.task.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskLog;
import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
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
	public int updateTask(Task task){
	    String content = "";
        task.setUpdateTime(System.currentTimeMillis());
        if(task.getTaskName() != null && task.getTaskName() != ""){
            content = TaskLogFunction.T.getName();
        }
        if(task.getPriority() != null && task.getPriority() != ""){
            content = TaskLogFunction.F.getName();
        }
        if((task.getRepeat() != "" && task.getRepeat() != null) || (task.getRepetitionTime() != null)){
            content = TaskLogFunction.D.getName();
        }
        if(task.getRemarks() != null && task.getRemarks() != null){
            content = TaskLogFunction.E.getName();
        }
        if(task.getExecutor() != null && task.getExecutor() != ""){
            content = TaskLogFunction.U.getName();
        }
        if(task.getTaskMenuId() != null && task.getTaskMenuId() != ""){
            //content = TaskLogFunction
        }
        int result = taskMapper.updateTask(task);
        taskLogService.saveTaskLog(getTaskLog(task,content));
        return result;
	}

	/**
	 * 重写方法
	 * 将添加的任务信息保存至数据库
     * @param memberId 该任务的参与者
     * @param project 当前项目的实体信息
     * @param task task信息
     */
	@Override
	public void saveTask(String[] memberId, Project project, Task task) {
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
        taskLogService.saveTaskLog(getTaskLog(task,TaskLogFunction.R.getName()));
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
    public int moveToRecycleBin(String taskId, String taskDel) {
        int result = taskMapper.moveToRecycleBin(taskId,taskDel,System.currentTimeMillis());
        Task task = new Task();
        task.setTaskId(taskId);
        //如果任务状态为0 日志打印内容为 xxx把任务移入了回收站 否则   xxx恢复了任务
        if(taskDel == "0"){
            taskLogService.saveTaskLog(getTaskLog(task,TaskLogFunction.P.getName()));
        } else{
            taskLogService.saveTaskLog(getTaskLog(task,TaskLogFunction.O.getName()));
        }
        return result;
    }

	/**
	 * 重写方法
	 * 修改当前任务状态
	 * @param taskId 任务id
	 * @return
	 */
	@Override
	public int changeTaskStatus(String taskId,String taskStatus) {
	    //修改任务状态
	    int result = taskMapper.changeTaskStatus(taskId,taskStatus,System.currentTimeMillis());
        Task task = new Task();
        task.setTaskId(taskId);
        //如果当前状态为未完成  则 日志记录为 完成任务 否则为 重做任务
        if(taskStatus == "1"){
	        taskLogService.saveTaskLog(getTaskLog(task,TaskLogFunction.S.getName()));
        } else{
            taskLogService.saveTaskLog(getTaskLog(task,TaskLogFunction.Q.getName()));
        }
	    return result;
	}

    /**
     * 重写方法
     * 设定任务的时间(开始 / 结束)
     * @param task 任务时间的信息
     * @return
     */
    @Override
    public int updateTaskTime(Task task) {
        String content = "";
        if(task.getStartTime() != null){
            content = TaskLogFunction.M.getName();
        }
        if(task.getEndTime() != null){
            content = TaskLogFunction.L.getName();
        }
        int result =  taskMapper.updateTask(task);
        taskLogService.saveTaskLog(getTaskLog(task,content));
        return result;
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
     * 返回日志实体对象
     */
    public TaskLog getTaskLog(Task task,String content){
        TaskLog taskLog = new TaskLog();
        taskLog.setId(IdGen.uuid());
        taskLog.setMemberId(task.getMemberId());
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
        return taskLog;
    }

}